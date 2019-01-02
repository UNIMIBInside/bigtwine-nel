package it.unimib.disco.bigtwine.nel.executors;

import it.unimib.disco.bigtwine.commons.executors.Executor;
import it.unimib.disco.bigtwine.config.ApplicationProperties;
import it.unimib.disco.bigtwine.nel.Linker;
import org.springframework.beans.factory.FactoryBean;

public class ExecutorFactory implements FactoryBean<Executor> {
    private Linker linker;
    private ApplicationProperties.Executors executorsProps;

    public ExecutorFactory(ApplicationProperties.Executors executorsProps) {
        this.executorsProps = executorsProps;
    }

    public void setLinker(Linker linker) {
        this.linker = linker;
    }

    public Linker getLinker() {
        return this.linker;
    }

    public Executor getExecutor(Linker linker) throws Exception {
        this.setLinker(linker);
        return this.getExecutor();
    }

    public Executor getExecutor() throws Exception {
        if (this.linker == null) {
            throw new IllegalArgumentException("linker not set");
        }

        switch (linker) {
            case mind2016:
                return new Mind2016DockerExecutor();
            default:
                return null;
        }
    }

    public Executor getDefaultExecutor() throws Exception {
        return this.getExecutor(Linker.getDefault());
    }

    @Override
    public Executor getObject() throws Exception {
        return this.getExecutor();
    }

    @Override
    public Class<?> getObjectType() {
        if (this.linker == null) {
            return null;
        }

        switch (linker) {
            case mind2016:
                return Mind2016DockerExecutor.class;
            default:
                return null;
        }
    }

    @Override
    public boolean isSingleton() {
        return false;
    }
}
