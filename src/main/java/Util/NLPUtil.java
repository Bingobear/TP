package Util;

import models.Category;
import models.Word;
import models.WordProperty;
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class NLPUtil {
    /**
     * Generates WordProperty array -> erasing duplicates and counting words
     *
     * @param words
     * @return
     */
    @SuppressWarnings("unchecked")
    public static ArrayList<WordProperty> keyOcc(ArrayList<Word> words) {
        ArrayList<Word> keywords = new ArrayList<Word>();
        keywords = (ArrayList<Word>) words.clone();
        ArrayList<WordProperty> result = new ArrayList<WordProperty>();
        int arraySize = keywords.size();

        @SuppressWarnings("unused")
        int counter = 0;
        @SuppressWarnings("unused")
        int size = 0;
        while (arraySize > 0) {
            int count = 0;
            Word current = keywords.get(0);

            for (int ii = 0; ii < keywords.size(); ii++) {
                Word compare = keywords.get(ii);

                if (compare.getText().equals(current.getText())
                        || ((compare.getStem().equals(current.getStem())) && ((compare
                        .getType().contains(current.getType()) || (current
                        .getType().contains(compare.getType())))))) {
                    keywords.remove(ii);
                    count++;
                    arraySize--;
                }
                else if (AlgorithmUtil.LevenshteinDistance(current.getText(),
                        compare.getText()) < 0.2) {
                    keywords.remove(ii);
                    count++;
                    arraySize--;
                }
                counter = ii;
                size = keywords.size();

            }
            result.add(new WordProperty(current, count));
        }
        return result;
    }

    /**
     * Generate Word ArrayList -> filtering words type specific...
     *
     * @param filter
     * @param tokens
     * @param types  : List of filter types
     * @return
     */
    public static ArrayList<Word> generateWords(String[] filter, String[] tokens,
                                                List<WordTypeFilter> types, String language, ArrayList<Category> keywords) {
        // ArrayList<Integer> result = new ArrayList<Integer>();
        ArrayList<Word> result = new ArrayList<>();
        // for eng and german
        Stemmer stem = new Stemmer();
        String[] stemmedW = stem.stem(tokens, language);

        for (int ii = 0; ii < filter.length; ii++) {
            for (String type : retrieveWordTypes(types)) {
                if ((filter[ii].contains(type))) {
                    if (isNotGerman(language)) {
                        String text = tokens[ii].replaceAll("\\W", "");
                        if (text.length() > 1) {
                            Word word = new Word(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    }
                    else {
                        String text = tokens[ii].replaceAll(
                                "[^\\p{L}\\p{Nd}]+", "");
                        if (text.length() > 1) {
                            Word word = new Word(text, stemmedW[ii],
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    }
                }
            }
        }

        return result;
    }

    private static List<String> retrieveWordTypes(List<WordTypeFilter> types) {
        return types.stream().map(WordTypeFilter::getTypes).collect(Collectors.toList());
    }

    private static boolean isNotGerman(String language) {
        return !language.equals("de");
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
            }
            else {
                modelIn = NLPUtil.class.getResourceAsStream("/ger/de-sent.bin");
            }

            final SentenceModel sentenceModel = new SentenceModel(modelIn);
            modelIn.close();

            _sentenceDetector = new SentenceDetectorME(sentenceModel);

        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (final IOException e) {
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
                }
                else if ((help.equals("-")) && (jj + 1 < tokenSen.length)) {
                    String tokencomb = tokensA.get(tokensA.size() - 1) + "-"
                            + tokenSen[jj + 1];
                    jj++;
                    tokensA.add(tokencomb);
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
            }
            else {
                modelIn = NLPUtil.class.getResourceAsStream("/ger/de-token.bin");
            }
            final TokenizerModel tokenModel = new TokenizerModel(modelIn);
            modelIn.close();

            _tokenizer = new TokenizerME(tokenModel);
            tokens = _tokenizer.tokenize(parsedText);
        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (final IOException e) {
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
            }
            else {
                modelIn = NLPUtil.class.getResourceAsStream(
                        "/ger/de-pos-maxent.bin");
            }

            final POSModel posModel = new POSModel(modelIn);
            modelIn.close();

            _posTagger = new POSTaggerME(posModel);

        }
        catch (final IOException ioe) {
            ioe.printStackTrace();
        }
        finally {
            if (modelIn != null) {
                try {
                    modelIn.close();
                }
                catch (final IOException e) {
                } // oh well!
            }
        }
        return _posTagger;

    }


    /**
     * Converts x pages (end-start) pdf to String
     *
     * @param pdDoc
     * @param start (starting page)
     * @param end   (ending page)
     * @return
     * @throws IOException
     */
    public static String parsePdftoString(PDDocument pdDoc, int start, int end) throws IOException {
        PDFTextStripper pdfStripper = new PDFTextStripper();
        pdfStripper.setStartPage(start);
        pdfStripper.setEndPage(end);
        String parsedText = pdfStripper.getText(pdDoc);
        return parsedText;
    }


}
