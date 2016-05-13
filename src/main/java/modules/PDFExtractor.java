package modules;

import Util.NLPUtil;
import com.cybozu.labs.langdetect.LangDetectException;
import models.BasicText;
import models.Category;
import models.Words;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Main Text Mining Class
 *
 * @author Simon Bruns
 */

public class PDFExtractor {

    public static final int FILTER_WORDTYPE_MODE = 0;
    public static final int steps = 5;
    public static final int stepPages = steps - 1;
    public static final int endFirstPage = 1;
    public static final int startFirstPage = 0;

    private String titlePage;
    private int catnumb;
    private int endPosition = 0;
    private int pagenumber;
    private int wordcount = 0;
    private ArrayList<Category> keywords = new ArrayList<Category>();
    private PDFConverter pdfConverter;

    public ArrayList<Words> parsePDF(File fileEntry) throws IOException, InvalidPDF, LangDetectException {
        pdfConverter = new PDFConverter(fileEntry);
        parseFirstPages();
        return parsePDF2Words();
    }

    private void parseFirstPages() throws IOException, InvalidPDF, LangDetectException {
        String parsedText = pdfConverter.parseNPages(startFirstPage, endFirstPage);
        BasicText basicText = new BasicText(parsedText);
        setTitlePage(basicText.getText());
        String[] tokens = NLPUtil.getTokenPM(parsedText, basicText.getLanguage());
        getKeywordsPDF(tokens);
        optimizeTitlePageSize();
    }



    private void getKeywordsPDF(String[] tokens) throws InvalidPDF {
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        setKeywords(keywordExtractor.getKeywordsFromPDF(tokens));
        setCatnumb(keywords.size());
        endPosition = keywordExtractor.getEndPosition();
    }

    private void optimizeTitlePageSize() {
        if (this.titlePage.length() > endPosition) {
            this.titlePage = this.titlePage.substring(0,
                    endPosition - 1);
        }
    }

    /**
     * Parses PDFfile -> performs textmining: keyword-extraction,
     * word-extraction
     *
     * @return ArrayList of words
     * @throws LangDetectException
     * @throws IOException
     */
    public ArrayList<Words> parsePDF2Words() throws LangDetectException, IOException, InvalidPDF {
        ArrayList<Words> result = new ArrayList<Words>();
        setPagenumber(pdfConverter.getPageNumber());
        for (int startPage = 0; startPage < pdfConverter.getPageNumber(); startPage += steps) {
            int endPage = startPage + stepPages;
            BasicText basicText = new BasicText(pdfConverter.parseNPages(startPage, endPage));
            if (isValidPDF(startPage, basicText.getText())) {
                ArrayList<Words> words = extractWords(basicText);
                result.addAll(words);
            } else {
                throw new InvalidPDF();
            }
        }
        pdfConverter.close();
        return result;
    }

    private ArrayList<Words> extractWords(BasicText basicText) {
        String[] tokens = NLPUtil.getToken(basicText.getText(), basicText.getLanguage());
        String[] filter = NLPUtil.posttags(tokens, basicText.getLanguage());
        wordcount = wordcount + tokens.length;
        return NLPUtil.generateWords(filter, tokens, FILTER_WORDTYPE_MODE, basicText.getLanguage(), this.getKeywords());
    }

    private boolean isFirstPage(int startPage) {
        return startPage == 0;
    }

    private boolean isValidPDF(int counter, String parsedText) {
        return !((isFirstPage(counter)) && (parsedText.length() < 50));
    }

    public ArrayList<Category> getKeywords() {
        return keywords;
    }

    private void setKeywords(ArrayList<Category> keywords) {
        this.keywords = keywords;
    }

    public int getWordcount() {
        return wordcount;
    }

    public String getTitlePage() {
        return titlePage;
    }

    private void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public int getCatnumb() {
        return catnumb;
    }

    private void setCatnumb(int catnumb) {
        this.catnumb = catnumb;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }
}