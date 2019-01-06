package it.unimib.disco.bigtwine.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;
import it.unimib.disco.bigtwine.nel.Linker;
import it.unimib.disco.bigtwine.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.nel.producers.InputProducerBuilder;

public final class Mind2016Processor extends NelSyncFileProcessor {

    public static final Linker linker = Linker.mind2016;

    public Mind2016Processor(SyncFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        super(executor, inputProducerBuilder, outputParserBuilder);
    }

    @Override
    public Linker getLinker() {
        return linker;
    }
}
