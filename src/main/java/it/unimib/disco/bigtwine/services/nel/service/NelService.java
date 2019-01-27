package it.unimib.disco.bigtwine.services.nel.service;

import it.unimib.disco.bigtwine.commons.messaging.NelRequestMessage;
import it.unimib.disco.bigtwine.commons.messaging.NelResponseMessage;
import it.unimib.disco.bigtwine.commons.messaging.RequestCounter;
import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.nel.messaging.NelRequestsConsumerChannel;
import it.unimib.disco.bigtwine.services.nel.messaging.NelResponsesProducerChannel;
import it.unimib.disco.bigtwine.services.nel.Linker;
import it.unimib.disco.bigtwine.services.nel.processors.Processor;
import it.unimib.disco.bigtwine.services.nel.processors.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class NelService implements ProcessorListener<LinkedTweet> {

    private final Logger log = LoggerFactory.getLogger(NelService.class);

    private MessageChannel channel;
    private ProcessorFactory processorFactory;
    private KafkaTemplate<Integer, String> kafka;
    private Map<Linker, Processor> processors = new HashMap<>();
    private Map<String, RequestCounter<NelRequestMessage>> requests = new HashMap<>();

    public NelService(
        NelResponsesProducerChannel channel,
        ProcessorFactory processorFactory,
        KafkaTemplate<Integer, String> kafka) {
        this.channel = channel.nelResponsesChannel();
        this.processorFactory = processorFactory;
        this.kafka = kafka;
    }

    private Linker getLinker(String linkerId) {
        if (linkerId != null) {
            linkerId = linkerId.trim();
            if (linkerId.equals("default")) {
                return Linker.getDefault();
            }else {
                try {
                    return Linker.valueOf(linkerId);
                }catch (IllegalArgumentException e) {
                    return null;
                }
            }
        }else {
            return Linker.getDefault();
        }
    }

    private Processor getProcessor(Linker linker) {
        Processor processor;
        if (this.processors.containsKey(linker)) {
            processor = this.processors.get(linker);
        }else {
            try {
                processor = this.processorFactory.getProcessor(linker);
            } catch (Exception e) {
                System.err.println("Cannot create processor");
                log.error("Cannot create processor");
                return null;
            }
            processor.setListener(this);
            boolean processorReady = processor.configureProcessor();
            if (processorReady) {
                this.processors.put(linker, processor);
            }else {
                System.err.println("Processor not ready: " + processor.getLinker().toString());
                log.error("Processor not ready: " + processor.getLinker().toString());
                return null;
            }
        }

        log.info("Processor ready: " + processor.getClass().toString());

        return processor;
    }

    private String getNewRequestTag() {
        return UUID.randomUUID().toString();
    }

    private void processRequest(NelRequestMessage request) {
        Linker linker = this.getLinker(request.getLinker());

        if (linker == null) {
            return;
        }

        Processor processor = this.getProcessor(linker);

        if (processor == null) {
            return;
        }

        String tag = this.getNewRequestTag();
        this.requests.put(tag, new RequestCounter<>(request, request.getTweets().length));
        processor.process(tag, request.getTweets());
    }

    private void sendResponse(Processor processor, String tag, LinkedTweet[] tweets) {
        if (!this.requests.containsKey(tag)) {
            log.debug("Request tagged '" + tag + "' expired");
            return;
        }

        RequestCounter<NelRequestMessage> requestCounter = this.requests.get(tag);
        requestCounter.decrement(tweets.length);
        NelRequestMessage request = requestCounter.get();
        if (!requestCounter.hasMore()) {
            this.requests.remove(tag);
        }

        NelResponseMessage response = new NelResponseMessage();
        response.setLinker(processor.getLinker().toString());
        response.setTweets(tweets);
        response.setRequestId(tag);

        MessageBuilder<NelResponseMessage> messageBuilder = MessageBuilder
            .withPayload(response);

        if (request.getOutputTopic() != null) {
            messageBuilder.setHeader(KafkaHeaders.TOPIC, request.getOutputTopic());
            this.kafka.send(messageBuilder.build());
        }else {
            this.channel.send(messageBuilder.build());
        }

        log.info("Request Processed: {}.", tag);
    }

    @StreamListener(NelRequestsConsumerChannel.CHANNEL)
    public void onNewRequest(NelRequestMessage request) {
        log.info("Request Received: {}.", request.getRequestId());
        this.processRequest(request);
    }

    @Override
    public void onProcessed(GenericProcessor processor, String tag, LinkedTweet[] tweets) {
        if (!(processor instanceof Processor)) {
            throw new AssertionError("Invalid processor type");
        }

        this.sendResponse((Processor)processor, tag, tweets);
    }
}
