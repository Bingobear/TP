package modules;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;

import models.Corpus;
import models.PDF;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class PDFHandlerTest {

	@Test
	public void testCreateCorpus() {
		String importData = "c:/RWTH/Data/test/";
		File folder = new File(importData);
		Corpus corpus = new Corpus();
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		boolean first = true;
		PDFHandler pdfh = new PDFHandler();
		try {
			corpus = pdfh.createCorpus(folder, corpus, pdfList, first);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(4, corpus.getPdfList().size());
	}

	@Test
	public void testCreatePDF() {
		PDFHandler pdfh = new PDFHandler();
		String importData = "c:/RWTH/Data/test/himmel_et_al-older-users-wishlist.pdf";
		File fileEntry = new File(importData);
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		File hack = new File(".");
		String home = hack.getAbsolutePath();
		String importtitle = home + "/importData/pdftitleo.csv";
		ArrayList<String> titles = pdfh.readCSVTitle(importtitle);
		PDF pdf = new PDF();
		try {
			pdf = pdfh.createPDF(fileEntry, true, pdfList, titles);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(12, pdf.getPagecount());
	}

	@Test
	public void testgetTitle() {
		PDFHandler pdfh = new PDFHandler();
		File hack = new File(".");
		String home = hack.getAbsolutePath();
		String importtitle = home + "/importData/pdftitleo.csv";
		ArrayList<String> titles = pdfh.readCSVTitle(importtitle);
		String title = "";

		title = pdfh.getTitle("himmel_et_al-older-users-wishlist", titles);

		assertEquals(
				"Older Users’ Wish List for Technology Attributes: A Comparison of Household and Medical Technologies",
				title);
	}

}
