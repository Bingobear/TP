package models;

/**
 * PDF model class
 *
 * @author Simon Bruns
 *
 */

import java.util.ArrayList;

import Util.AlgorithmUtil;
import modules.PDFExtractor;

public class PDF {
    private ArrayList<WordProperty> wordProperty;
    private String language;
    private int wordcount;
    private ArrayList<Category> genericKeywords;

    private int catnumb;
    private String firstPage;
    private int publicationID;
    private String title;
    private int pagecount;
    private String filename;



    public PDF() {
    }

    public PDF(ArrayList<WordProperty> words, String language, int wordcount) {
        this.wordProperty = words;
        this.wordcount = wordcount;
        this.language = language;
    }

    public PDF(ArrayList<WordProperty> words, PDFExtractor extractor) {
        this.wordProperty = words;
        this.wordcount = extractor.getWordcount();
        this.language = extractor.getPdfText().getLanguage();
        this.firstPage = extractor.getTitlePage();
        this.setGenericKeywords(extractor.getKeywords());
        this.setCatnumb(extractor.getCatnumb());
        this.setPagecount(extractor.getPagenumber());
    }

    public void calculateTF_IDF() {
        WordProperty word = null;
        for (int ii = 0; ii < wordProperty.size(); ii++) {
            word = wordProperty.get(ii);
            double tf = AlgorithmUtil
                    .calcTF((double) word.getOcc(), (double) wordcount);
            word.setTf(tf);
            double tfidf = AlgorithmUtil.calcTFIDF(tf, (double) word.getIdf());
            word.setTfidf(tfidf);
        }
    }

    public WordProperty getWordOcc(String word) {
        for (int ii = 0; ii < this.wordProperty.size(); ii++) {
            if (wordProperty.get(ii).getWord().getText().contains(word)) {
                return wordProperty.get(ii);
            }
        }
        return null;
    }

    public int getWordcount() {
        return wordcount;
    }

    public void setWordcount(int wordcount) {
        this.wordcount = wordcount;
    }

    public ArrayList<WordProperty> getWordOccList() {
        return wordProperty;
    }

    public void addWordOcc(WordProperty word) {
        this.addWordOcc(word);
    }

    public void setWordProperty(ArrayList<WordProperty> wordocc) {
        this.wordProperty = wordocc;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public ArrayList<Category> getGenericKeywords() {
        return genericKeywords;
    }

    public void setGenericKeywords(ArrayList<Category> genericKeywords) {
        this.genericKeywords = genericKeywords;
    }

    public String getFirstPage() {
        return firstPage;
    }

    public void setFirstPage(String firstPage) {
        this.firstPage = firstPage;
    }

    public int getPublicationID() {
        return publicationID;
    }

    public void setPublicationID(int publicationID) {
        this.publicationID = publicationID;
    }

    public int getCatnumb() {
        return catnumb;
    }

    public void setCatnumb(int catnumb) {
        this.catnumb = catnumb;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getPagecount() {
        return pagecount;
    }

    public void setPagecount(int pagecount) {
        this.pagecount = pagecount;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }




}
