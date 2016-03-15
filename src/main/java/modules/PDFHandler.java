package modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.text.Normalizer;
import java.util.ArrayList;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import models.Corpus;
import models.PDF;
import models.WordOcc;
import models.Words;
import Util.NLPUtil;

import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Main Interface to initiate Textmining (pdf extractor)
 *
 * @author Simon Bruns
 */
public class PDFHandler {
    // debug modes
    private static boolean debug_extractor = true;
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
     * Initiates corpus text mining - ranking
     *
     * @param args
     */
    public static void main(String[] args) {
        // BasicConfigurator.configure();
        PDFHandler app = new PDFHandler();
        String pdfLocation = "text";
        if (debug_extractor) {
            try {

                corpus = app.createCorpus(pdfLocation);
            } catch (LangDetectException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

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
            corpus.filterTFICF(0.00001);
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
        // Not necessary, but for the cases when no corresponding publication is
        // available
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                generatePDF(fileEntry);

            } else if (fileEntry.isDirectory()) {
                fillCorpus(fileEntry);
            }
        }
        return corpus;
    }

    private void generatePDF(File fileEntry) throws LangDetectException {
        PDF pdf;
        try {
            pdf = createPDF(fileEntry);
            pdf.setFilename(fileEntry.getName());
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

        ArrayList<Words> words = new ArrayList<Words>();
        words = extractor.parsePDFtoKey(fileEntry, corpus.getPdfList());
        if (words.size() > 0) {

            ArrayList<WordOcc> occ = NLPUtil.keyOcc(words);

            PDF pdf = new PDF(occ, extractor.getLang(),
                    extractor.getWordcount(), extractor.getTitlePage());
            pdf.setGenericKeywords(extractor.getKeywords());

            pdf.setCatnumb(extractor.getCatnumb());
            // RUDEMENTARY TITLE EXTRACTION VIA FILE
            pdf.setTitle(getTitle(fileEntry.getName()));

            // No keywords -> not valid pdf
            if (!pdf.getGenericKeywords().isEmpty()) {
                pdf.setFilename(fileEntry.getName());
                pdf.setPagecount(extractor.getPagenumber());
                return pdf;
            }
        }
        throw new InvalidPDF();
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
        String[] helper = null;
        Reader in = new FileReader(csvFile);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.parse(in);
        for (CSVRecord record : records) {
            line = record.get(0);
            helper = line.split(cvsSplitBy);
            //	System.out.println(helper);
            for (int counter = 0; counter < helper.length; counter++) {
                titles.add(helper[counter]);
            }
        }
        return titles;
    }


}
