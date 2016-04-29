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
    public static final String BRACKET_OPEN = "(";
    public static final String BRACKET_CLOSED = ")";
    public static final String KEYWORDS_START_SYMBOL_TWO = "keywords";
    public static final String KEYWORDS_START_SYMBOL_THREE = "terms";
    public static final String KEYWORDS_START_SYMBOL_ONE = ":";
    private ArrayList<Category> keywords = new ArrayList<Category>();
    private String currentAkronom = "";
    private String currentKeyword = "";
    private int endPosition;
    private int catnumb;

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
                if (isSeparator(keywordPassage.get(ii), seperator)) {
                    resolveCompletedKeyword();
                } else {
                    addNextKeywordSegmentToCurrentKeyword(keywordPassage.get(ii));
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
        int startPosition = KeywordUtil.findKeyWStart(textPDF);
        if (startPosition > 0) {
            textPDF = extractStartKeywordPassage(textPDF);
            ArrayList<String> extractedPassage = extractEndKeywordPassage(textPDF);
            return extractedPassage;
        } else {
            throw new InvalidPDF();
        }

    }

    private void resolveCompletedKeyword() {
        currentAkronom = KeywordUtil.getAkronom(currentKeyword);
        trimKeywordofArtifacts();
        String normalizedKeyword = normalizeCurrentKeyword();
        if (isValidKeyword(normalizedKeyword)) {
            keywords.add(new Category(currentKeyword, normalizedKeyword, currentAkronom));
        }
        prepareForNextKeyword();
    }

    private void prepareForNextKeyword() {
        currentAkronom = "";
        currentKeyword = "";
    }

    private void addNextKeywordSegmentToCurrentKeyword(String nextKeywordSegment) {
        currentKeyword = currentKeyword + " " + nextKeywordSegment;
    }

    private void trimKeywordofArtifacts() {
        currentKeyword = removeAkronomFromKeyword();
        currentKeyword = currentKeyword.replaceFirst(REGEX_SPECIAL_SYMBOLS, REPLACEMENT_EMPTY);
        currentKeyword = currentKeyword.trim();
    }

    private String removeAkronomFromKeyword() {
        if (!currentAkronom.isEmpty()) {
            currentKeyword = currentKeyword.replaceAll(BRACKET_OPEN + currentAkronom + BRACKET_CLOSED, REPLACEMENT_EMPTY);
            currentKeyword = currentKeyword.replace(BRACKET_CLOSED, REPLACEMENT_EMPTY);
        }
        return currentKeyword;
    }

    private ArrayList<String> extractStartKeywordPassage(ArrayList<String> textPDF) {
        int startPosition = KeywordUtil.findKeyWStart(textPDF);

        if (textPDF.get(startPosition).equals(KEYWORDS_START_SYMBOL_ONE)) {
            startPosition++;
        }
        if (textPDF.get(startPosition).contains(KEYWORDS_START_SYMBOL_TWO)) {
            String value = textPDF.get(startPosition);
            textPDF.set(startPosition, value.replaceAll(KEYWORDS_START_SYMBOL_TWO, REPLACEMENT_EMPTY));
        }
        if (textPDF.get(startPosition).contains(KEYWORDS_START_SYMBOL_THREE)) {
            String value = textPDF.get(startPosition);
            textPDF.set(startPosition, value.replaceAll(KEYWORDS_START_SYMBOL_THREE, REPLACEMENT_EMPTY));
        }
        return new ArrayList<String>(textPDF.subList(startPosition,
                textPDF.size() - 1));
    }

    private ArrayList<String> extractEndKeywordPassage(ArrayList<String> textPDF) {
        endPosition = KeywordUtil.findKeyWEnd(textPDF);
        return new ArrayList<String>(textPDF.subList(0, endPosition));
    }

    private void resolveLastKeyword() {
        if (isValidKeywordCandidate()) {
            currentKeyword = currentKeyword.replace("1", REPLACEMENT_EMPTY);
        }
        currentAkronom = KeywordUtil.getAkronom(currentKeyword);
        removeAkronomFromKeyword();
        currentKeyword = currentKeyword.replaceFirst(REGEX_SPECIAL_SYMBOLS, REPLACEMENT_EMPTY);
        if (currentKeyword.endsWith(".")) {
            currentKeyword = currentKeyword.substring(0, currentKeyword.length() - 1);
        }
        currentKeyword = currentKeyword.trim();
        String normalizedKeyword = normalizeCurrentKeyword();
        if (isValidKeyword(normalizedKeyword)) {
            keywords.add(new Category(currentKeyword, normalizedKeyword, currentAkronom));
        }
    }

    private boolean isValidKeywordCandidate() {
        return (currentKeyword.charAt(currentKeyword.length() - 1) == '1')
                && (!currentKeyword.isEmpty());
    }

    private String normalizeCurrentKeyword() {
        return currentKeyword.replaceAll(REGEX_SPECIAL_SYMBOLS, REPLACEMENT_EMPTY);
    }

    private boolean isSeparator(String keywordSegment, String seperator) {
        return keywordSegment.equals(seperator);
    }

    private boolean isLastKeywordValid() {
        return (currentKeyword.length() < 100) && (currentKeyword.length() > 2);
    }

    private boolean isValidKeyword(String normKey) {
        return (!currentKeyword.isEmpty()) && (!normKey.isEmpty());
    }

    private void setNumberofCategories(int numberofCategories) {
        catnumb = numberofCategories;
    }

    public int getEndPosition() {
        return endPosition;
    }
}

