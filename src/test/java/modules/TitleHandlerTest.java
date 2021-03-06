package modules;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Created by simonbruns on 16/03/16.
 */
public class TitleHandlerTest {
    @Test
    public void testgetTitle() throws IOException {
        TitleMatcher titleHandler = new TitleMatcher();
        titleHandler.initializeKnownTitles("text");
        String title = titleHandler.getTitle("schaar_06038875");
        assertEquals("Smart Clothing. Perceived Benefits vs. Perceived Fears", title);
    }
}