package modules;

import models.Category;
import org.junit.Test;


import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * Created by simonbruns on 15/03/16.
 */
public class KeywordHandlerTest {

    @Test
    public void testGetKeywordsFromPDF() throws Exception {
        KeywordHandler keywordHandler = new KeywordHandler();
        String[] testKeywords = new String[]{"Abstract", "keyword", ":", "Optimization", ",", "LaserSD", ",",
                "Information Visualization", ",", "DesigningIS", ",", "ObjectiveOP", ".", "Introduction"};
        ArrayList<Category> cats = keywordHandler.getKeywordsFromPDF(testKeywords);
        assertEquals(5, cats.size());
    }
}