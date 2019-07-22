package it.unimib.disco.bigtwine.services.nel.processors;

import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.ProcessorListener;
import it.unimib.disco.bigtwine.commons.processors.file.FileProcessor;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParser;
import it.unimib.disco.bigtwine.services.nel.parsers.OutputParserBuilder;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducer;
import it.unimib.disco.bigtwine.services.nel.producers.InputProducerBuilder;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

public abstract class NelFileProcessor implements Processor, FileProcessor<RecognizedTweet> {

    protected OutputParserBuilder outputParserBuilder;
    protected InputProducerBuilder inputProducerBuilder;
    protected String processorId;
    protected File workingDirectory;
    protected File inputDirectory;
    protected File outputDirectory;
    protected ProcessorListener<LinkedTweet> processorListener;

    public NelFileProcessor(InputProducerBuilder inputProducerBuilder, OutputParserBuilder outputParserBuilder) {
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
    public boolean process(String tag, RecognizedTweet tweet) {
        return this.process(tag, new RecognizedTweet[]{tweet});
    }

    @Override
    public boolean generateInputFile(File inputFile, RecognizedTweet[] tweets) {
        File tmpFile;
        try {
            tmpFile = File.createTempFile(inputFile.getName(), ".tmp", inputFile.getAbsoluteFile().getParentFile());
        } catch (IOException e) {
            return false;
        }
        FileWriter fileWriter;

        try {
            fileWriter = new FileWriter(tmpFile);
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

        try {
            Files.move(tmpFile.toPath(), inputFile.toPath());
        } catch (IOException | SecurityException e) {
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
