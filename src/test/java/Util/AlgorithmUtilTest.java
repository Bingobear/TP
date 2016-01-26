package Util;

import org.junit.Test;

import junit.framework.TestCase;

public class AlgorithmUtilTest extends TestCase {

	@Test
    public void testLevenshteinDistance() {
        int test = AlgorithmUtil.LevenshteinDistance("test", "hell");
        assertEquals(3, test);
    }
	@Test
    public void testCalculateWordSim() {
        double val = 0.2;
        double test = AlgorithmUtil.calculateWordSim("test", val);
        assertEquals(0.05, test);
    }

}
