import com.cybozu.labs.langdetect.LangDetectException;
import models.Corpus;
import modules.PDFHandler;

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
        PDFHandler app = new PDFHandler();
        String pdfLocation = "text";
        if (debug_extractor) {
            try {

                Corpus corpus = app.createCorpus(pdfLocation);
            } catch (LangDetectException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }
}
