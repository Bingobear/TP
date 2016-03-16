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
    private String currKey;
    private String seperator;
    private int catnumb;

    public int getEndPosition() {
        return endPosition;
    }

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
            currKey = "";
            for (int ii = 0; ii < keywordPassage.size(); ii++) {
                if (keywordPassage.get(ii).equals(seperator)) {

                    resolveCurrentKeyword();
                    akronom = "";
                    currKey = "";

                } else if (keywordPassage.get(ii).contains("(")) {
                    akronom = KeywordUtil.getAkronom(new ArrayList<String>(keywordPassage.subList(
                            ii, keywordPassage.size())));
                } else {

                    currKey = currKey + " " + keywordPassage.get(ii);

                }
            }
            if (isLastKeywordValid()) {
                resolveLastKeyword();
            }
            setCatnumb(keywords.size());
        }
        return keywords;
    }

    private void resolveCurrentKeyword() {
        currKey = removeAkronomFromKeyword();
        currKey = currKey.replaceFirst("[^\\p{L}]+", "");
        currKey = currKey.trim();
        String normKey = currKey.replaceAll("[^\\p{L}]+", "");
        if (isValidKeyword(normKey)) {
            keywords.add(new Category(currKey, normKey, akronom));
        }
    }

    private String removeAkronomFromKeyword() {
        if (!akronom.isEmpty()) {
            currKey = currKey.replaceAll("(" + akronom + ")", "");
            currKey = currKey.replace(")", "");
        }
        return currKey;
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
        if ((currKey.charAt(currKey.length() - 1) == '1')
                && (!currKey.isEmpty())) {
            currKey = currKey.replace("1", "");
        }
        removeAkronomFromKeyword();
        currKey = currKey.replaceFirst("[^\\p{L}]+", "");
        if (currKey.endsWith(".")) {
            currKey = currKey.substring(0, currKey.length() - 1);
        }
        currKey = currKey.trim();
        String normKey = currKey.replaceAll("[^\\p{L}]+", "");
        if (isValidKeyword(normKey)) {
            keywords.add(new Category(currKey, normKey, akronom));
        }
    }

    private boolean isLastKeywordValid() {
        return (currKey.length() < 100) && (currKey.length() > 2);
    }

    private boolean isValidKeyword(String normKey) {
        return (!currKey.isEmpty()) && (!normKey.isEmpty());
    }

    public void setCatnumb(int catnumb) {
        this.catnumb = catnumb;
    }
}

