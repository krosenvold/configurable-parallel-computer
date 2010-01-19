package org.jdogma.junit;

import org.junit.Test;

/**
 * @author Kristian Rosenvold
 */
public class Failure {
    @Test
    public void testNotMuch(){
    }

    @Test
    public void testNotMuch2(){
    }
    

    @Test
    public void testWithException() {
        throw new RuntimeException("We expect this");
    }


}