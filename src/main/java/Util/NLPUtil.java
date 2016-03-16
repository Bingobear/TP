package Util;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import models.Category;
import models.WordOcc;
import models.Words;
import modules.Stemmer;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetector;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;

public class NLPUtil {
    /**
     * Generates WordOcc array -> erasing duplicates and counting words
     *
     * @param words
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<WordOcc> keyOcc(ArrayList<Words> words) {
        ArrayList<Words> keywords = new ArrayList<Words>();
        keywords = (ArrayList<Words>) words.clone();
        ArrayList<WordOcc> result = new ArrayList<WordOcc>();
        int arraySize = keywords.size();

        @SuppressWarnings("unused")
        int counter = 0;
        @SuppressWarnings("unused")
        int size = 0;
        while (arraySize > 0) {
            int count = 0;
            Words current = keywords.get(0);

            for (int ii = 0; ii < keywords.size(); ii++) {
                Words compare = keywords.get(ii);

                if (compare.getWord().equals(current.getWord())
                        || ((compare.getStem().equals(current.getStem())) && ((compare
                        .getType().contains(current.getType()) || (current
                        .getType().contains(compare.getType())))))) {
                    keywords.remove(ii);
                    count++;
                    arraySize--;
                } else if (AlgorithmUtil.LevenshteinDistance(current.getWord(),
                        compare.getWord()) < 0.2) {
                    keywords.remove(ii);
                    count++;
                    arraySize--;
                }
                counter = ii;
                size = keywords.size();
            }
            result.add(new WordOcc(current, count));
        }
        return result;
    }

    /**
     * Generate Word ArrayList -> filtering words type specific...
     *
     * @param filter
     * @param tokens
     * @param modes  : 0-Noun, 1-Noun&Verb, 2-Noun&Adjective
     * @return
     */
    public static ArrayList<Words> generateWords(String[] filter, String[] tokens,
                                                 int mode, String language, ArrayList<Category> keywords) {
        // ArrayList<Integer> result = new ArrayList<Integer>();

        ArrayList<Words> result = new ArrayList<Words>();
        // for eng and german
        Stemmer stem = new Stemmer();
        String[] stemmedW = stem.stem(tokens, language);

        if (mode == 0) {
            for (int ii = 0; ii < filter.length; ii++) {
                if ((filter[ii].contains("NN"))) {
                    if (!language.equals("de")) {
                        // System.out.println(tokens[ii]);
                        String text = tokens[ii].replaceAll("\\W", "");
                        if ((!text.isEmpty()) && (text.length() > 1)) {
                            Words word = new Words(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    } else {
                        // MAYBE SOLVES PROBLEM?TODO
                        String text = tokens[ii].replaceAll(
                                "[^\\p{L}\\p{Nd}]+", "");
                        if ((!text.isEmpty()) && (text.length() > 1)) {
                            Words word = new Words(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    }
                }
            }
        } else if (mode == 1) {
            for (int ii = 0; ii < filter.length; ii++) {
                if ((filter[ii].contains("NN")) || (filter[ii].contains("VB"))) {
                    if (!language.equals("de")) {
                        // System.out.println(tokens[ii]);
                        String text = tokens[ii].replaceAll("\\W", "");
                        if ((!text.isEmpty()) && (text.length() > 1)) {
                            Words word = new Words(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    } else {
                        // MAYBE SOLVES PROBLEM?TODO
                        String text = tokens[ii].replaceAll(
                                "[^\\p{L}\\p{Nd}]+", "");
                        if ((!text.isEmpty()) && (text.length() > 1)) {
                            Words word = new Words(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    }
                }
            }
        } else if (mode == 2) {
            for (int ii = 0; ii < filter.length; ii++) {
                if ((filter[ii].contains("NN")) || (filter[ii].contains("JJ"))) {
                    if (!language.equals("de")) {
                        String text = tokens[ii].replaceAll("\\W", "");
                        if ((!text.isEmpty()) && (text.length() > 1)) {
                            Words word = new Words(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    } else {
                        String text = tokens[ii].replaceAll(
                                "[^\\p{L}\\p{Nd}]+", "");
                        if ((!text.isEmpty()) && (text.length() > 1)) {
                            Words word = new Words(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    }
                }
            }
        }

        return result;
    }

    /**
     * Creates the SentenceDetector object (utilizes gloabal language to refer
     * to resource lang)
     *
     * @return
     */
    public static SentenceDetector sentencedetect(String language) {

        SentenceDetector _sentenceDetector = null;

        InputStream modelIn = null;
        try {
            // Loading sentence detection model
            if (language.equals("en")) {
                modelIn = NLPUtil.class.getResourceAsStream("/eng/en-sent.bin");
            } else {
                modelIn = NLPUtil.class.getResourceAsStream("/ger/de-sent.bin");
            }

            final SentenceModel sentenceModel = new SentenceModel(modelIn);
            modelIn.close();

            _sentenceDetector = new SentenceDetectorME(sentenceModel);

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {
                } // oh well!
            }
        }
        return _sentenceDetector;
    }

    /**
     * Converts text string to token array string[]
     *
     * @param parsedText
     * @return
     */
    public static String[] getTokenPM(String parsedText, String language) {
        SentenceDetector sentdetector = sentencedetect(language);
        String[] sentence = sentdetector.sentDetect(parsedText);
        ArrayList<String> tokensA = new ArrayList<String>();
        for (int ii = 0; ii < sentence.length; ii++) {
            String[] tokenSen = generalToken(sentence[ii], language);
            for (int jj = 0; jj < tokenSen.length; jj++) {
                tokensA.add(tokenSen[jj]);
            }
        }
        String[] tokens = new String[tokensA.size()];
        for (int ii = 0; ii < tokensA.size(); ii++) {
            tokens[ii] = tokensA.get(ii);

        }
        return tokens;
    }

    /**
     * Extracts tokens from a given text - sums sup sentence detection and
     * tokenization
     *
     * @param parsedText
     * @return string[]
     */
    public static String[] getToken(String parsedText, String language) {
        SentenceDetector sentdetector = sentencedetect(language);
        String[] sentence = sentdetector.sentDetect(parsedText);
        ArrayList<String> tokensA = new ArrayList<String>();
        String help = "";
        for (int ii = 0; ii < sentence.length; ii++) {
            String[] tokenSen = generalToken(sentence[ii], language);
            for (int jj = 0; jj < tokenSen.length; jj++) {
                help = tokenSen[jj].replaceAll("\\W", "");

                if ((!help.isEmpty()) && (help.length() > 2)) {
                    tokensA.add(tokenSen[jj]);
                } else if ((help.equals("-")) && (jj + 1 < tokenSen.length)) {
//					System.out.println(tokenSen[jj]);
                    String tokencomb = tokensA.get(tokensA.size() - 1) + "-"
                            + tokenSen[jj + 1];
                    jj++;
                    tokensA.add(tokencomb);
                    // System.out.println("NEW TOKEN"+tokensA.get(tokensA.size()-1));

                }

            }
        }
        String[] tokens = new String[tokensA.size()];
        for (int ii = 0; ii < tokensA.size(); ii++) {
            tokens[ii] = tokensA.get(ii);

        }
        return tokens;
    }

    /**
     * Tokenizes given String
     *
     * @param parsedText
     * @return string[]
     */
    public static String[] generalToken(String parsedText, String language) {
        Tokenizer _tokenizer = null;

        InputStream modelIn = null;
        String[] tokens = null;
        try {
            // Loading tokenizer model
            if (language.equals("en")) {
                modelIn = NLPUtil.class.getResourceAsStream("/eng/en-token.bin");
            } else {
                modelIn = NLPUtil.class.getResourceAsStream("/ger/de-token.bin");
            }
            final TokenizerModel tokenModel = new TokenizerModel(modelIn);
            modelIn.close();

            _tokenizer = new TokenizerME(tokenModel);
            tokens = _tokenizer.tokenize(parsedText);
        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {
                } // oh well!
            }
        }
        return tokens;
    }

    /**
     * Creates posttag array for a given text
     *
     * @param text
     * @return string[]
     */
    public static String[] posttags(String[] text, String language) {
        POSTaggerME posttagger = createposttagger(language);
        String[] result = posttagger.tag(text);
        return result;

    }

    private static POSTaggerME createposttagger(String language) {

        InputStream modelIn = null;
        POSTaggerME _posTagger = null;
        try {
            // Loading tokenizer model
            if (language.equals("en")) {
                modelIn = NLPUtil.class.getResourceAsStream(
                        "/eng/en-pos-maxent.bin");
            } else {
                modelIn = NLPUtil.class.getResourceAsStream(
                        "/ger/de-pos-maxent.bin");
            }

            final POSModel posModel = new POSModel(modelIn);
            modelIn.close();

            _posTagger = new POSTaggerME(posModel);

        } catch (final IOException ioe) {
            ioe.printStackTrace();
        } finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                } catch (final IOException e) {
                } // oh well!
            }
        }
        return _posTagger;

    }


    /**
     * Converts x pages (end-start) pdf to String
     *
     * @param pdfStripper
     * @param pdDoc
     * @param start       (starting page)
     * @param end         (ending page)
     * @return
     * @throws IOException
     */
    public static String parsePdftoString(PDFTextStripper pdfStripper,
                                          PDDocument pdDoc, int start, int end) throws IOException {

        pdfStripper.setStartPage(start);
        pdfStripper.setEndPage(end);
        String parsedText = pdfStripper.getText(pdDoc);
        return parsedText;
    }


}
