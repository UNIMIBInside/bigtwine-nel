package it.unimib.disco.bigtwine.messaging;

import org.springframework.cloud.stream.annotation.Input;
import org.springframework.messaging.SubscribableChannel;

public interface NelRequestsConsumerChannel {
    String CHANNEL = "nelRequestsChannel";

    @Input
    SubscribableChannel nelRequestsChannel();
}
