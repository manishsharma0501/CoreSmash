package test.com.coresmash.performance;

import com.breakthecore.screens.LoadingScreen;

import junit.framework.TestCase;

@Deprecated //BROKEN
public class SmallPerformaceTests extends TestCase {
    long start;
    long result;

    public SmallPerformaceTests(String name) {
        super(name);
    }

    public void setUp() {
        start = System.nanoTime();
    }

    public void tearDown() {
        result = System.nanoTime() - start;
    }

    public void testCastingPerformance() {
    }
}
