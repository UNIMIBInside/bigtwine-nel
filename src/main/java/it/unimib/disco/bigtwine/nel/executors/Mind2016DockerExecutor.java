package it.unimib.disco.bigtwine.nel.executors;

import it.unimib.disco.bigtwine.commons.executors.DockerExecutor;
import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;

import java.util.List;
import java.util.Map;

public class Mind2016DockerExecutor extends DockerExecutor implements SyncFileExecutor {
    public static final String DOCKER_IMAGE = "bigtwine-tool-nel";

    protected Mind2016DockerExecutor(String dockerImage) {
        super(dockerImage);
    }

    public Mind2016DockerExecutor() {
        this(DOCKER_IMAGE);
    }

    @Override
    public String getExecutorId() {
        return "docker-mind2016";
    }

    @Override
    public Map<String, Object> getExecutorConf() {
        // TODO: Implement this
        return null;
    }

    @Override
    public void setExecutorConf(Map<String, Object> conf) {
        // TODO: Implement this
    }

    @Override
    protected List<String> getArguments() {
        return null;
    }

    @Override
    public void setInputPath(String inputFile) {

    }

    @Override
    public String getInputPath() {
        return null;
    }

    @Override
    public void setOutputPath(String outputFile) {

    }

    @Override
    public String getOutputPath() {
        return null;
    }
}
