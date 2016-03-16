package modules;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import models.PDF;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class PDFHandlerTest {


    @Test
    public void testCreatePDF() throws LangDetectException, IOException, InvalidPDF {
        PDFHandler pdfHandler = new PDFHandler();
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDF pdf = pdfHandler.generatePDFContent(fileEntry);
        assertEquals(8, pdf.getPagecount());
    }

    @Test(expected = InvalidPDF.class)
    public void testCreatePDFFromPresentation() throws LangDetectException, IOException, InvalidPDF {
        PDFHandler pdfHandler = new PDFHandler();
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/tbp.pdf").getFile());
        PDF pdf = pdfHandler.generatePDFContent(fileEntry);
        assertEquals(12, pdf.getPagecount());
    }

}
