package modules;

import Util.NLPUtil;
import com.cybozu.labs.langdetect.LangDetectException;
import models.BasicText;
import models.Category;
import models.Word;

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
    private BasicText pdfText;
    private ArrayList<Category> keywords = new ArrayList<Category>();
    private PDFConverter pdfConverter;

    public ArrayList<Word> parsePDF(File fileEntry) throws IOException, InvalidPDF, LangDetectException {
        pdfConverter = new PDFConverter(fileEntry);
        parseFirstPages();
        return parsePDF2Words();
    }

    private void parseFirstPages() throws IOException, InvalidPDF, LangDetectException {
        String parsedText = pdfConverter.parseNPages(startFirstPage, endFirstPage);
        BasicText basicText = new BasicText(parsedText);
        setTitlePage(basicText.getText());
        String[] tokens = NLPUtil.getTokenPM(basicText.getText(), basicText.getLanguage());
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
    public ArrayList<Word> parsePDF2Words() throws LangDetectException, IOException, InvalidPDF {
        ArrayList<Word> result = new ArrayList<Word>();
        setPagenumber(pdfConverter.getPageNumber());
        ArrayList<BasicText> basicTexts= new ArrayList<BasicText>();
        for (int startPage = 0; startPage < pdfConverter.getPageNumber(); startPage += steps) {
            int endPage = startPage + stepPages;
            BasicText basicTextofPages = new BasicText(pdfConverter.parseNPages(startPage, endPage));
            basicTexts.add(basicTextofPages);
            if (isValidPDF(startPage, basicTextofPages.getText())) {
                ArrayList<Word> words = extractWords(basicTextofPages);
                result.addAll(words);
            } else {
                throw new InvalidPDF();
            }
        }
        setPdfText(mergeBasicTexts(basicTexts));
        pdfConverter.close();
        return result;
    }

    private ArrayList<Word> extractWords(BasicText basicText) {
        String[] tokens = NLPUtil.getToken(basicText.getText(), basicText.getLanguage());
        String[] filter = NLPUtil.posttags(tokens, basicText.getLanguage());
        wordcount = wordcount + tokens.length;
        return NLPUtil.generateWords(filter, tokens, FILTER_WORDTYPE_MODE, basicText.getLanguage(), this.getKeywords());
    }

    private BasicText mergeBasicTexts(ArrayList<BasicText> basicTexts) throws LangDetectException {
        String addedText="";
        for (BasicText basicText:basicTexts) {
            addedText = addedText.concat(basicText.getText());
        }
        BasicText completeText = new BasicText(addedText);
        return completeText;
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

    public BasicText getPdfText() {
        return pdfText;
    }

    private void setPdfText(BasicText pdfText) {
        this.pdfText = pdfText;
    }
}