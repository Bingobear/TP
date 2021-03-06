import com.cybozu.labs.langdetect.LangDetectException;
import models.Corpus;
import modules.CorpusHandler;
import modules.InvalidPDF;

import java.io.IOException;

/**
 * Created by simonbruns on 15/03/16.
 */
public class InitializeCorpus {
    /**
     * Initiates corpus text mining - ranking
     *
     * @param args
     */
    private static boolean debug_extractor = true;
    public static void main(String[] args) {
        // BasicConfigurator.configure();
        CorpusHandler app = new CorpusHandler();
        String pdfLocation = "text";
        if (debug_extractor) {
            try {

                Corpus corpus = app.createCorpus(pdfLocation);
            } catch (LangDetectException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InvalidPDF invalidPDF) {
                invalidPDF.printStackTrace();
            }
        }

    }
}
