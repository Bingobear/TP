package modules;

import Util.AlgorithmUtil;
import Util.NLPUtil;
import Util.WordTypeFilter;
import com.cybozu.labs.langdetect.LangDetectException;
import models.BasicText;
import models.Category;
import models.Word;
import models.WordProperty;
import org.apache.commons.lang3.Validate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDF {

    private static final int steps = 5;
    private static final int stepPages = steps - 1;

    private PDFConverter pdfConverter;
    private String titlePage;
    private String title;
    private BasicText pdfText;
    private ArrayList<Category> keywords = new ArrayList<>();
    private ArrayList<WordProperty> words = new ArrayList<>();
    private List<WordTypeFilter> wordTypes;
    private String fileName;
    private String language;
    private int pageCount;

    public PDF(File file, List<WordTypeFilter> types) throws InvalidPDF, LangDetectException {
        try {
            pdfConverter = new PDFConverter(file);
            pageCount = pdfConverter.getPageNumber();
            fileName = file.getName();
            wordTypes = types;
            titlePage = pdfConverter.parseNPages(0, 1);
            detectLanguage(titlePage);
            keywords = retrieveKeywords(titlePage);
            Validate.notEmpty(keywords, "PDF has no keywords");
            words = parsePDF2Words();
            Validate.notEmpty(keywords, "PDF has no words");
            pdfConverter.close();

        } catch (IOException e) {
            throw new InvalidPDF();
        }

    }

    private void detectLanguage(String text) throws LangDetectException {
        LangDetect lang = new LangDetect();
        language = lang.detect(text);
    }

    private ArrayList<Category> retrieveKeywords(String titlePage) throws InvalidPDF, LangDetectException {
        BasicText basicText = new BasicText(titlePage);
        String[] tokens = NLPUtil.getTokenPM(basicText.getText(), basicText.getLanguage());
        KeywordExtractor keywordExtractor = new KeywordExtractor();
        return keywordExtractor.retrieveKeywordsFromPDF(tokens);
    }

    private ArrayList<WordProperty> parsePDF2Words() throws IOException, InvalidPDF, LangDetectException {
        ArrayList<Word> result = new ArrayList<>();
        ArrayList<BasicText> basicTexts = new ArrayList<>();
        for (int currentPage = 0; currentPage < pageCount; currentPage += steps) {
            BasicText basicTextOfPages = new BasicText(pdfConverter.parseNPages(currentPage, currentPage + stepPages));
            if (isValidPDF(currentPage, basicTextOfPages.getText())) {
                basicTexts.add(basicTextOfPages);
                result.addAll(extractWords(basicTextOfPages));
            } else {
                throw new InvalidPDF();
            }
        }
        pdfText = mergeBasicTexts(basicTexts);
        return NLPUtil.keyOcc(result);
    }

    private ArrayList<Word> extractWords(BasicText basicText) {
        List<String> tokens = NLPUtil.getToken(basicText.getText(), basicText.getLanguage());
        tokens = NLPUtil.filterLanguageTokens(tokens, basicText.getLanguage());
        List<String> filter = NLPUtil.posttags(tokens.toArray(new String[0]), basicText.getLanguage());
        return NLPUtil.generateWords(filter, tokens, wordTypes, basicText.getLanguage(), this.getKeywords());
    }

    private boolean isValidPDF(int counter, String parsedText) {
        return !((isFirstPage(counter)) && (parsedText.length() < 50));
    }

    private BasicText mergeBasicTexts(ArrayList<BasicText> basicTexts) throws LangDetectException {
        String addedText = "";
        for (BasicText basicText : basicTexts) {
            addedText = addedText.concat(basicText.getText());
        }
        return new BasicText(addedText);
    }


    /**
     * Removes words from each pdf which tfidf is not above the defined level
     *
     * @param level
     * @return
     */
    public void filterPDFTDIDF(double level) {
        ArrayList<WordProperty> filteredWords = new ArrayList<WordProperty>();

        for (WordProperty word : words) {
            if (word.getTfidf() > level) {
                filteredWords.add(word);
            }
        }
        words = filteredWords;
    }

    public void calculateTF_IDF() {
        WordProperty word = null;
        for (int ii = 0; ii < words.size(); ii++) {
            word = words.get(ii);
            double tf = AlgorithmUtil
                    .calcTF((double) word.getOcc(), (double) retrieveWordCount());
            word.setTf(tf);
            double tfidf = AlgorithmUtil.calcTFIDF(tf, word.getIdf());
            word.setTfidf(tfidf);
        }
    }

    private boolean isFirstPage(int startPage) {
        return startPage == 0;
    }

    public BasicText getPdfText() {
        return pdfText;
    }

    public int retrieveWordCount() {
        return words.stream().mapToInt(WordProperty::getOcc).sum();
    }

    public String getTitlePage() {
        return titlePage;
    }

    public void setTitlePage(String titlePage) {
        this.titlePage = titlePage;
    }

    public List<WordTypeFilter> getWordTypes() {
        return wordTypes;
    }

    public ArrayList<Category> getKeywords() {
        return keywords;
    }

    public ArrayList<WordProperty> getWords() {
        return words;
    }

    public String getFileName() {
        return fileName;
    }

    public String getLanguage() {
        return language;
    }

    public int getPageCount() {
        return pageCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
