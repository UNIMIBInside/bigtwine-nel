package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.executors.*;
import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.commons.processors.file.SyncFileProcessor;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParser;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducer;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

public abstract class NelSyncFileProcessor implements Processor, SyncFileProcessor<RecognizedTweet> {

    protected SyncFileExecutor executor;
    protected OutputParserBuilder outputParserBuilder;
    protected InputProducerBuilder inputProducerBuilder;
    protected String processorId;
    protected File workingDirectory;
    protected File inputDirectory;
    protected File outputDirectory;
    protected ProcessorListener<LinkedTweet> processorListener;

    public NelSyncFileProcessor(SyncFileExecutor executor, InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
        this.setExecutor(executor);
        this.setInputProducerBuilder(inputProducerBuilder);
        this.setOutputParserBuilder(outputParserBuilder);
    }

    @Override
    public File getOutputDirectory() {
        return outputDirectory;
    }

    @Override
    public void setOutputDirectory(File outputDirectory) {
        this.outputDirectory = outputDirectory;
    }


    public InputProducerBuilder getInputProducerBuilder() {
        return this.inputProducerBuilder;
    }

    public void setInputProducerBuilder(InputProducerBuilder producerBuilder) {
        this.inputProducerBuilder = producerBuilder
            .setLinker(this.getLinker());
    }

    public OutputParserBuilder getOutputParserBuilder() {
        return this.outputParserBuilder;
    }

    public void setOutputParserBuilder(OutputParserBuilder outputParserBuilder) {
        this.outputParserBuilder = outputParserBuilder
            .setLinker(this.getLinker());
    }

    @Override
    public Executor getExecutor() {
        return this.executor;
    }

    @Override
    public void setExecutor(Executor executor) {
        if (!(executor instanceof SyncFileExecutor)) {
            throw new IllegalArgumentException("Unsupported executor type");
        }
        this.executor = (SyncFileExecutor)executor;
    }

    @Override
    public SyncExecutor getSyncExecutor() {
        return this.executor;
    }

    @Override
    public SyncFileExecutor getSyncFileExecutor() {
        return this.executor;
    }

    @Override
    public File getInputDirectory() {
        return this.inputDirectory;
    }

    @Override
    public void setInputDirectory(File inputDirectory) {
        this.inputDirectory = inputDirectory;
    }

    @Override
    public File getWorkingDirectory() {
        return this.workingDirectory;
    }

    @Override
    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    @Override
    public String getProcessorId() {
        return this.processorId;
    }

    @Override
    public void setListener(ProcessorListener<LinkedTweet> listener) {
        this.processorListener = listener;
    }

    @Override
    public boolean configureProcessor() {
        this.processorId = RandomStringUtils.randomAlphanumeric(16);
        this.inputDirectory = Paths.get(this.getWorkingDirectory().toString(), this.getProcessorId(), "input").toFile();
        this.outputDirectory = Paths.get(this.getWorkingDirectory().toString(), this.getProcessorId(), "output").toFile();

        return this.setupWorkingDirectory();
    }

    @Override
    public boolean process(String tag, RecognizedTweet tweet) {
        return this.process(tag, new RecognizedTweet[]{tweet});
    }

    @Override
    public boolean process(String tag, RecognizedTweet[] tweets) {
        File inputFile = this.makeInputFile(tag);
        File outputFile = this.makeOutputFile(tag);

        boolean res = this.generateInputFile(inputFile, tweets);

        if (!res) {
            return false;
        }

        try {
            if (!outputFile.createNewFile()) {
                return false;
            }
        } catch (IOException|SecurityException e) {
            return false;
        }

        res = this.getSyncFileExecutor().execute(inputFile, outputFile) != null;

        if (!res) {
            return false;
        }

        this.processOutputFile(outputFile);

        return true;
    }

    @Override
    public boolean generateInputFile(File inputFile, RecognizedTweet[] tweets) {
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(inputFile);
        } catch (IOException e) {
            return false;
        }

        InputProducer inputProducer = this.inputProducerBuilder
            .setLinker(this.getLinker())
            .setWriter(fileWriter)
            .build();

        if (inputProducer == null) {
            return false;
        }

        try {
            inputProducer.append(tweets);
            inputProducer.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    @Override
    public void processOutputFile(File outputFile) {
        OutputParser outputParser = this.outputParserBuilder
            .setLinker(this.getLinker())
            .setInput(outputFile)
            .build();

        if (outputParser == null) {
            return;
        }

        String tag = FilenameUtils.removeExtension(outputFile.getName());
        LinkedTweet[] tweets = outputParser.items();

        if (!tag.isEmpty() && this.processorListener != null && tweets != null) {
            this.processorListener.onProcessed(this, tag, tweets);
        }
    }
}
