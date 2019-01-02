package it.unimib.disco.bigtwine.config;

import it.unimib.disco.bigtwine.nel.Linker;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Properties specific to Nel.
 * <p>
 * Properties are configured in the application.yml file.
 * See {@link io.github.jhipster.config.JHipsterProperties} for a good example.
 */
@ConfigurationProperties(prefix = "application", ignoreUnknownFields = false)
public class ApplicationProperties {
    private String defaultLinker = ApplicationDefaults.defaultLinker;
    private final Executors executors = new Executors();
    private final Processors processors = new Processors();

    public static class Processors {

    }
    public static class Executors {

    }

    public String getDefaultLinker() {
        return defaultLinker;
    }

    public void setDefaultLinker(String defaultLinker) {
        this.defaultLinker = defaultLinker;
        Linker linker = Linker.valueOf(defaultLinker);
        Linker.setDefault(linker);
    }

    public Executors getExecutors() {
        return this.executors;
    }

    public Processors getProcessors() {
        return this.processors;
    }
}
