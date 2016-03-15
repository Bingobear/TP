package modules;

import java.io.*;
import java.lang.reflect.Array;
import java.text.Normalizer;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import models.*;
import Util.NLPUtil;

import com.cybozu.labs.langdetect.LangDetectException;
//TODO Split Class into PDFHANDLER & CORPUSHANDLER

/**
 * Main Interface to initiate Textmining (pdf extractor)
 *
 * @author Simon Bruns
 */
public class PDFHandler {
    public static final double TFICF_THRESHOLD = 0.00001;
    // debug modes
    private static boolean debug_calc = false;
    private static String title = "";
    private static File folder;
    private ArrayList<String> titles;

    public static void setCorpus(Corpus corpus) {
        PDFHandler.corpus = corpus;
    }

    private static Corpus corpus;

    public PDFHandler() {
        titles = new ArrayList<String>();
        corpus = new Corpus();
    }


    /**
     * Main text mining method return parsed/calculated corpus (containing all
     * pdfs)
     *
     * @return corpus
     * @throws LangDetectException
     * @throws IOException
     */
    public Corpus createCorpus(String pdfLocation) throws LangDetectException, IOException {
        setFolder(pdfLocation);
        setTitles(pdfLocation);
        corpus = new Corpus();
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
                generatePDFinCorpus(fileEntry);

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

    private void generatePDFinCorpus(File fileEntry) throws LangDetectException {
        PDF pdf;
        try {
            pdf = createPDF(fileEntry);
            addPDF2Corpus(pdf);

        } catch (IOException e) {
            // TODO Auto-generated catch block
        } catch (InvalidPDF e) {
            // TODO Auto-generated catch block
        }
    }

    private void addPDF2Corpus(PDF pdf) {
        corpus.incDocN(pdf.getLanguage());
        corpus.associateWordswithCategory(pdf);
        ArrayList<PDF> pdfTemList = corpus.getPdfList();
        pdfTemList.add(pdf);
        corpus.setPdfList(pdfTemList);
    }

    /***
     * Generates the PDF with its corresponding informations from a given File
     *
     * @param fileEntry
     * @return
     * @throws LangDetectException
     * @throws IOException
     * @throws InvalidPDF
     */
    public PDF createPDF(File fileEntry) throws LangDetectException, IOException, InvalidPDF {
        PDFExtractor extractor = new PDFExtractor();

        ArrayList<WordOcc> wordOccs = extractWordsAndOccs(fileEntry, extractor);
        PDF pdf = fillPDF(fileEntry, extractor, wordOccs);
        
        if (hasKeywords(pdf)) {
            return pdf;
        }
        throw new InvalidPDF();
    }

    private ArrayList<WordOcc> extractWordsAndOccs(File fileEntry, PDFExtractor extractor) throws LangDetectException, IOException, InvalidPDF {
        ArrayList<Words> words = new ArrayList<Words>();
        ArrayList<WordOcc> wordOccs = new ArrayList<WordOcc>();
        words = extractor.parsePDFtoKey(fileEntry, corpus.getPdfList());
        if (words.size() > 0) {
            wordOccs = NLPUtil.keyOcc(words);
        }
        return wordOccs;
    }

    private PDF fillPDF(File fileEntry, PDFExtractor extractor, ArrayList<WordOcc> occ) {
        PDF pdf = new PDF(occ, extractor.getLang(),
                extractor.getWordcount(), extractor.getTitlePage());
        pdf.setGenericKeywords(extractor.getKeywords());

        pdf.setCatnumb(extractor.getCatnumb());
        pdf.setTitle(getTitle(fileEntry.getName()));

        pdf.setFilename(fileEntry.getName());
        pdf.setPagecount(extractor.getPagenumber());
        return pdf;
    }

    private boolean hasKeywords(PDF pdf) {
        return pdf.getCatnumb() > 0;
    }

    public void setTitles(String location) throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String importtitle = classLoader.getResource(location + "/titles/pdftitleo.csv").getFile();
        titles = readCSVTitle(importtitle);
    }

    /**
     * Maps the file names with the reference titles (csv). Work around for pdfs
     * that have no corresponding publication within the library information
     *
     * @param fileName (fileEntry.getName())
     * @return
     */

    public String getTitle(String fileName) {
        for (int ii = 0; ii < titles.size(); ii = ii + 2) {
            if (titles.get(ii).contains(fileName)) {
                String titleNorm = Normalizer.normalize(titles.get(ii + 1),
                        Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                return titleNorm;
            }
        }
        return fileName;

    }

    /**
     * retrieve titles from external csv title file. External mapping exists for
     * unknown pdfs (titles) to the library file.
     *
     * @param csvFile
     * @return
     * @throws IOException
     */
    ArrayList<String> readCSVTitle(String csvFile) throws IOException {
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ";";
        ArrayList<String> titles = new ArrayList<String>();
        String[] titleEntries = null;
        Reader in = new FileReader(csvFile);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        for (CSVRecord record : records) {
            line = record.get(0);
            titleEntries = line.split(cvsSplitBy);
            for (String currentTitle : titleEntries) {
                titles.add(currentTitle);
            }
        }
        return titles;
    }
}
