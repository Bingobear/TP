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
    public void testGetKeywordsFromStandardPDF() throws Exception {
        KeywordHandler keywordHandler = new KeywordHandler();
        String[] testKeywords = new String[]{"Abstract", "keyword", ":", "Optimization", ",", "LaserSD", ",",
                "Information Visualization", ",", "DesigningIS", ",", "ObjectiveOP", ".", "Introduction"};
        ArrayList<Category> cats = keywordHandler.getKeywordsFromPDF(testKeywords);
        assertEquals(5, cats.size());
    }

    @Test
    public void testGetKeywordsFromPDFWith2Keywords() throws Exception {
        KeywordHandler keywordHandler = new KeywordHandler();
        String[] testKeywords = new String[]{"Abstract", "keyword", ":", "Optimization", ",", "LaserSD", ".", "Introduction"};
        ArrayList<Category> cats = keywordHandler.getKeywordsFromPDF(testKeywords);
        assertEquals(2, cats.size());
    }

    @Test
    public void testGetKeywordsFromPDFWithNoEndingKeyword() throws Exception {
        KeywordHandler keywordHandler = new KeywordHandler();
        String[] testKeywords = new String[]{"Abstract", "keyword", ":", "Optimization", ",", "LaserSD", ",","InfoViz "," to all the nice guys who live here"};
        ArrayList<Category> cats = keywordHandler.getKeywordsFromPDF(testKeywords);
        assertEquals(2, cats.size());
    }

    @Test(expected = InvalidPDF.class)
    public void testGetKeywordsFromNoKeywordPassagePDF() throws Exception {
        KeywordHandler keywordHandler = new KeywordHandler();
        String[] testKeywords = new String[]{"Hello", "darkness", "my", "old", "friend",
                "its", "nice", "to", "see", "you", "again"};
        ArrayList<Category> cats = keywordHandler.getKeywordsFromPDF(testKeywords);
        assertEquals(5, cats.size());
    }
}