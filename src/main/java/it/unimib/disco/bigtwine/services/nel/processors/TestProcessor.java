package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.models.LinkedEntity;
import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.models.TextRange;
import it.unimib.disco.bigtwine.commons.models.dto.LinkedEntityDTO;
import it.unimib.disco.bigtwine.commons.models.dto.LinkedTweetDTO;
import it.unimib.disco.bigtwine.commons.models.dto.TextRangeDTO;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.services.nel.Linker;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class TestProcessor implements Processor {

    public static final Linker linker = Linker.test;

    private ProcessorListener<LinkedTweet> processorListener;

    private String[] links = new String[] {
        "http://dbpedia.org/resource/Lamar_Odom",
        "http://dbpedia.org/resource/Dow_Jones_&_Company",
        "http://dbpedia.org/resource/Aquarius_(constellation)",
        "http://dbpedia.org/resource/Karma",
        "http://dbpedia.org/resource/Lucas_Glover",
        "http://dbpedia.org/resource/Harry_Potter",
        "http://dbpedia.org/resource/Willis_McGahee",
        "http://dbpedia.org/resource/Denver",
        "http://dbpedia.org/resource/Ladbroke_Grove",
        "http://dbpedia.org/resource/Gruff_Rhys",
        "http://dbpedia.org/resource/Barack_Obama",
        "http://dbpedia.org/resource/Aquarius_(constellation)",
        "http://dbpedia.org/resource/Facebook",
        "http://dbpedia.org/resource/Pee-wee_Herman"
    };

    @Override
    public Linker getLinker() {
        return linker;
    }

    @Override
    public String getProcessorId() {
        return "test-processor";
    }

    @Override
    public void setExecutor(Executor executor) {

    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void setListener(ProcessorListener<LinkedTweet> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean configureProcessor() {
        return true;
    }

    @Override
    public boolean process(String tag, RecognizedTweet item) {
        return this.process(tag, new RecognizedTweet[]{item});
    }

    @Override
    public boolean process(String tag, RecognizedTweet[] items) {
        List<LinkedTweet> linkedTweets = new ArrayList<>();
        for (RecognizedTweet tweet : items) {
            int count = tweet.getEntities().length > 0 ? new Random().nextInt(tweet.getEntities().length) : 0;
            LinkedTweet lt = new LinkedTweetDTO(tweet.getId(), null);
            List<LinkedEntity> entities = new ArrayList<>();
            for (int i = 0; i < count; ++i)  {
                entities.add(new LinkedEntityDTO(
                    new TextRangeDTO(0, 1),
                    this.links[new Random().nextInt(this.links.length)],
                    1.0f,
                    "test",
                    false
                ));
            }
            lt.setEntities(entities.toArray(new LinkedEntityDTO[0]));
            linkedTweets.add(lt);
        }
        this.processorListener.onProcessed(this, tag, linkedTweets.toArray(new LinkedTweet[0]));
        return true;
    }
}
