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
    private ArrayList<WordOcc> wordOcc;
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

    public PDF(ArrayList<WordOcc> words, String language, int wordcount) {
        this.wordOcc = words;
        this.wordcount = wordcount;
        this.language = language;
    }

    public PDF(ArrayList<WordOcc> words, PDFExtractor extractor) {
        this.wordOcc = words;
        this.wordcount = extractor.getWordcount();
        this.language = extractor.getPdfText().getLanguage();
        this.firstPage = extractor.getTitlePage();
        this.setGenericKeywords(extractor.getKeywords());
        this.setCatnumb(extractor.getCatnumb());
        this.setPagecount(extractor.getPagenumber());
    }

    public void calculateTF_IDF() {
        WordOcc word = null;
        for (int ii = 0; ii < wordOcc.size(); ii++) {
            word = wordOcc.get(ii);
            double tf = AlgorithmUtil
                    .calcTF((double) word.getOcc(), (double) wordcount);
            word.setTf(tf);
            double tfidf = AlgorithmUtil.calcTFIDF(tf, (double) word.getIdf());
            word.setTfidf(tfidf);
        }
    }

    public WordOcc getWordOcc(String word) {
        for (int ii = 0; ii < this.wordOcc.size(); ii++) {
            if (wordOcc.get(ii).getWord().getWord().contains(word)) {
                return wordOcc.get(ii);
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

    public ArrayList<WordOcc> getWordOccList() {
        return wordOcc;
    }

    public void addWordOcc(WordOcc word) {
        this.addWordOcc(word);
    }

    public void setWordOcc(ArrayList<WordOcc> wordocc) {
        this.wordOcc = wordocc;
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
