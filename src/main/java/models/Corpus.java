package models;

import java.io.*;
import java.util.ArrayList;

import Util.AlgorithmUtil;
import modules.PDF;

/**
 * Corpus class Interface to perform ranking algorithm (tfidf) and (tficf)
 *
 * @author Simon Bruns
 */
public class Corpus {

    private int docNEng;
    private int docNGer;
    private ArrayList<PDF> pdfList;
    private ArrayList<CategoryCatalog> globalCategoryCatalog;

    public ArrayList<CategoryCatalog> getGlobalCategoryCatalog() {
        return globalCategoryCatalog;
    }

    public Corpus() {
        pdfList = new ArrayList<>();
        docNEng = 0;
        docNGer = 0;
        globalCategoryCatalog = new ArrayList<CategoryCatalog>();
    }

    public void setGlobalCategoryCatalog(
            ArrayList<CategoryCatalog> globalCategoryCatalog) {
        this.globalCategoryCatalog = globalCategoryCatalog;
    }

    public void calculateIdf() {
        ArrayList<WordProperty> words = null;
        // new
        ArrayList<WordProperty> wordes = null;
        String language = null;
        for (PDF doc : pdfList) {
            words = doc.getWords();
            language = doc.getLanguage();
            for (WordProperty word : words) {
                // so words are not considered multiple times
                if (word.getKeyinPDF() == 0) {
                    for (PDF currdoc : pdfList) {
                        if (currdoc.getLanguage().equals(language)) {
                            wordes = currdoc.getWords();
                            for (int ii = 0; ii < wordes.size(); ii++) {
                                if (wordes.get(ii).getWord().getText()
                                        .contains(word.getWord().getText())) {
                                    word.incKeyinPDF();
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }
        for (PDF doc : pdfList) {
            words = doc.getWords();
            for (WordProperty word : words) {
                String pdfLanguage = doc.getLanguage();
                word.setIdf(AlgorithmUtil.calcIDF(
                        (double) getDocN(pdfLanguage),
                        (double) word.getKeyinPDF()));
            }
        }
    }

    public int getDocN(String language) {
        if (language.equals("de")) {
            return docNGer;
        } else if (language.equals("en")) {
            return docNEng;
        }
        return docNEng;

    }

    public void setDocN(int docN, String language) {
        if (language.equals("de")) {
            this.docNGer = docN;
        } else if (language.equals("en")) {
            this.docNEng = docN;
        }
    }

    public void incDocN(String language) {
        if (language.equals("de")) {
            this.docNGer++;
        } else if (language.equals("en")) {
            this.docNEng++;
        }
    }


    public ArrayList<PDF> getPdfList() {
        return pdfList;
    }

    public void setPdfList(ArrayList<PDF> pdfList) {
        this.pdfList = pdfList;
    }

    @SuppressWarnings("unused")
    public ArrayList<PDF> calculateTD_IDF(ArrayList<PDF> pdfList) {
        for (int ii = 0; ii < pdfList.size(); ii++) {
            pdfList.get(ii).calculateTF_IDF();
            return pdfList;

        }
        return pdfList;
    }


    /**
     * Associates pdfwords with category (preparation for tficf)
     *
     * @param pdf
     */

    public void associateWordswithCategory(PDF pdf) {
        boolean found = false;
        for (Category cat : pdf.getKeywords()) {
            for (int counter = 0; counter < this.globalCategoryCatalog.size(); counter++) {
                String wordCat = cat.getNormtitle();
                String wordGlobal = this.globalCategoryCatalog.get(counter)
                        .getCategory().getNormtitle();
                int ld = AlgorithmUtil.LevenshteinDistance(wordCat, wordGlobal);
                double sim = 0;
                if (wordCat.length() > wordGlobal.length()) {
                    sim = AlgorithmUtil.calculateWordSim(wordCat, ld);
                } else {
                    sim = AlgorithmUtil.calculateWordSim(wordGlobal, ld);
                }
                if (sim <= 0.2) {
                    found = true;
                    try {
                        writelog(wordCat, wordGlobal, pdf.getTitle());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cat.setAssociatedGCAT(this.getGlobalCategoryCatalog()
                            .get(counter).getCategory().getNormtitle());
                    addCategoryWords(counter, pdf.getWords());
                    break;
                }
            }
            if (!found) {
                cat.setAssociatedGCAT(cat.getNormtitle());
                this.globalCategoryCatalog.add(new CategoryCatalog(cat, pdf
                        .getWords()));
            } else {
                found = false;
            }
        }
    }

    private void writelog(String wordCat, String wordGlobal, String string)
            throws IOException {
        String timeLog = "MatchesLogFile";
        File logFile = new File(timeLog);

        // This will output the full path where the file will be written to...
//		System.out.println(logFile.getCanonicalPath());

        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(logFile, true));
        writer.write(string + " has matches " + wordCat + " with gCat"
                + wordGlobal);
        writer.newLine();
        writer.write("_________________________________________________________");
        writer.newLine();
        writer.close();

    }

    /**
     * Calculates occurences of a word within each category (preparation TFICF)
     *
     * @param position
     * @param wordPropertyList
     */
    private void addCategoryWords(int position, ArrayList<WordProperty> wordPropertyList) {
        ArrayList<WordProperty> keys = this.globalCategoryCatalog.get(position)
                .getKeywordList();
        boolean found = false;
        int catocc = 0;
        for (WordProperty word : wordPropertyList) {
            for (WordProperty gkey : keys) {
                if (word.getWord().getText().equals(gkey.getWord().getText())) {
                    found = true;
                    gkey.setOcc(gkey.getOcc() + word.getOcc());
                    catocc = catocc + gkey.getOcc();
                    break;
                }
            }
            if (!found) {
                this.globalCategoryCatalog.get(position).getKeywordList()
                        .add(word);
                catocc = catocc + word.getOcc();
            } else {
                found = false;
            }
        }
        this.globalCategoryCatalog.get(position).incTotalW(catocc);

    }

    /**
     * Calculates TFIDF for every words in each pdfs prepares its values (count
     * occ of words in cat)
     */
    public void initializeTFICFCalc() {
        for (int ii = 0; ii < this.pdfList.size(); ii++) {
            PDF current = this.pdfList.get(ii);
            for (int counter = 0; counter < current.getKeywords().size(); counter++) {
                for (CategoryCatalog catcat : this.globalCategoryCatalog) {
                    if (catcat
                            .getCategory()
                            .getTitle()
                            .equals(current.getKeywords().get(counter)
                                    .getTitle())) {
                        current = calculateCatTF(current, counter, catcat);
                    }
                }
            }
            this.pdfList.set(ii, current);
        }
        calculateICF();
        calculateTFICF();
    }

    /**
     * Calculates TFICF for every word for each category
     */
    private void calculateTFICF() {
        for (int ii = 0; ii < this.globalCategoryCatalog.size(); ii++) {
            this.globalCategoryCatalog.get(ii).calculateTF_IDF();
//			 System.out.println(ii);
//			 ArrayList<WordProperty> words = this.globalCategoryCatalog.get(ii)
//			 .getKeywordList();
//			 for (int jj = 0; jj < words.size(); jj++) {
//			 if (words.get(jj).getCatTFIDF() > 0) {
//			 System.out.println("CATEGORY:"
//			 + this.globalCategoryCatalog.get(ii).getCategory()
//			 .getTitle() + " "
//			 + words.get(jj).getCatTFIDF() + ":"
//			 + words.get(jj).getText().getText());
//			 }
//			 }
//			 System.out
//			 .println("______________________________________________________________");
        }
    }

    /**
     * Calculates catTF for each word in the given pdf
     *
     * @param current
     * @param counter
     * @param catcat
     * @return
     */
    private PDF calculateCatTF(PDF current, int counter, CategoryCatalog catcat) {
        for (WordProperty pdfword : current.getWords()) {
            for (WordProperty word : catcat.getKeywordList()) {
                if (pdfword.getWord().getText()
                        .equals(word.getWord().getText())) {
                    current.getKeywords().get(counter)
                            .incwOcc(word.getOcc());
                    break;
                }
            }
        }
        return current;
    }

    /**
     * Calculates the icf part of tf-icf
     */
    public void calculateICF() {
        ArrayList<WordProperty> words = null;
        ArrayList<WordProperty> wordes = null;
        for (CategoryCatalog doc : this.globalCategoryCatalog) {
            words = doc.getKeywordList();
            for (WordProperty word : words) {
                if (word.getKeyinCat() == 0) {
                    for (CategoryCatalog currdoc : this.globalCategoryCatalog) {
                        wordes = currdoc.getKeywordList();
                        for (int ii = 0; ii < wordes.size(); ii++) {
                            if ((wordes.get(ii).getWord().getText().equals(word
                                    .getWord().getText()))
                                    && (!word.isCatRet())) {

                                word.incKeyinCat();

                                break;
                            }
                        }
                    }
                    word.setCatRet(true);
                }
            }
        }
        for (CategoryCatalog doc : this.globalCategoryCatalog) {
            words = doc.getKeywordList();
            for (WordProperty word : words) {
                word.setCatIDF(AlgorithmUtil.calcIDF(
                        // have to consider also occurence not only size
                        (double) this.globalCategoryCatalog.size(),
                        (double) word.getKeyinCat()));
            }
        }
    }

    /**
     * Calculate keyword relevance for every PDF of the corpus (adding TFICF
     * values of matching keywords from associated categories)
     */
    public void calculateAllPDFCatRel() {
        for (int ii = 0; ii < this.pdfList.size(); ii++) {
            ArrayList<Category> pdfcat = this.pdfList.get(ii)
                    .getKeywords();
            for (WordProperty word : pdfList.get(ii).getWords()) {
                for (int counter = 0; counter < pdfcat.size(); counter++) {
                    for (Category current : word.getWord().getCategory()) {
                        if (current.getTitle().equals(
                                pdfcat.get(counter).getTitle())) {
                            this.pdfList.get(ii).getKeywords()
                                    .get(counter)
                                    .incRelevance(word.getCatTFIDF());
                            // try to normalize -> avoid problem
                            this.pdfList.get(ii).getKeywords()
                                    .get(counter).incNormAdd();
                        }
                    }
                    this.pdfList.get(ii).getKeywords().get(counter)
                            .getRelevance();
                }
            }
            for (int counter = 0; counter < pdfcat.size(); counter++) {
                if (pdfcat.get(counter).getRelevance() == 0) {
                    pdfcat.get(counter).setRelevance(0.00001);
                }
            }
        }

    }

    /**
     * All TFICF values have to be above the defined leve, else remove word
     *
     * @param level
     */
    public void filterTFICF(double level) {
        for (int ii = 0; ii < globalCategoryCatalog.size(); ii++) {
            ArrayList<WordProperty> words = globalCategoryCatalog.get(ii)
                    .getKeywordList();
            ArrayList<WordProperty> relevantWords = new ArrayList<WordProperty>();

            for (int jj = 0; jj < words.size(); jj++) {

                if (words.get(jj).getTfidf() > level) {
                    relevantWords.add(words.get(jj));
                }

            }
            globalCategoryCatalog.get(ii).setKeywordList(relevantWords);
        }

    }
}
