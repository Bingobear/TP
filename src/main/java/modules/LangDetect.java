package modules;

import Util.AlgorithmUtil;

import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;

/**
 * Language Interface - Detects a given Strings language
 * 
 * @author Simon Bruns
 * 
 */
public class LangDetect {

	public String detect(String parsedText)
			throws LangDetectException {
		String text = "";
		if (parsedText.length() < 150) {
			text = parsedText;
		} else {
			text = parsedText.substring(150);
		}

		if (AlgorithmUtil.first) {
			ClassLoader classLoader = getClass().getClassLoader();
			DetectorFactory.loadProfile(classLoader.getResource("res/profiles").getFile());
			AlgorithmUtil.first = false;
		}
		Detector detector = DetectorFactory.create();
		detector.append(text);

		String lang = detector.detect();
		return lang;
	}

}
