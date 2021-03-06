package modules;

import Util.WordTypeFilter;
import com.cybozu.labs.langdetect.LangDetectException;
import models.Corpus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by simonbruns on 16/03/16.
 *TODO Remove statics
 */
public class CorpusHandler {
    private static final double TFICF_THRESHOLD = 0.00001;
    private final boolean debug_calc = false;
    private File folder;

    private TitleMatcher titleHandler;
    private Corpus corpus;

    public CorpusHandler() {
        corpus = new Corpus();
        titleHandler = new TitleMatcher();
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
        initPDFSourceFolder(pdfLocation);
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

    private void initPDFSourceFolder(String pdfLocation) {
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
                    PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
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

    private void addPDF2Corpus(PDF pdf) {
        corpus.incDocN(pdf.getLanguage());
        corpus.associateWordswithCategory(pdf);
        ArrayList<PDF> pdfTemList = corpus.getPdfList();
        pdfTemList.add(pdf);
        corpus.setPdfList(pdfTemList);
    }


    private boolean isNotTitlesFolder(File fileEntry) {
        return !fileEntry.getName().contains("titles");
    }

    public void setCorpus(Corpus newCorpus) {
        corpus = newCorpus;
    }

}
