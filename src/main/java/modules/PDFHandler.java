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
    private static final double TFICF_THRESHOLD = 0.00001;
    private static final boolean debug_calc = false;

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
        PDFExtractor extractor = new PDFExtractor();

        ArrayList<WordOcc> wordOccs = extractWordsAndOccs(fileEntry, extractor);
        PDF pdf = fillPDF(fileEntry, extractor, wordOccs);

        if (hasKeywords(pdf)) {
            return pdf;
        }
        throw new InvalidPDF();
    }



    private ArrayList<WordOcc> extractWordsAndOccs(File fileEntry, PDFExtractor extractor) throws LangDetectException, IOException, InvalidPDF {
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

    private PDF fillPDF(File fileEntry, PDFExtractor extractor, ArrayList<WordOcc> occ) {
        PDF pdf = new PDF(occ, extractor.getLang(),
                extractor.getWordcount(), extractor.getTitlePage());
        pdf.setGenericKeywords(extractor.getKeywords());
        pdf.setCatnumb(extractor.getCatnumb());
        pdf.setPagecount(extractor.getPagenumber());
        pdf.setFilename(fileEntry.getName());

        return pdf;
    }

    private boolean hasKeywords(PDF pdf) {
        return pdf.getCatnumb() > 0;
    }
}
