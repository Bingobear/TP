package modules;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class LangDetectTest {


	@Test
	public void testGerDetect() {
		LangDetect detect = new LangDetect();
		String lang = null;
		try {
			lang = detect.detect("Guten Abend die Herren Schnösel", false);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("de",lang);
	}
	@Test
	public void testEngDetect() {
		LangDetect detect = new LangDetect();
		String lang = null;
		try {
			lang = detect.detect("Hello darkness my old friend. Nice to see you again", false);
		} catch (LangDetectException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals("en",lang);
	}

}
