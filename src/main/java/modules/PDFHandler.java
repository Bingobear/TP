package modules;

import Util.NLPUtil;
import com.cybozu.labs.langdetect.LangDetectException;
import models.PDF;
import models.WordOcc;
import models.Words;

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

        ArrayList<WordOcc> wordOccurences = extractWordsAndOccs(fileEntry);
        PDF pdf = fillPDF(fileEntry,wordOccurences);

        if (hasKeywords(pdf)) {
            return pdf;
        }
        throw new InvalidPDF();
    }



    private ArrayList<WordOcc> extractWordsAndOccs(File fileEntry) throws LangDetectException, IOException, InvalidPDF {
        ArrayList<Words> words;
        ArrayList<WordOcc> wordOccs = new ArrayList<WordOcc>();
        words = extractor.parsePDFtoKey(fileEntry);
        if (hasWords(words)) {
            wordOccs = NLPUtil.keyOcc(words);
        }
        return wordOccs;
    }

    private boolean hasWords(ArrayList<Words> words) {
        return words.size() > 0;
    }

    private PDF fillPDF(File fileEntry, ArrayList<WordOcc> occ) {
        PDF pdf = new PDF(occ, extractor);
        pdf.setFilename(fileEntry.getName());

        return pdf;
    }

    private boolean hasKeywords(PDF pdf) {
        return pdf.getCatnumb() > 0;
    }
}
