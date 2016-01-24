package modules;

import org.tartarus.snowball.ext.englishStemmer;
import org.tartarus.snowball.ext.germanStemmer;

/** Stemmer Class - generates stemmed version of a given array
 * @author Simon Bruns
 *
 */
public class Stemmer {

	/**generates stemmed version of a given array
	 * @param tokens
	 * @param lang (ger-german,en-english)
	 * @return stemmed array
	 */
	public String[] stem(String[] tokens, String lang) {
		String[] stemtokens = new String[tokens.length];
		if (lang.contains("en")) {
			englishStemmer stemmer = new englishStemmer();
			for (int ii = 0; ii < tokens.length; ii++) {
				stemmer.setCurrent(tokens[ii]);
				stemmer.stem();
				stemtokens[ii] = stemmer.getCurrent();
			}
		} else {
			germanStemmer stemmer = new germanStemmer();
			for (int ii = 0; ii < tokens.length; ii++) {
				stemmer.setCurrent(tokens[ii]);
				stemmer.stem();
				stemtokens[ii] = stemmer.getCurrent();
			}
		}
		return stemtokens;
	}

}
