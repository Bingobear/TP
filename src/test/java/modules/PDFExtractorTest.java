package modules;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import models.PDF;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class PDFExtractorTest {

    @Test(expected=InvalidPDF.class)
    public void testParsePrestoKey() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/tbp.pdf").getFile());
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        PDFExtractor extract = new PDFExtractor();
        assertEquals(0,extract.parsePDFtoKey(fileEntry).size());
    }

    @Test(expected=NullPointerException.class)
    public void testParseNoPDFtoKey() throws LangDetectException, IOException, InvalidPDF {
        File fileEntry = null;
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        PDFExtractor extract = new PDFExtractor();
        assertEquals(2062,extract.parsePDFtoKey(fileEntry).size());
    }

    @Test
    public void testParsePDFtoKey() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        PDFExtractor extract = new PDFExtractor();
        assertEquals(2062,extract.parsePDFtoKey(fileEntry).size());
    }

    @Test
    public void testgetPDFKeywords() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        PDFExtractor extract = new PDFExtractor();
        extract.parsePDFtoKey(fileEntry);
        assertEquals(6,extract.getCatnumb());
    }
}
