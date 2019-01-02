package it.unimib.disco.bigtwine.nel.processors;

import it.unimib.disco.bigtwine.commons.models.LinkedTweet;
import it.unimib.disco.bigtwine.commons.models.RecognizedTweet;
import it.unimib.disco.bigtwine.commons.processors.GenericProcessor;
import it.unimib.disco.bigtwine.nel.Linker;

public interface Processor extends GenericProcessor<RecognizedTweet, LinkedTweet> {

    Linker getLinker();

}
