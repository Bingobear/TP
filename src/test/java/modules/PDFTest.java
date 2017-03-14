package modules;

import Util.WordTypeFilter;
import com.cybozu.labs.langdetect.LangDetectException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class PDFTest {

    @Test(expected = InvalidPDF.class)
    public void testParsePrestoKey() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/tbp.pdf").getFile());
        PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
        assertEquals(0, pdf.retrieveWordCount());
    }

    @Test(expected = NullPointerException.class)
    public void testParseNoPDFtoKey() throws LangDetectException, IOException, InvalidPDF {
        File fileEntry = null;
        PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
        assertEquals(2070, pdf.retrieveWordCount());
    }

    @Test
    public void testParsePDFtoKey() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
        assertEquals(2637, pdf.retrieveWordCount());
    }

    @Test
    public void testgetPDFKeywords() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
        assertEquals(6, pdf.getKeywords().size());
    }

    @Test
    public void testCreatePDF() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/schaar_06038875.pdf").getFile());
        PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
        assertEquals(8, pdf.getPageCount());
    }

    @Test(expected = InvalidPDF.class)
    public void testCreatePDFFromPresentation() throws LangDetectException, IOException, InvalidPDF {
        ClassLoader classLoader = getClass().getClassLoader();
        File fileEntry = new File(classLoader.getResource("text/tbp.pdf").getFile());
        PDF pdf = new PDF(fileEntry, Collections.singletonList(WordTypeFilter.NOUN));
        assertEquals(12, pdf.getPageCount());
    }
}
