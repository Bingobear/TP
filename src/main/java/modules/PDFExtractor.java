package modules;

import java.io.*;
import java.util.ArrayList;

import models.*;

import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

import Util.*;

import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Main Text Mining Class
 *
 * @author Simon Bruns
 */

public class PDFExtractor {
;
    public static final int FILTER_WORTTYPE_MODE = 0;
    public static final int steps = 5;
    public static final int stepPages = steps-1;

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
//        Preparation Objects to read PDF

        pdDoc = parsePDFDocument(fileEntry);
        setPagenumber(pdDoc.getNumberOfPages());
        LangDetect lang = new LangDetect();

        for (int startPage = 0; startPage < pdDoc.getNumberOfPages(); startPage += steps) {
            int endPage = startPage + stepPages;
            String parsedText = NLPUtil.parsePdftoString(pdDoc, startPage,
                    endPage);
            if (isValidPDF(startPage, parsedText)) {
                setLang(lang.detect(parsedText));

                if (isFirstPage(startPage)) {
                    int firstTwoPages = startPage + 1;
                    this.setTitlePage(NLPUtil.parsePdftoString(pdDoc,
                            startPage, firstTwoPages));
                    parsedText = parsedText.toLowerCase();
                    String[] tokens = NLPUtil.getTokenPM(parsedText, this.language);
                    getKeywordsPDF(tokens);
                    optimizeTitlePageSize();
                }

                parsedText = parsedText.toLowerCase();

                // sentence detector -> tokenizer
                String[] tokens = NLPUtil.getToken(parsedText, this.language);
                String[] filter = NLPUtil.posttags(tokens, this.language);
                ArrayList<Words> words = NLPUtil.generateWords(filter, tokens, FILTER_WORTTYPE_MODE, this.getLang(), this.getKeywords());
                result.addAll(words);
                wordcount = wordcount + tokens.length;
            } else {
                throw new InvalidPDF();


            }
        }
        cosDoc.close();
        return result;
    }

    private PDDocument parsePDFDocument(File fileEntry) throws IOException {
        PDFParser parser = initializePDFParser(fileEntry);
        cosDoc= parser.getDocument();
        return new PDDocument(cosDoc);
    }

    private PDFParser initializePDFParser(File fileEntry) throws IOException {
        PDFParser parser = new PDFParser(new FileInputStream(fileEntry));
        parser.parse();
        return parser;
    }


    private void getKeywordsPDF(String[] tokens) throws InvalidPDF {
        KeywordHandler keywordHandler = new KeywordHandler();
        setKeywords(keywordHandler.getKeywordsFromPDF(tokens));
        setCatnumb(keywords.size());
        endPosition = keywordHandler.getEndPosition();
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