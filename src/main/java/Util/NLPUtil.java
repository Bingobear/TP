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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static Util.AlgorithmUtil.LevenshteinDistance;

public class NLPUtil {
    /**
     * Generates WordProperty array -> erasing duplicates and counting words
     *
     * @param words
     * @return
     */
    public static ArrayList<WordProperty> keyOcc(ArrayList<Word> words) {
        ArrayList<Word> keywords = (ArrayList<Word>) words.clone();
        ArrayList<WordProperty> result = new ArrayList<>();

        for (Word current : words) {
            ArrayList<Word> foundWords = new ArrayList<>();

            if (!keywords.contains(current)) {
                continue;
            }

            for (Word compare : keywords) {
                if ((LevenshteinDistance(current.getText(), compare.getText()) < 0.2) || hasSameWordOrigin(current, compare)) {
                    foundWords.add(compare);
                }
            }

            keywords.removeAll(foundWords);
            result.add(new WordProperty(current, foundWords.size() + 1));
        }
        return result;
    }

    private static boolean hasSameWordOrigin(Word current, Word compare) {
        return current.getStem().equals(compare.getStem()) &&
                (current.getType().contains(compare.getType()) || compare.getType().contains(current.getType()));
    }

    /**
     * Generate Word ArrayList -> filtering words type specific...
     *
     * @param filter
     * @param tokens
     * @param types  : List of filter types
     * @return
     */
    public static ArrayList<Word> generateWords(String[] filter, List<String> tokens,
                                                List<WordTypeFilter> types, String language, ArrayList<Category> keywords) {
        ArrayList<Word> result = new ArrayList<>();
        // for eng and german
        Stemmer stem = new Stemmer();
        List<String> stemmedW = stem.stem(tokens, language);

        for (int ii = 0; ii < filter.length; ii++) {
            for (String type : retrieveWordTypes(types)) {
                if ((filter[ii].contains(type))) {
                    if (isNotGerman(language)) {
                        String text = tokens.get(ii).replaceAll("\\W", "");
                        if (text.length() > 1) {
                            Word word = new Word(text, stemmedW.get(ii),
                                    filter[ii], keywords);
                            result.add(word);
                        }
                    }
                    else {
                        String text = tokens.get(ii).replaceAll(
                                "[^\\p{L}\\p{Nd}]+", "");
                        if (text.length() > 1) {
                            Word word = new Word(text, stemmedW.get(ii),
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
        return types.stream()
                .map(WordTypeFilter::getTypes)
                .collect(Collectors.toList());
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
    private static SentenceDetector sentencedetect(String language) {

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
        String[] sentences = parseSentence(parsedText, language);
        ArrayList<String> tokens = new ArrayList<>();

        for (String sentence : sentences) {
            String[] tokenSen = generalToken(sentence, language);
            Collections.addAll(tokens, tokenSen);
        }

        return tokens.toArray(new String[0]);
    }

    //TODO also consider words with '-' e.g. net-working

    /**
     * Extracts tokens from a given text - sums sup sentence detection and
     * tokenization
     *
     * @param parsedText
     * @return string[]
     */
    public static List<String> getToken(String parsedText, String language) {
        String[] sentence = parseSentence(parsedText, language);
        ArrayList<String> tokens = new ArrayList<>();

        for (String aSentence : sentence) {
            List<String> tokenSen = Arrays.asList(generalToken(aSentence, language));
            final List<String> sentenceToken = tokenSen.stream()
                    .map(x -> x.replaceAll("\\W", ""))
                    .filter(x -> !x.isEmpty())
                    .filter(x -> x.length() > 2)
                    .collect(Collectors.toList());

            tokens.addAll(sentenceToken);
        }
        return tokens;
    }

    private static String[] parseSentence(String parsedText, String language) {
        SentenceDetector sentdetector = sentencedetect(language);
        return sentdetector.sentDetect(parsedText);
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
        return posttagger.tag(text);

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
        return pdfStripper.getText(pdDoc);
    }

}