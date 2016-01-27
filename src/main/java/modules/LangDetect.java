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

	public String detect(String parsedText, boolean first)
			throws LangDetectException {
		String text = "";
		if (parsedText.length() < 150) {
			text = parsedText;
		} else {
			text = parsedText.substring(150);
		}

		if (AlgorithmUtil.first) {
			DetectorFactory.loadProfile("res/profiles");
			AlgorithmUtil.first = false;
		}
		Detector detector = DetectorFactory.create();
		detector.append(text);

		String lang = detector.detect();
		return lang;
	}

}
