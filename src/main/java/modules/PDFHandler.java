package modules;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.Normalizer;
import java.util.ArrayList;

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
 * 
 */
public class PDFHandler {
	// debug modes
	static boolean debug_extractor = true;
	static boolean debug_calc = false;
	static String title = "";

	public PDFHandler() {

	}

	/**
	 * Initiates corpus text mining - ranking
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// BasicConfigurator.configure();
		@SuppressWarnings("unused")
		Corpus corpus = null;
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

		String importData = "c:/RWTH/Data/test/";
		File folder = new File(importData);
		Corpus corpus = new Corpus();
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		boolean first = true;
		corpus = createCorpus(folder, corpus, pdfList, first);
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
	 * @param corpus
	 * @param pdfList
	 * @param first
	 * @return corpus
	 * @throws LangDetectException
	 */
	public Corpus createCorpus(File folder, Corpus corpus,
			ArrayList<PDF> pdfList, boolean first) throws LangDetectException {
		File hack = new File(".");
		String home = hack.getAbsolutePath();

		// Not necessary, but for the cases when no corresponding publication is
		// available
		String importtitle = home + "/importData/pdftitleo.csv";
		System.out.println(importtitle);
		ArrayList<String> titles = readCSVTitle(importtitle);
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isFile()) {

				System.out.println("File= " + folder.getAbsolutePath() + "\\"
						+ fileEntry.getName());

				PDF pdf;
				try {
					pdf = createPDF(fileEntry, first, corpus.getPdfList(),
							titles);
					// No keywords -> not valid pdf
					if (pdf != null) {
						if (!pdf.getGenericKeywords().isEmpty()) {
							pdf.setFilename(fileEntry.getName());
							pdfList.add(pdf);
							corpus.incDocN(pdf.getLanguage());
							corpus.setPdfList(pdfList);
							corpus.associateWordswithCategory(pdf);
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.out.println("File corrupted: "+fileEntry);
					e.printStackTrace();
				}

			} else if (fileEntry.isDirectory()) {
				System.out.println("Change Current Folder!");
				createCorpus(fileEntry, corpus, pdfList, first);
			}
		}
		return corpus;
	}

	/***
	 * Generates the PDF with its corresponding informations from a given File
	 * 
	 * @param fileEntry
	 * @param first
	 * @param pdfList
	 * @param titles
	 * @return
	 * @throws LangDetectException
	 * @throws IOException
	 */
	public PDF createPDF(File fileEntry, boolean first, ArrayList<PDF> pdfList,
			ArrayList<String> titles) throws LangDetectException, IOException {
		PDFExtractor extractor = new PDFExtractor();

		ArrayList<Words> words = new ArrayList<Words>();
		words = extractor.parsePDFtoKey(fileEntry, first, pdfList);
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
		return null;
	}

	/**
	 * Maps the file names with the reference titles (csv). Work around for pdfs
	 * that have no corresponding publication within the library information
	 * 
	 * @param fileName
	 *            (fileEntry.getName())
	 * @param titles
	 * @return
	 */

	public String getTitle(String fileName, ArrayList<String> titles) {
		for (int ii = 0; ii < titles.size(); ii = ii + 2) {
			if (titles.get(ii).contains(fileName)) {
				System.out.println("FOUND:" + titles.get(ii + 1));
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
	 */
	ArrayList<String> readCSVTitle(String csvFile) {
		BufferedReader br = null;
		String line = "";
		String cvsSplitBy = ";";
		ArrayList<String> titles = new ArrayList<String>();
		String[] helper = null;

		try {

			br = new BufferedReader(new FileReader(csvFile));
			while ((line = br.readLine()) != null) {

				// use comma as separator

				helper = line.split(cvsSplitBy);
				System.out.println(helper);
				for (int counter = 0; counter < helper.length; counter++) {
					titles.add(helper[counter]);
				}

			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		System.out.println("Done");
		return titles;
	}

}
