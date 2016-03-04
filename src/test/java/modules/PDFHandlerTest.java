package modules;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import models.Corpus;
import models.PDF;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class PDFHandlerTest {

	@Test
	public void testCreateCorpus() throws LangDetectException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File folder = new File(classLoader.getResource("text").getFile());
		Corpus corpus = new Corpus();
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		boolean first = true;
		PDFHandler pdfh = new PDFHandler();
		corpus = pdfh.createCorpus(folder, corpus, pdfList, first);
		assertEquals(2, corpus.getPdfList().size());
	}

	@Test
	public void testCreatePDF() throws LangDetectException, IOException, InvalidPDF {
		PDFHandler pdfh = new PDFHandler();
		ClassLoader classLoader = getClass().getClassLoader();
		File fileEntry = new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		String importtitle = classLoader.getResource("importData/pdftitleo.csv").getFile();
		ArrayList<String> titles = pdfh.readCSVTitle(importtitle);
		PDF pdf = new PDF();
		pdf = pdfh.createPDF(fileEntry, true, pdfList, titles);
		assertEquals(8, pdf.getPagecount());
	}

	@Test(expected = InvalidPDF.class)
	public void testCreatePDFFromPresentation() throws LangDetectException, IOException, InvalidPDF {
		PDFHandler pdfh = new PDFHandler();
		ClassLoader classLoader = getClass().getClassLoader();
		File fileEntry = new File(classLoader.getResource("text/tbp.pdf").getFile());
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		String importtitle = classLoader.getResource("importData/pdftitleo.csv").getFile();
		ArrayList<String> titles = pdfh.readCSVTitle(importtitle);
		PDF pdf = new PDF();
		pdf = pdfh.createPDF(fileEntry, true, pdfList, titles);
		assertEquals(12, pdf.getPagecount());
	}

	@Test
	public void testgetTitle() throws IOException {
		PDFHandler pdfh = new PDFHandler();
		ClassLoader classLoader = getClass().getClassLoader();
		String importtitle = classLoader.getResource("importData/pdftitleo.csv").getFile();
		ArrayList<String> titles = pdfh.readCSVTitle(importtitle);
		String title = pdfh.getTitle("schaar_06038875", titles);

		assertEquals("Smart Clothing. Perceived Benefits vs. Perceived Fears", title);
	}

}
