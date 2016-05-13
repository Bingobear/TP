package models;

import com.cybozu.labs.langdetect.LangDetectException;
import modules.LangDetect;

/**
 * Created by simonbruns on 13/05/16.
 */
public class BasicText {
    private String text;
    private String language;

    public BasicText(String medium) throws LangDetectException {
        medium = medium.toLowerCase();
        setText(medium);
        detectLanguage();

    }

    public String getLanguage() {
        return language;
    }

    private void setLanguage(String language) {
        this.language = language;
    }

    public String getText() {
        return text;
    }

    private void setText(String medium) {
        this.text = medium;
    }

    private void detectLanguage() throws LangDetectException {
        LangDetect lang = new LangDetect();
        setLanguage(lang.detect(getText()));
    }
}
