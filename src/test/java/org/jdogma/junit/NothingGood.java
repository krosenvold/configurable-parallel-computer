package org.jdogma.junit;

import org.junit.Ignore;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assume.*;
import static junit.framework.Assert.fail;

/**
 * @author Kristian Rosenvold
 */
public class NothingGood {
    @Ignore
    @Test
    public void testWithIgnore(){
    }

    @Test
    public void testiWithFail(){
        fail("We excpect this");

    }

    @Test
    public void testWithException() {
        throw new RuntimeException("We expect this");
    }

    @Test
    public void testWithFailingAssumption() {
        assumeThat( 2, is(3));
    }

}