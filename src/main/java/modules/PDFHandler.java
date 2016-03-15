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
    private ArrayList<String> titles;
    private static Corpus corpus;
    public PDFHandler() {

    }

    /**
     * Initiates corpus text mining - ranking
     *
     * @param args
     */
    public static void main(String[] args) {
        // BasicConfigurator.configure();
        corpus = null;
        PDFHandler app = new PDFHandler();
        if (debug_extractor) {
            try {
                corpus = app.parsePDFtoKey();
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
    public Corpus parsePDFtoKey() throws LangDetectException, IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        File folder = new File(classLoader.getResource("text").getFile());
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        corpus.setPdfList(pdfList);
        corpus = createCorpus(folder);
        if (debug_calc) {
            corpus.calculateIdf();
            corpus.setPdfList(corpus.calculateTD_IDF(corpus.getPdfList()));

            corpus.initializeTFICFCalc();
            corpus.filterTFICF(0.00001);
            corpus.calculateAllPDFCatRel();
        }
        return corpus;

    }

    /**
     * Creates basic corpus -> text mining (word extraction,keyword,pdfs)
     *
     * @param folder
     * @return corpus
     * @throws LangDetectException
     * @throws IOException
     */
    public Corpus createCorpus(File folder) throws LangDetectException, IOException {
        // Not necessary, but for the cases when no corresponding publication is
        // available
        ClassLoader classLoader = getClass().getClassLoader();
        String importtitle = classLoader.getResource("importData/pdftitleo.csv").getFile();
        ArrayList<String> titles = readCSVTitle(importtitle);
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                PDF pdf;
                try {
                    pdf = createPDF(fileEntry, corpus.getPdfList(),
                            titles);
                    // No keywords -> not valid pdf
                    if (pdf != null) {
                        if (!pdf.getGenericKeywords().isEmpty()) {
                            pdf.setFilename(fileEntry.getName());
                            corpus.incDocN(pdf.getLanguage());
                            corpus.associateWordswithCategory(pdf);
                            ArrayList<PDF> pdfTemList = corpus.getPdfList();
                            pdfTemList.add(pdf);
                            corpus.setPdfList(pdfTemList);
                        }
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
//					System.out.println("File corrupted: "+fileEntry);
//					e.printStackTrace();
                } catch (InvalidPDF e) {
                    // TODO Auto-generated catch block
//					e.printStackTrace();
                }

            } else if (fileEntry.isDirectory()) {
//				System.out.println("Change Current Folder!");
                createCorpus(fileEntry);
            }
        }
        return corpus;
    }

    /***
     * Generates the PDF with its corresponding informations from a given File
     *
     * @param fileEntry
     * @param pdfList
     * @param titles
     * @return
     * @throws LangDetectException
     * @throws IOException
     * @throws InvalidPDF
     */
    public PDF createPDF(File fileEntry, ArrayList<PDF> pdfList,
                         ArrayList<String> titles) throws LangDetectException, IOException, InvalidPDF {
        PDFExtractor extractor = new PDFExtractor();

        ArrayList<Words> words = new ArrayList<Words>();
        words = extractor.parsePDFtoKey(fileEntry, pdfList);
        if (words.size() > 0) {

            ArrayList<WordOcc> occ = NLPUtil.keyOcc(words);

            PDF pdf = new PDF(occ, extractor.getLang(),
                    extractor.getWordcount(), extractor.getTitlePage());
            pdf.setGenericKeywords(extractor.getKeywords());

            pdf.setCatnumb(extractor.getCatnumb());
            // RUDEMENTARY TITLE EXTRACTION VIA FILE
            pdf.setTitle(getTitle(fileEntry.getName(), titles));

            // No keywords -> not valid pdf
            if (!pdf.getGenericKeywords().isEmpty()) {
                pdf.setFilename(fileEntry.getName());
                pdf.setPagecount(extractor.getPagenumber());
                return pdf;
            }
        }
        throw new InvalidPDF();
    }

    /**
     * Maps the file names with the reference titles (csv). Work around for pdfs
     * that have no corresponding publication within the library information
     *
     * @param fileName (fileEntry.getName())
     * @param titles
     * @return
     */

    public String getTitle(String fileName, ArrayList<String> titles) {
        for (int ii = 0; ii < titles.size(); ii = ii + 2) {
            if (titles.get(ii).contains(fileName)) {
//				System.out.println("FOUND:" + titles.get(ii + 1));
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
//		try {
//
//			br = new BufferedReader(new FileReader(csvFile));
//			while ((line = br.readLine()) != null) {
//
//				// use comma as separator
//
//				helper = line.split(cvsSplitBy);
////				System.out.println(helper);
//				for (int counter = 0; counter < helper.length; counter++) {
//					titles.add(helper[counter]);
//				}
//
//			}
//
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			if (br != null) {
//				try {
//					br.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}

//		System.out.println("Done");
        return titles;
    }

}
