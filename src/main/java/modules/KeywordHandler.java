package modules;

import Util.KeywordUtil;
import models.Category;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by simonbruns on 15/03/16.
 */
public class KeywordHandler {

    private ArrayList<Category> keywords = new ArrayList<Category>();
    private String akronom;
    private String currentKeyword;
    private String seperator;
    private int catnumb;
    private int endPosition;

    /**
     * Extracts keywords from a given pdf (token string[]=tokenpm)
     *
     * @param tokens
     * @return
     */
    public ArrayList<Category> getKeywordsFromPDF(String[] tokens) throws InvalidPDF {
        ArrayList<String> textPDF = new ArrayList<String>(Arrays.asList(tokens));
        ArrayList<String> keywordPassage = extractKeywordPassage(textPDF);
        if (!keywordPassage.isEmpty()) {
            seperator = KeywordUtil.findSep(keywordPassage);
            akronom = "";
            currentKeyword = "";
            for (int ii = 0; ii < keywordPassage.size(); ii++) {
                if (keywordPassage.get(ii).equals(seperator)) {

                    resolveCurrentKeyword();
                    akronom = "";
                    currentKeyword = "";

                } else if (keywordPassage.get(ii).contains("(")) {
                    akronom = KeywordUtil.getAkronom(new ArrayList<String>(keywordPassage.subList(
                            ii, keywordPassage.size())));
                } else {

                    currentKeyword = currentKeyword + " " + keywordPassage.get(ii);

                }
            }
            if (isLastKeywordValid()) {
                resolveLastKeyword();
            }
            setNumberofCategories(keywords.size());
        }
        return keywords;
    }

    private void resolveCurrentKeyword() {
        currentKeyword = removeAkronomFromKeyword();
        currentKeyword = currentKeyword.replaceFirst("[^\\p{L}]+", "");
        currentKeyword = currentKeyword.trim();
        String normKey = currentKeyword.replaceAll("[^\\p{L}]+", "");
        if (isValidKeyword(normKey)) {
            keywords.add(new Category(currentKeyword, normKey, akronom));
        }
    }

    private String removeAkronomFromKeyword() {
        if (!akronom.isEmpty()) {
            currentKeyword = currentKeyword.replaceAll("(" + akronom + ")", "");
            currentKeyword = currentKeyword.replace(")", "");
        }
        return currentKeyword;
    }

    private ArrayList<String> extractKeywordPassage(ArrayList<String> textPDF) throws InvalidPDF {
        ArrayList<String> extractedPassage = new ArrayList<String>();
        int startPosition = KeywordUtil.findKeyWStart(textPDF);

        if (startPosition > 0) {
            textPDF = extractStartKeywordPassage(textPDF);
            endPosition = startPosition;
            endPosition = KeywordUtil.findKeyWEnd(textPDF);

            extractedPassage = extractEndKeywordPassage(textPDF);
            return extractedPassage;
        } else {
            throw new InvalidPDF();
        }

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

    private void resolveLastKeyword() {
        if ((currentKeyword.charAt(currentKeyword.length() - 1) == '1')
                && (!currentKeyword.isEmpty())) {
            currentKeyword = currentKeyword.replace("1", "");
        }
        removeAkronomFromKeyword();
        currentKeyword = currentKeyword.replaceFirst("[^\\p{L}]+", "");
        if (currentKeyword.endsWith(".")) {
            currentKeyword = currentKeyword.substring(0, currentKeyword.length() - 1);
        }
        currentKeyword = currentKeyword.trim();
        String normKey = currentKeyword.replaceAll("[^\\p{L}]+", "");
        if (isValidKeyword(normKey)) {
            keywords.add(new Category(currentKeyword, normKey, akronom));
        }
    }

    private boolean isLastKeywordValid() {
        return (currentKeyword.length() < 100) && (currentKeyword.length() > 2);
    }

    private boolean isValidKeyword(String normKey) {
        return (!currentKeyword.isEmpty()) && (!normKey.isEmpty());
    }

    private void setNumberofCategories(int numberofCategories) {
        this.catnumb = numberofCategories;
    }

    public int getEndPosition() {
        return endPosition;
    }
}

