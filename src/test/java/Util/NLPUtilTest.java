package Util;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class NLPUtilTest {
	String[] tokens;
	String[] filter;
	String testText;

	@Before
	public void setup() {
		testText = "Mama, just killed a man, Put a gun against his head, Pulled my trigger, now he's dead. Mama, life had just begun, But now I've gone and thrown it all away.";
		tokens = NLPUtil.getToken(testText, "en");
		filter = NLPUtil.posttags(tokens, "en");
	}

	@Test
	public void testKeyOcc() {
		String parsedText = "Fischers Fritze fischt frische Fische;Frische Fische fischt Fischers Fritze.";
		String[] tokens = NLPUtil.getToken(parsedText, "en");
		String[] filter = NLPUtil.posttags(tokens, "en");
		assertEquals(
				6,
				NLPUtil.keyOcc(
						NLPUtil.generateWords(filter, tokens, 0, "de", null))
						.size());
	}

	@Test
	public void testGenerateWordsEmpty() {
		String [] tokenemp=new String[]{""};
		String [] filteremp=new String[]{""};
		assertEquals(0, NLPUtil.generateWords(filteremp, tokenemp, 0, "en", null)
				.size());
	}
	
	@Test
	public void testGenerateWordsNouns() {
		assertEquals(7, NLPUtil.generateWords(filter, tokens, 0, "en", null)
				.size());
	}

	@Test
	public void testGenerateWordsNounsVerbs() {
		assertEquals(14, NLPUtil.generateWords(filter, tokens, 1, "en", null)
				.size());
	}

	@Test
	public void testGenerateWordsNounsAdjectives() {
		assertEquals(8, NLPUtil.generateWords(filter, tokens, 2, "en", null)
				.size());
	}

	@Test(expected = AssertionError.class)
	public void testGenerateWordsNotKnownModeSelected() {
		assertEquals(8, NLPUtil.generateWords(filter, tokens, 5, "en", null)
				.size());
	}

	@Test
	public void testGetTokenWithPuncMark() {
		assertEquals(41,NLPUtil.getTokenPM(testText, "en").length);
	}

	@Test
	public void testGetTokenWithoutPuncMark() {
		assertEquals(25, NLPUtil.getToken(testText, "en").length);
	}



}
