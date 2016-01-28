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
		String importData = "c:/RWTH/Data/test/himmel_et_al-older-users-wishlist.pdf";
		File fileEntry = new File(importData);
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		PDFExtractor extract = new PDFExtractor();
		assertEquals(1359,extract.parsePDFtoKey(fileEntry, true, pdfList).size());
	}

	@Test
	public void testParsePrestoKey() throws LangDetectException, IOException {
		String importData = "c:/RWTH/Data/test/RS_ACCES_POSTER_Numerical_Shape_Optimization_of_Profile_Extrusion_Dies.pdf";
		File fileEntry = new File(importData);
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		PDFExtractor extract = new PDFExtractor();
		assertEquals(0,extract.parsePDFtoKey(fileEntry, true, pdfList).size());
	}
	
	@Test(expected=NullPointerException.class)
	public void testParseNoPDFtoKey() throws LangDetectException, IOException {
		File fileEntry = null;
		ArrayList<PDF> pdfList = new ArrayList<PDF>();
		PDFExtractor extract = new PDFExtractor();
		assertEquals(1359,extract.parsePDFtoKey(fileEntry, true, pdfList).size());
	}
	
}
