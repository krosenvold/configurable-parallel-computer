package org.jdogma.junit;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * @author Kristian Rosenvold
 */
public class SlowTest {
    @Test
    public void testNotMuch() throws InterruptedException {
        Thread.sleep(100);
    }

    @Test
    public void testNotMuch2() throws InterruptedException {
        Thread.sleep(300);
    }

    @Test
    public void testNotMuch3() throws InterruptedException {
        Thread.sleep(200);
    }


}