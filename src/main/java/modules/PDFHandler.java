package modules;

import Util.NLPUtil;
import com.cybozu.labs.langdetect.LangDetectException;
import models.PDF;
import models.Word;
import models.WordProperty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main Interface to initiate Textmining (pdf extractor)
 *
 * @author Simon Bruns
 */
public class PDFHandler {
    private PDFExtractor extractor;

    public PDFHandler(){
        extractor = new PDFExtractor();
    }

    public PDF generatePDFContent(File fileEntry) throws LangDetectException, IOException, InvalidPDF {
        return createPDF(fileEntry);
    }

    /***
     * Generates the PDF with its corresponding informations from a given File
     *
     * @param fileEntry
     * @return
     * @throws LangDetectException
     * @throws IOException
     * @throws InvalidPDF
     */
    private PDF createPDF(File fileEntry) throws LangDetectException, IOException, InvalidPDF {

        ArrayList<WordProperty> wordOccurences = extractPDFContent(fileEntry);
        PDF pdf = fillPDF(fileEntry,wordOccurences);

        if (hasKeywords(pdf)) {
            return pdf;
        }
        throw new InvalidPDF();
    }



    private ArrayList<WordProperty> extractPDFContent(File fileEntry) throws LangDetectException, IOException, InvalidPDF {
        ArrayList<Word> words;
        ArrayList<WordProperty> wordProperties = new ArrayList<WordProperty>();
        words = extractor.parsePDF(fileEntry);
        if (hasWords(words)) {
            wordProperties = NLPUtil.keyOcc(words);
        }
        return wordProperties;
    }

    private PDF fillPDF(File fileEntry, ArrayList<WordProperty> occ) {
        PDF pdf = new PDF(occ, extractor);
        pdf.setFilename(fileEntry.getName());
        return pdf;
    }

    private boolean hasWords(ArrayList<Word> words) {
        return words.size() > 0;
    }

    private boolean hasKeywords(PDF pdf) {
        return pdf.getCatnumb() > 0;
    }
}
