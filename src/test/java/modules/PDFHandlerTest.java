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
        String testPDFlocation ="text";
        PDFHandler pdfh = new PDFHandler();
        Corpus corpus = pdfh.createCorpus(testPDFlocation);
        assertEquals(2, corpus.getPdfList().size());
    }

    @Test
    public void testCreatePDF() throws LangDetectException, IOException, InvalidPDF {
        PDFHandler pdfHandler = new PDFHandler();
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        pdfHandler.setTitles("text");
        PDF pdf = pdfHandler.createPDF(fileEntry);
        assertEquals(8, pdf.getPagecount());
    }

    @Test(expected = InvalidPDF.class)
    public void testCreatePDFFromPresentation() throws LangDetectException, IOException, InvalidPDF {
        PDFHandler pdfHandler = new PDFHandler();
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/tbp.pdf").getFile());
        ArrayList<PDF> pdfList = new ArrayList<PDF>();
        pdfHandler.setTitles("text");
        PDF pdf = pdfHandler.createPDF(fileEntry);
        assertEquals(12, pdf.getPagecount());
    }

    @Test
    public void testgetTitle() throws IOException {
        PDFHandler pdfHandler = new PDFHandler();
        ClassLoader classLoader = getClass().getClassLoader();
        pdfHandler.setTitles("text");
        String title = pdfHandler.getTitle("schaar_06038875");

        assertEquals("Smart Clothing. Perceived Benefits vs. Perceived Fears", title);
    }

}
