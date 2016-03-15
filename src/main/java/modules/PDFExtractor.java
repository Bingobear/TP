package modules;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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
     * @param pdfList
     * @return ArrayList of words
     * @throws LangDetectException
     * @throws IOException
     */
    public ArrayList<Words> parsePDFtoKey(File fileEntry,
                                          ArrayList<PDF> pdfList) throws LangDetectException, IOException {
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

        for (int counter = 0; counter < pdDoc.getNumberOfPages(); counter += 5) {
            String parsedText = NLPUtil.parsePdftoString(pdfStripper, pdDoc, counter,
                    counter + 4);
            if (isValidPDF(counter, parsedText)) {
                setLang(lang.detect(parsedText));

                if (counter == 0) {

                    this.setTitlePage(NLPUtil.parsePdftoString(pdfStripper, pdDoc,
                            counter, counter + 1));
                    if (isFragmentedPDF(this.getTitlePage(), pdfList)) {
                        break;
                    }

                    parsedText = parsedText.toLowerCase();
                    String[] tokens = NLPUtil.getTokenPM(parsedText, this.language);
                    ArrayList<Category> keywords = getKeywordsFromPDF(tokens,
                            fileEntry.getName());
                    if (keywords.isEmpty()) {
                        break;
                    } else if ((keywords.size() < 4) || (keywords.size() > 8)) {
                        if (this.titlePage.length() > endPosition) {
                            this.titlePage = this.titlePage.substring(0,
                                    endPosition - 1);
                        }
                        this.setKeywords(keywords);
                    } else {
                        if (this.titlePage.length() > endPosition) {
                            this.titlePage = this.titlePage.substring(0,
                                    endPosition - 1);
                        }
                        this.setKeywords(keywords);
                    }
                }

                parsedText = parsedText.toLowerCase();

                // sentence detector -> tokenizer
                String[] tokens = NLPUtil.getToken(parsedText, this.language);
                String[] filter = NLPUtil.posttags(tokens, this.language);
                ArrayList<Words> words = NLPUtil.generateWords(filter, tokens, 0, this.getLang(), this.getKeywords());
                result.addAll(words);
                wordcount = wordcount + tokens.length;
            } else {
                System.out.println("Bad Paper or Presentation");
                break;
            }
        }
        cosDoc.close();
        return result;
    }

    private boolean isValidPDF(int counter, String parsedText) {
        return !((counter == 0) && (parsedText.length() < 50));
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

    /**
     * Extracts keywords from a given pdf (token string[]=tokenpm)
     *
     * @param tokens
     * @param name
     * @return
     */
    private ArrayList<Category> getKeywordsFromPDF(String[] tokens, String name) {
        ArrayList<String> textPDF = new ArrayList<String>(Arrays.asList(tokens));
        ArrayList<String> keywordPassage = extractKeywordPassage(textPDF);
        String seperator="";
        if (!keywordPassage.isEmpty()) {

            seperator = KeywordUtil.findSep(keywordPassage);
            String akronom = "";
            String currKey = "";
            for (int ii = 0; ii < keywordPassage.size(); ii++) {
                if (keywordPassage.get(ii).equals(seperator)) {

                    if (!akronom.isEmpty()) {
                        currKey = currKey.replaceAll("(" + akronom + ")", "");
                        currKey = currKey.replace(")", "");
                    }
                    currKey = currKey.replaceFirst("[^\\p{L}]+", "");
                    currKey = currKey.trim();
                    String normKey = currKey.replaceAll("[^\\p{L}]+", "");
                    if (isValidKeyword(currKey, normKey)) {
                        keywords.add(new Category(currKey, normKey, akronom));
                    }
                    akronom = "";
                    currKey = "";

                } else if (keywordPassage.get(ii).contains("(")) {
                    akronom = KeywordUtil.getAkronom(new ArrayList<String>(keywordPassage.subList(
                            ii, keywordPassage.size())));
                } else {

                    currKey = currKey + " " + keywordPassage.get(ii);

                }
            }
            if (isLastKeywordValid(currKey)) {
                resolveLastKeyword(akronom, currKey);
            }
        }

        setCatnumb(keywords.size());
        try {
            LogUtil.writelog(keywords, name, seperator, textPDF.size(), this.language);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return keywords;
    }

    private ArrayList<String> extractKeywordPassage(ArrayList<String> textPDF) {
        int startPosition = KeywordUtil.findKeyWStart(textPDF);
        textPDF= extractStartKeywordPassage(textPDF);

        endPosition = startPosition;
        endPosition = KeywordUtil.findKeyWEnd(textPDF);
        textPDF = extractEndKeywordPassage(textPDF);
        return textPDF;
    }

    private ArrayList<String> extractEndKeywordPassage(ArrayList<String> textPDF) {
        return new ArrayList<String>(textPDF.subList(0, endPosition));
    }

    private ArrayList<String> extractStartKeywordPassage(ArrayList<String> textPDF) {
        int startPosition = KeywordUtil.findKeyWStart(textPDF);

        if (textPDF.get(startPosition).equals(":")) {
            startPosition++;
        }
        if (textPDF.get(startPosition).contains("keywords")) {
            String value = textPDF.get(startPosition);
            textPDF.set(startPosition, value.replaceAll("keywords", ""));
        }
        if (textPDF.get(startPosition).contains("terms")) {
            String value = textPDF.get(startPosition);
            textPDF.set(startPosition, value.replaceAll("terms", ""));
        }
        return new ArrayList<String>(textPDF.subList(startPosition,
                textPDF.size() - 1));
    }

    private void resolveLastKeyword(String akronom, String currKey) {
        if ((currKey.charAt(currKey.length() - 1) == '1')
                && (!currKey.isEmpty())) {
            currKey = currKey.replace("1", "");
        }
        if (!akronom.isEmpty()) {
            currKey = currKey.replaceAll("(" + akronom + ")", "");
            currKey = currKey.replace(")", "");
        }
        currKey = currKey.replaceFirst("[^\\p{L}]+", "");
        if (currKey.endsWith(".")) {
            currKey = currKey.substring(0, currKey.length() - 1);
        }
        currKey = currKey.trim();
        String normKey = currKey.replaceAll("[^\\p{L}]+", "");
        if (isValidKeyword(currKey, normKey)) {
            keywords.add(new Category(currKey, normKey, akronom));
        }
    }

    private boolean isLastKeywordValid(String currKey) {
        return (currKey.length() < 100) && (currKey.length() > 2);
    }

    private boolean isValidKeyword(String currKey, String normKey) {
        return (!currKey.isEmpty()) && (!normKey.isEmpty());
    }
}