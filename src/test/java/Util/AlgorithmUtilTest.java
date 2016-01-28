package Util;


import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class AlgorithmUtilTest {

	private String testStr = "test";
	
	@Before
	public void setup(){
		
		this.testStr = "test";
	}
	
	@Test
    public void testLevenshteinDistance() {
		assertEquals(0, AlgorithmUtil.LevenshteinDistance("", ""));	
		assertEquals(0, AlgorithmUtil.LevenshteinDistance(testStr, "test")); 
		assertEquals(1, AlgorithmUtil.LevenshteinDistance(testStr, "tes1"));
		assertEquals(1, AlgorithmUtil.LevenshteinDistance(testStr, "tes"));
		assertEquals(1, AlgorithmUtil.LevenshteinDistance(testStr, "testt"));
        assertEquals(3, AlgorithmUtil.LevenshteinDistance(testStr, "bell"));
    }
	
	@Test
    public void testCalculateWordSim() {
        double val = 0.2;
        double test = AlgorithmUtil.calculateWordSim("test", val);
        assertEquals(0.05, test, 0.001);
    }

}
