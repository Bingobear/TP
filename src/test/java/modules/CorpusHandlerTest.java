package modules;

import com.cybozu.labs.langdetect.LangDetectException;
import models.Corpus;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by simonbruns on 16/03/16.
 */
public class CorpusHandlerTest {
    @Test
    public void testCreateCorpus() throws LangDetectException, IOException, InvalidPDF {
        String testPDFlocation ="text";
        CorpusHandler corpusHandler = new CorpusHandler();
        Corpus corpus = corpusHandler.createCorpus(testPDFlocation);
        assertEquals(2, corpus.getPdfList().size());
    }
}