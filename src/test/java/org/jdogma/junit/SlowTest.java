package org.jdogma.junit;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * @author Kristian Rosenvold
 */
public class SlowTest {
    final int scaling = 100;
    @Test
    public void testNotMuch() throws InterruptedException {
        Thread.sleep(scaling);
    }

    @Test
    public void testNotMuch2() throws InterruptedException {
        Thread.sleep(3 * scaling);
    }

    @Test
    public void testNotMuch3() throws InterruptedException {
        Thread.sleep(2 * scaling);
    }


}