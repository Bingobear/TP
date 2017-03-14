package modules;

import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.germanStemmer;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Stemmer Class - generates stemmed version of a given array
 *
 * @author Simon Bruns
 */
public class Stemmer {

    /**
     * generates stemmed version of a given array
     *
     * @param tokens
     * @param lang   (ger-german,en-english)
     * @return stemmed array
     */
    public List<String> stem(List<String> tokens, String lang) {
        if (lang.contains("en")) {
            return tokens.stream()
                    .map(this::getEnglishWordStem)
                    .collect(Collectors.toList());
        }
        else {
            return tokens.stream()
                    .map(this::getGermanWordStem)
                    .collect(Collectors.toList());
        }
    }

    private String getEnglishWordStem(String token) {
        englishStemmer stemmer = new englishStemmer();
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }

    private String getGermanWordStem(String token) {
        germanStemmer stemmer = new germanStemmer();
        stemmer.setCurrent(token);
        stemmer.stem();
        return stemmer.getCurrent();
    }

}
