package it.unimib.disco.bigtwine.config;


import it.unimib.disco.bigtwine.nel.executors.ExecutorFactory;
import it.unimib.disco.bigtwine.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.nel.processors.ProcessorFactory;
import it.unimib.disco.bigtwine.nel.producers.InputProducerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NelConfiguration {
    private ApplicationProperties appProps;

    public NelConfiguration(ApplicationProperties appProps) {
        this.appProps = appProps;
    }

    @Bean
    public ProcessorFactory getProcessorFactory() {
        return new ProcessorFactory(this.appProps.getProcessors(), this.getExecutorFactory());
    }

    @Bean
    public ExecutorFactory getExecutorFactory() {
        return new ExecutorFactory(this.appProps.getExecutors());
    }

    @Bean
    public InputProducerBuilder getInputProducerBuilder() {
        return InputProducerBuilder.getDefaultBuilder();
    }

    @Bean
    public OutputParserBuilder getOutputParserBuilder() {
        return OutputParserBuilder.getDefaultBuilder();
    }
}
