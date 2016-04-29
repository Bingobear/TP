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
        PDFExtractor extract = new PDFExtractor();
        assertEquals(0,extract.parsePDF(fileEntry).size());
    }

    @Test(expected=NullPointerException.class)
    public void testParseNoPDFtoKey() throws LangDetectException, IOException, InvalidPDF {
        File fileEntry = null;
        PDFExtractor extract = new PDFExtractor();
        assertEquals(2062,extract.parsePDF(fileEntry).size());
    }

    @Test
    public void testParsePDFtoKey() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDFExtractor extract = new PDFExtractor();
        assertEquals(2062,extract.parsePDF(fileEntry).size());
    }

    @Test
    public void testgetPDFKeywords() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDFExtractor extract = new PDFExtractor();
        extract.parsePDF(fileEntry);
        assertEquals(6,extract.getCatnumb());
    }

    @Test
    public void testgetPDFLanguage() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDFExtractor extract = new PDFExtractor();
        extract.parsePDF(fileEntry);
        assertEquals("en",extract.getLang());
    }
}
