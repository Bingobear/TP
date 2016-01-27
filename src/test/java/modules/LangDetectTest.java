package modules;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cybozu.labs.langdetect.LangDetectException;

public class LangDetectTest {

	@Test
	public void testGerDetect() throws LangDetectException {
		LangDetect detect = new LangDetect();
		String lang = detect.detect("Guten Abend die Herren", false);
		assertEquals("de", lang);
	}

	@Test
	public void testEngDetect() throws LangDetectException {
		LangDetect detect = new LangDetect();
		String lang = detect.detect(
				"Hello darkness my old friend. Nice to see you again", false);
		assertEquals("en", lang);
	}
	
	@Test(expected=LangDetectException.class)
	public void testNoLangDetect() throws LangDetectException {
		LangDetect detect = new LangDetect();
		String lang = detect.detect("§$%&/()=", false);
		assertEquals("unknown", lang); // unreached
	}

}
