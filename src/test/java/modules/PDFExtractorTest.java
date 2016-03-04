package modules;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import models.PDF;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class PDFExtractorTest {

	@Test
	public void testParsePDFtoKey() throws LangDetectException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		PDFExtractor extract = new PDFExtractor();
		assertEquals(2062,extract.parsePDFtoKey(fileEntry, true, pdfList).size());
	}

	@Test
	public void testParsePrestoKey() throws LangDetectException, IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		File fileEntry =new File(classLoader.getResource("text/tbp.pdf").getFile());
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		PDFExtractor extract = new PDFExtractor();
		assertEquals(0,extract.parsePDFtoKey(fileEntry, true, pdfList).size());
	}
	
	@Test(expected=NullPointerException.class)
	public void testParseNoPDFtoKey() throws LangDetectException, IOException {
		File fileEntry = null;
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		PDFExtractor extract = new PDFExtractor();
		assertEquals(2062,extract.parsePDFtoKey(fileEntry, true, pdfList).size());
	}
	
}
