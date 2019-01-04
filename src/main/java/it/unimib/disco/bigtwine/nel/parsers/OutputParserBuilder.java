package it.unimib.disco.bigtwine.nel.parsers;

import it.unimib.disco.bigtwine.commons.csv.CSVFactory;
import it.unimib.disco.bigtwine.nel.Linker;
import javafx.util.Builder;

import java.io.*;

public class OutputParserBuilder implements Builder<OutputParser> {
    private Linker linker;

    private Reader reader;

    public static OutputParserBuilder getDefaultBuilder() {
        return new OutputParserBuilder();
    }

    public OutputParserBuilder setLinker(Linker linker) {
        this.linker = linker;
        return this;
    }

    public Linker getLinker() {
        return linker;
    }

    public Reader getReader() {
        return reader;
    }

    public OutputParserBuilder setReader(Reader reader) {
        this.reader = reader;
        return this;
    }

    public void setInput(String string) {
        this.reader = new StringReader(string);
    }

    public OutputParserBuilder setInput(File file) {
        try {
            this.reader = new FileReader(file);
        } catch (FileNotFoundException e) {
            this.reader = null;
        }
        return this;
    }

    @Override
    public OutputParser build() {
        if (this.linker == null) {
            return null;
        }

        if (this.reader == null) {
            return null;
        }

        OutputParser outputParser;
        switch (this.linker) {
            case mind2016:
                outputParser = new Mind2016OutputParser(CSVFactory.getFactory());
                break;
            default:
                return null;
        }

        outputParser.setReader(this.reader);

        return outputParser;
    }
}
