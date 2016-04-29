package modules;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Created by simonbruns on 29/04/16.
 */
public class PDFConverterTest {

    @Test
    public void testParseNPages() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDFConverter pdfConverter = new PDFConverter(fileEntry);
        assertEquals(5748,pdfConverter.parseNPages(0,1).length());
    }

    @Test
    public void testGetPageNumber() throws Exception {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry =new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDFConverter pdfConverter = new PDFConverter(fileEntry);
        assertEquals(8,pdfConverter.getPageNumber());

    }

    @Test
    public void testClose() throws Exception {

    }
}