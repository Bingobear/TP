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

//TODO Split into PDFExtractor & KeywordHandler
public class PDFExtractor {

    public static final int stepPages = 4;
    public static final int FILTER_WORTTYPE_MODE = 0;
    /**
     * PDF Extractor
     *
     * @throws IOException
     */
    private String titlePage;
    private int catnumb;
    private int pagenumber;
    private int endPosition = 0;
    private String language;
    private ArrayList<Category> keywords = new ArrayList<Category>();

    public ArrayList<Category> getKeywords() {
        return keywords;
    }

    public void setKeywords(ArrayList<Category> keywords) {
        this.keywords = keywords;
    }

    private int wordcount = 0;

    public int getWordcount() {
        return wordcount;
    }

    PDFExtractor() {

    }

    PDFExtractor(String lang) {
        this.language = lang;
    }

    public void setLang(String lang) {
        this.language = lang;
    }

    public String getLang() {
        return this.language;
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    /**
     * Retrieve number of categories for this pdf
     *
     * @return
     */
    public int getCatnumb() {
        return catnumb;
    }

    public void setCatnumb(int catnumb) {
        this.catnumb = catnumb;
    }

    public int getPagenumber() {
        return pagenumber;
    }

    public void setPagenumber(int pagenumber) {
        this.pagenumber = pagenumber;
    }


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
        PDFTextStripper pdfStripper = null;
        PDDocument pdDoc = null;
        COSDocument cosDoc = null;
        setTitlePage(fileEntry.getName());

        PDFParser parser = new PDFParser(new FileInputStream(fileEntry));
        parser.parse();
        cosDoc = parser.getDocument();
        pdfStripper = new PDFTextStripper();

        pdDoc = new PDDocument(cosDoc);
        this.setPagenumber(pdDoc.getNumberOfPages());
        LangDetect lang = new LangDetect();

        for (int startPage = 0; startPage < pdDoc.getNumberOfPages(); startPage += 5) {
            int endPage = startPage + stepPages;
            String parsedText = NLPUtil.parsePdftoString(pdfStripper, pdDoc, startPage,
                    endPage);
            if (isValidPDF(startPage, parsedText)) {
                setLang(lang.detect(parsedText));

                if (isFirstPage(startPage)) {
                    int firstTwoPages = startPage + 1;
                    this.setTitlePage(NLPUtil.parsePdftoString(pdfStripper, pdDoc,
                            startPage, firstTwoPages));
//                    if (isFragmentedPDF(getTitlePage(), pdfList)) {
//                        throw new InvalidPDF();
//                    }

                    parsedText = parsedText.toLowerCase();
                    String[] tokens = NLPUtil.getTokenPM(parsedText, this.language);
                    getKeywordsPDF(fileEntry, tokens);
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

    private void getKeywordsPDF(File fileEntry, String[] tokens) throws InvalidPDF {
        KeywordHandler keywordHandler = new KeywordHandler();
        keywords = keywordHandler.getKeywordsFromPDF(tokens);
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

    /**
     * Test if PDF is complete and not fragmented after parsing it
     *
     * @param titlepage
     * @param pdfList
     * @return
     */
    private boolean isFragmentedPDF(String titlepage, ArrayList<PDF> pdfList) {
        int sublength = 20;
        for (PDF compare : pdfList) {
            if (titlepage.length() < 20) {
                sublength = titlepage.length() - 1;
            }
            if (compare.getFirstPage().length() < sublength) {
                sublength = compare.getFirstPage().length() - 1;
            }
            if (compare.getFirstPage().substring(0, sublength)
                    .equals(titlepage.substring(0, sublength))) {
                return true;
            }
        }
        return false;
    }
}