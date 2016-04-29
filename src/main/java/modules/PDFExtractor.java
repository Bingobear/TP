package modules;

import Util.NLPUtil;
import com.cybozu.labs.langdetect.LangDetectException;
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
    public static final int endTitlePage = 1;
    public static final int startTitlePage = 0;

    private String titlePage;
    private int catnumb;
    private int pagenumber;
    private int endPosition = 0;
    private int wordcount = 0;
    private String language;
    private ArrayList<Category> keywords = new ArrayList<Category>();
    private PDFConverter pdfConverter;

    public ArrayList<Words> parsePDF(File fileEntry) throws IOException, InvalidPDF, LangDetectException {
        pdfConverter = new PDFConverter(fileEntry);
        parseFirstPages();
        return parsePDF2Words();
    }

    private void parseFirstPages() throws IOException, InvalidPDF, LangDetectException {
        String parsedText = pdfConverter.parseNPages(startTitlePage,endTitlePage);
        detectLanguage(parsedText);
        setTitlePage(parsedText);
        String[] tokens = NLPUtil.getTokenPM(parsedText, this.language);
        getKeywordsPDF(tokens);
        optimizeTitlePageSize();
    }

    private void detectLanguage(String parsedText) throws LangDetectException {
        LangDetect lang = new LangDetect();
        setLang(lang.detect(parsedText));
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
        for (int startPage = 0; startPage < pdfConverter.getPageNumber(); startPage += steps) {
            int endPage = startPage + stepPages;
            String parsedText = pdfConverter.parseNPages(startPage, endPage);
            if (isValidPDF(startPage, parsedText)) {
                parsedText = parsedText.toLowerCase();
                ArrayList<Words> words = extractWords(parsedText);
                result.addAll(words);
            } else {
                throw new InvalidPDF();
            }
        }
        pdfConverter.close();
        return result;
    }

    private ArrayList<Words> extractWords(String parsedText) {
        String[] tokens = NLPUtil.getToken(parsedText, language);
        String[] filter = NLPUtil.posttags(tokens, language);
        wordcount = wordcount + tokens.length;
        return NLPUtil.generateWords(filter, tokens, FILTER_WORDTYPE_MODE, this.getLang(), this.getKeywords());
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

    private void setLang(String lang) {
        this.language = lang;
    }

    public String getLang() {
        return this.language;
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
}