package it.unimib.disco.bigtwine.service;

import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import org.springframework.stereotype.Service;

@Service
public class NelService implements ProcessorListener<LinkedTweet> {
    @Override
    public void onProcessed(GenericProcessor processor, String tag, LinkedTweet[] processedItems) {
        // TODO: Implement this
    }
}
