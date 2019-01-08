package it.unimib.disco.bigtwine.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;
import it.unimib.disco.bigtwine.config.ApplicationProperties;
import it.unimib.disco.bigtwine.nel.Linker;
import it.unimib.disco.bigtwine.nel.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.nel.producers.InputProducerBuilder;
import org.springframework.beans.factory.FactoryBean;


public class ProcessorFactory implements FactoryBean<Processor> {

    private Linker linker;
    private ExecutorFactory executorFactory;
    private ApplicationProperties.Processors processorsProps;

    public ProcessorFactory(ApplicationProperties.Processors processorsProps, ExecutorFactory executorFactory) {
        this.executorFactory = executorFactory;
        this.processorsProps = processorsProps;
    }

    public void setLinker(Linker linker) {
        this.linker = linker;
    }

    public Linker getLinker() {
        return this.linker;
    }

    protected Processor getMind2016Processor() throws Exception {
        return new Mind2016Processor(
            (SyncFileExecutor) this.executorFactory.getExecutor(this.linker),
            InputProducerBuilder.getDefaultBuilder(),
            OutputParserBuilder.getDefaultBuilder());
    }

    public Processor getProcessor() throws Exception {
        if (this.linker == null) {
            throw new IllegalArgumentException("linker not set");
        }

        switch (linker) {
            case mind2016:
                return this.getMind2016Processor();
            default:
                return null;
        }
    }

    public Processor getProcessor(Linker linker) throws Exception {
        this.setLinker(linker);
        return this.getProcessor();
    }

    public Processor getDefaultProcessor() throws Exception {
        return this.getProcessor(Linker.getDefault());
    }

    @Override
    public Processor getObject() throws Exception {
        return this.getProcessor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.linker == null) {
            return null;
        }

        switch (linker) {
            case mind2016:
                return Mind2016Processor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
