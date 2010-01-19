package org.jdogma.junit;

import org.junit.Ignore;
import org.junit.Test;

import static junit.framework.Assert.fail;

/**
 * @author Kristian Rosenvold
 */
public class FailingAssertions {
    @Test
    public void testNotMuch(){
    }

    @Test
    public void testNotMuch2(){
    }
    
    @Test
    public void testWithFail() {
        fail("We excpect this");
    }


}
