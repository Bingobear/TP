package Util;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class KeywordUtilTest {
	ArrayList<String> textPDF;

	@Before
	public void setup() {

		String testStr = "Our research provides valuable insights for stakeholders and contributes to the research on acceptance of energy infrastructures by providing a cross-sectional view. Keywords: energy infrastructure, technology acceptance, technical selfefficacy, user diversity, renewable energies. 1 Introduction The ongoing diffusion";
		testStr = testStr.toLowerCase();
		String[] tokens = NLPUtil.getTokenPM(testStr, "en");
		textPDF = new ArrayList<String>(Arrays.asList(tokens));
	}

	@Test
	public void testFindSep() {
		assertEquals(",", KeywordUtil.findSep(textPDF));
	}
	
	@Test
	public void testFindNoSep() {
		String testStr = "energy infrastructure technology acceptance";
		testStr = testStr.toLowerCase();
		String[] tokens = NLPUtil.getTokenPM(testStr, "en");
		ArrayList<String> textnoKey = new ArrayList<String>(Arrays.asList(tokens));
		assertNull(KeywordUtil.findSep(textnoKey));
	}

	@Test
	public void testFindKeyWStart() {
		assertEquals(24, KeywordUtil.findKeyWStart(textPDF));

	}
	
	@Test
	public void testFindKeyWEnd() {
		int start = KeywordUtil.findKeyWStart(textPDF);
		ArrayList<String> textend = new ArrayList<String>(textPDF.subList(
				start, textPDF.size() - 1));
		// 39
		assertEquals(15 + start, KeywordUtil.findKeyWEnd(textend) + start);

	}
	
	@Test
	public void testNoKeyWStart() {
		String testStr = "energy infrastructure, technology acceptance";
		testStr = testStr.toLowerCase();
		String[] tokens = NLPUtil.getTokenPM(testStr, "en");
		ArrayList<String> textnoKey = new ArrayList<String>(Arrays.asList(tokens));
		assertEquals(-1,KeywordUtil.findKeyWStart(textnoKey));

	}



}
