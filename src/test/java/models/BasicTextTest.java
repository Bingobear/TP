package models;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by simonbruns on 13/05/16.
 */
public class BasicTextTest {

    @Test
    public void testGetLanguage() throws Exception {
        BasicText basicText = new BasicText("HELLO MY NAME IS SiMON. I HAVE DEVELOPED THIS TEXTPARSING APPLICATION.");
        assertEquals("en",basicText.getLanguage());
    }

    @Test
    public void testGetText() throws Exception {
        BasicText basicText = new BasicText("TEST");
        assertEquals("TEST",basicText.getText());
    }
}