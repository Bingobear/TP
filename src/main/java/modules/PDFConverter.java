package modules;

import Util.NLPUtil;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * Created by simonbruns on 29/04/16.
 */
public class PDFConverter {
    private COSDocument cosDoc;
    private PDDocument pdDoc;
    private int pageNumber;
    public PDFConverter(File pdfFile) throws IOException {
        pdDoc = parsePDFDocument(pdfFile);
        setPagenumber(pdDoc.getNumberOfPages());
        }


    private PDDocument parsePDFDocument(File fileEntry) throws IOException {
        PDFParser parser = initializePDFParser(fileEntry);
        cosDoc = parser.getDocument();
        return new PDDocument(cosDoc);
    }
    private PDFParser initializePDFParser(File fileEntry) throws IOException {
        PDFParser parser = new PDFParser(new FileInputStream(fileEntry));
        parser.parse();
        return parser;
    }

    public String parseNPages(int startPage, int endPage) throws IOException {
        String textPassage = NLPUtil.parsePdftoString(pdDoc, startPage,
                endPage);
        return textPassage;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    private void setPagenumber(int pagenumber) {
        this.pageNumber = pagenumber;
    }

    public void close() throws IOException {
        cosDoc.close();
    }
}
