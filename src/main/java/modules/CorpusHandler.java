package modules;

import com.cybozu.labs.langdetect.LangDetectException;
import models.Corpus;
import models.PDF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by simonbruns on 16/03/16.
 *TODO Remove statics
 */
public class CorpusHandler {
    public static final double TFICF_THRESHOLD = 0.00001;
    // debug modes

    private PDFHandler pdfHandler;
    private static boolean debug_calc = false;
    private static File folder;
    private TitleHandler titleHandler;

    private Corpus corpus;

    public void setCorpus(Corpus newcorpus) {
        corpus = newcorpus;
    }


    public CorpusHandler() {
        corpus = new Corpus();
        titleHandler = new TitleHandler();
        pdfHandler = new PDFHandler();
    }


    /**
     * Main text mining method return parsed/calculated corpus (containing all
     * pdfs)
     *
     * @return corpus
     * @throws LangDetectException
     * @throws IOException
     */
    public Corpus createCorpus(String pdfLocation) throws LangDetectException, IOException, InvalidPDF {
        setFolder(pdfLocation);
        titleHandler.initializeKnownTitles(pdfLocation);
        corpus = fillCorpus(folder);
        if (debug_calc) {
            corpus.calculateIdf();
            corpus.setPdfList(corpus.calculateTD_IDF(corpus.getPdfList()));

            corpus.initializeTFICFCalc();
            corpus.filterTFICF(TFICF_THRESHOLD);
            corpus.calculateAllPDFCatRel();
        }
        return corpus;

    }

    private void setFolder(String pdfLocation) {
        ClassLoader classLoader = getClass().getClassLoader();
        folder = new File(classLoader.getResource(pdfLocation).getFile());
    }

    /**
     * Creates basic corpus -> text mining (word extraction,keyword,pdfs)
     *
     * @param folder
     * @return corpus
     * @throws LangDetectException
     * @throws IOException
     */
    public Corpus fillCorpus(File folder) throws LangDetectException, IOException {
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                try {
                    PDF pdf = pdfHandler.generatePDFContent(fileEntry);
                    pdf.setTitle(titleHandler.getTitle(fileEntry.getName()));
                    addPDF2Corpus(pdf);
                } catch (InvalidPDF invalidPDF) {
                    System.out.println(fileEntry.getName() + " was not valid PDF!");
                }


            } else if (fileEntry.isDirectory()) {
                if (isNotTitlesFolder(fileEntry)) {
                    fillCorpus(fileEntry);
                }
            }
        }
        return corpus;
    }


    private boolean isNotTitlesFolder(File fileEntry) {
        return !fileEntry.getName().contains("titles");
    }

    private void addPDF2Corpus(PDF pdf) {
        corpus.incDocN(pdf.getLanguage());
        corpus.associateWordswithCategory(pdf);
        ArrayList<PDF> pdfTemList = corpus.getPdfList();
        pdfTemList.add(pdf);
        corpus.setPdfList(pdfTemList);
    }

}
