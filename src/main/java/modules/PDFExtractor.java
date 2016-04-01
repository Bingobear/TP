package modules;

import java.io.*;
import java.util.ArrayList;

import models.*;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;

import Util.*;

import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Main Text Mining Class
 *
 * @author Simon Bruns
 */

public class PDFExtractor {

    public static final int FILTER_WORTTYPE_MODE = 0;
    public static final int steps = 5;
    public static final int stepPages = steps - 1;
    public static final int endTitlePage = 1;
    public static final int startTitlePage = 0;

    private COSDocument cosDoc;
    private PDDocument pdDoc;

    private String titlePage;
    private int catnumb;
    private int pagenumber;
    private int endPosition = 0;
    private int wordcount = 0;
    private String language;
    private ArrayList<Category> keywords = new ArrayList<Category>();

    /**
     * Parses PDFfile -> performs textmining: keyword-extraction,
     * word-extraction
     *
     * @param fileEntry
     * @return ArrayList of words
     * @throws LangDetectException
     * @throws IOException
     */
    public ArrayList<Words> parsePDFtoKey(File fileEntry) throws LangDetectException, IOException, InvalidPDF {
        ArrayList<Words> result = new ArrayList<Words>();
        pdDoc = parsePDFDocument(fileEntry);
        setPagenumber(pdDoc.getNumberOfPages());

        for (int startPage = 0; startPage < pdDoc.getNumberOfPages(); startPage += steps) {
            int endPage = startPage + stepPages;
            String parsedText = NLPUtil.parsePdftoString(pdDoc, startPage,
                    endPage);
            if (isValidPDF(startPage, parsedText)) {
                parsedText = parsedText.toLowerCase();
                if (isFirstPage(startPage)) {
                    parseFirstPages(parsedText);
                }
                ArrayList<Words> words = extractWords(parsedText);
                result.addAll(words);
            } else {
                throw new InvalidPDF();
            }
        }
        cosDoc.close();
        return result;
    }

    private ArrayList<Words> extractWords(String parsedText) {
        String[] tokens = NLPUtil.getToken(parsedText, language);
        String[] filter = NLPUtil.posttags(tokens, language);
        wordcount = wordcount + tokens.length;
        return NLPUtil.generateWords(filter, tokens, FILTER_WORTTYPE_MODE, this.getLang(), this.getKeywords());
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

    private void parseFirstPages(String parsedText) throws IOException, InvalidPDF, LangDetectException {
        LangDetect lang = new LangDetect();
        setLang(lang.detect(parsedText));
        this.setTitlePage(NLPUtil.parsePdftoString(pdDoc,
                startTitlePage, endTitlePage));
        String[] tokens = NLPUtil.getTokenPM(parsedText, this.language);
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

    private void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }
}