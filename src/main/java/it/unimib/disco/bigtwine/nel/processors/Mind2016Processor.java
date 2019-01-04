package it.unimib.disco.bigtwine.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.nel.Linker;

public final class Mind2016Processor implements Processor {
    @Override
    public Linker getLinker() {
        return null;
    }

    @Override
    public String getProcessorId() {
        return null;
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

    }

    @Override
    public boolean configureProcessor() {
        return false;
    }

    @Override
    public boolean process(String tag, RecognizedTweet item) {
        return false;
    }

    @Override
    public boolean process(String tag, RecognizedTweet[] items) {
        return false;
    }
}
