package modules;

import Util.KeywordUtil;
import models.Category;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by simonbruns on 15/03/16.
 */
public class KeywordExtractor {

    public static final String REGEX_SPECIAL_SYMBOLS = "[^\\p{L}]+";
    public static final String REPLACEMENT_EMPTY = "";
    private ArrayList<Category> keywords = new ArrayList<Category>();
    private String currentAkronom ="";
    private String currentKeyword="";
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
            String seperator = KeywordUtil.findSep(keywordPassage);
            for (int ii = 0; ii < keywordPassage.size(); ii++) {
                if (keywordPassage.get(ii).equals(seperator)) {
                    resolveCompletedKeyword();
                } else {
                    addNextKeywordSegmentToCurrentKeyword(keywordPassage, ii);
                }
            }
            if (isLastKeywordValid()) {
                resolveLastKeyword();
            }
            setNumberofCategories(keywords.size());
        }
        return keywords;
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

    private void resolveCompletedKeyword() {
        currentAkronom = KeywordUtil.getAkronom(currentKeyword);
        trimKeywordofArtifacts();
        String normalizedKeyword = normalize();
        if (isValidKeyword(normalizedKeyword)) {
            keywords.add(new Category(currentKeyword, normalizedKeyword, currentAkronom));
        }
        resetCurrentKeyword();
    }

    private void resetCurrentKeyword() {
        currentAkronom = "";
        currentKeyword = "";
    }

    private void addNextKeywordSegmentToCurrentKeyword(ArrayList<String> keywordPassage, int ii) {
        currentKeyword = currentKeyword + " " + keywordPassage.get(ii);
    }

    private void trimKeywordofArtifacts() {
        currentKeyword = removeAkronomFromKeyword();
        currentKeyword = currentKeyword.replaceFirst(REGEX_SPECIAL_SYMBOLS, REPLACEMENT_EMPTY);
        currentKeyword = currentKeyword.trim();
    }

    private String removeAkronomFromKeyword() {
        if (!currentAkronom.isEmpty()) {
            currentKeyword = currentKeyword.replaceAll("(" + currentAkronom + ")", REPLACEMENT_EMPTY);
            currentKeyword = currentKeyword.replace(")", REPLACEMENT_EMPTY);
        }
        return currentKeyword;
    }

    private ArrayList<String> extractStartKeywordPassage(ArrayList<String> textPDF) {
        int startPosition = KeywordUtil.findKeyWStart(textPDF);

        if (textPDF.get(startPosition).equals(":")) {
            startPosition++;
        }
        if (textPDF.get(startPosition).contains("keywords")) {
            String value = textPDF.get(startPosition);
            textPDF.set(startPosition, value.replaceAll("keywords", REPLACEMENT_EMPTY));
        }
        if (textPDF.get(startPosition).contains("terms")) {
            String value = textPDF.get(startPosition);
            textPDF.set(startPosition, value.replaceAll("terms", REPLACEMENT_EMPTY));
        }
        return new ArrayList<String>(textPDF.subList(startPosition,
                textPDF.size() - 1));
    }

    private ArrayList<String> extractEndKeywordPassage(ArrayList<String> textPDF) {
        return new ArrayList<String>(textPDF.subList(0, endPosition));
    }

    private void resolveLastKeyword() {
        if ((currentKeyword.charAt(currentKeyword.length() - 1) == '1')
                && (!currentKeyword.isEmpty())) {
            currentKeyword = currentKeyword.replace("1", REPLACEMENT_EMPTY);
        }
        removeAkronomFromKeyword();
        currentKeyword = currentKeyword.replaceFirst(REGEX_SPECIAL_SYMBOLS, REPLACEMENT_EMPTY);
        if (currentKeyword.endsWith(".")) {
            currentKeyword = currentKeyword.substring(0, currentKeyword.length() - 1);
        }
        currentKeyword = currentKeyword.trim();
        String normalizedKeyword = normalize();
        if (isValidKeyword(normalizedKeyword)) {
            keywords.add(new Category(currentKeyword, normalizedKeyword, currentAkronom));
        }
    }

    private String normalize() {
        return currentKeyword.replaceAll(REGEX_SPECIAL_SYMBOLS, REPLACEMENT_EMPTY);
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

