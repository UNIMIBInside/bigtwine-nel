package it.unimib.disco.bigtwine.nel.executors;

import com.github.dockerjava.api.command.CreateContainerCmd;
import com.github.dockerjava.api.model.Bind;
import com.github.dockerjava.api.model.HostConfig;
import com.github.dockerjava.api.model.Volume;
import it.unimib.disco.bigtwine.commons.executors.DockerExecutor;
import it.unimib.disco.bigtwine.commons.executors.SyncFileExecutor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class Mind2016DockerExecutor extends DockerExecutor implements SyncFileExecutor {
    public static final String DOCKER_IMAGE = "bigtwine-tool-nel";

    private String inputPath;
    private String outputPath;
    private String kbPath;

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
        return Arrays.asList("java", "-jar", "NEEL_Linking.jar", "/data/input", this.kbPath, "/data/output");
    }

    @Override
    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    @Override
    public String getInputPath() {
        return inputPath;
    }

    @Override
    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    @Override
    public String getOutputPath() {
        return outputPath;
    }

    public String getKnowledgeBasePath() {
        return this.kbPath;
    }

    public void setKnowledgeBasePath(String knowledgeBasePath) {
        this.kbPath = knowledgeBasePath;
    }

    @Override
    protected CreateContainerCmd createContainer(String image, List<String> args) {
        return super.createContainer(image, args)
            .withHostConfig(HostConfig.newHostConfig().withBinds(
                new Bind(this.getInputPath(), new Volume("/data/input")),
                new Bind(this.getOutputPath(), new Volume("/data/output"))
            ));
    }
}
