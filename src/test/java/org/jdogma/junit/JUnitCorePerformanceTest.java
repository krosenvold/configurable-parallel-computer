/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Also licensed under CPL http://junit.sourceforge.net/cpl-v10.html
 */


package org.jdogma.junit;

import org.junit.Test;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertTrue;

/**
 * These tests assert that the expected peformance of classes/methods/both.
 *
 * While the occasional glitch in computing power can make these tests fail (if actual time exceeds expected + 25%),
 * the lower bound should *never* be violated.
 *
 * These tests usually fail if run in a debugger, due to the changed timings.
 * 
 * @author <a href="mailto:kristianAzeniorD0Tno">Kristian Rosenvold</a>
 */
public class JUnitCorePerformanceTest {
    private static final int NUMTESTS = 1000;
    private final int METHOD_MS = 300;
    private final int CLASS_MS = 600;


    @Test
    public void testSpeedWithClasses() throws Exception {
        int NUMCLASSSES = 5;
        Result result = new Result();
        JUnitCore jUnitCore = getJunitCore(result);

        // Warmup to avoid classloading stuff
        runIt( getClassList(SlowTest.class, 2), jUnitCore, new ConfigurableParallelComputer(true, false, 3, false));


        // Running fClasses = true, it should be 600ms per class.
        int numberOfThreads = 3;
        long resp2 = runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, false, numberOfThreads, false));
        validateBounds( resp2,  getPigeonHoles(NUMCLASSSES, numberOfThreads) * CLASS_MS, numberOfThreads + " threads running " + NUMCLASSSES + "classes at " + CLASS_MS + "ms per class ");

        NUMCLASSSES++;
        {
        long resp3 = runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, false, numberOfThreads, false));
        validateBounds( resp3,  getPigeonHoles(NUMCLASSSES, numberOfThreads) * CLASS_MS, numberOfThreads + " threads running " + NUMCLASSSES + "classes at " + CLASS_MS + "ms per class ");
        }

        NUMCLASSSES++;
        {
        long resp3 = runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, false, numberOfThreads, false));
        validateBounds( resp3, getPigeonHoles(NUMCLASSSES, numberOfThreads) * CLASS_MS, numberOfThreads + " threads running " + NUMCLASSSES + "classes at " + CLASS_MS + "ms per class ");
        }
    }

    private int getPigeonHoles(int NUMCLASSSES, int numberOfThreads) {
        return (int) Math.ceil((float)NUMCLASSSES / numberOfThreads);
    }

    @Test
    public void testSpeedWithMethods() throws Exception {
        int NUMCLASSSES = 5;
        int numberOfThreads = 3;
        System.out.println("testSpeedWithSlowTest");
        Result result = new Result();
        JUnitCore jUnitCore = getJunitCore(result);

        // Warmup to avoid classloading stuff
        runIt( getClassList(SlowTest.class, 2), jUnitCore, new ConfigurableParallelComputer(false, true, 3, false));

        validateBounds(runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(false, true, numberOfThreads, false)),
                NUMCLASSSES * METHOD_MS, numberOfThreads + " threads running " + NUMCLASSSES + " classes in method mode at " + METHOD_MS + "ms per class ");

        NUMCLASSSES++;
         validateBounds( runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(false, true, numberOfThreads, false)),
                 NUMCLASSSES * METHOD_MS, numberOfThreads + " threads running " + NUMCLASSSES + " classes in method mode at " + METHOD_MS + "ms per class ");

        NUMCLASSSES++;
        validateBounds(runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(false, true, numberOfThreads, false)),
                    NUMCLASSSES * METHOD_MS, numberOfThreads + " threads running " + NUMCLASSSES + " classes in method mode at " + METHOD_MS + "ms per class ");
    }

    @Test
    public void testSpeedWithBoth() throws Exception {
        int NUMCLASSSES = 5;
        int numberOfThreads = 3;
        Result result = new Result();
        JUnitCore jUnitCore = getJunitCore(result);

        // Warmup to avoid classloading stuff
        runIt( getClassList(SlowTest.class, 2), jUnitCore, new ConfigurableParallelComputer(true, true, 3, false));

        validateBounds(runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, true, numberOfThreads, false)),
                (NUMCLASSSES * CLASS_MS) / numberOfThreads, numberOfThreads + " threads running " + NUMCLASSSES + " classes in both mode at " + CLASS_MS + "ms per class ");

        NUMCLASSSES++;
         validateBounds( runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, true, numberOfThreads, false)),
                 (NUMCLASSSES * CLASS_MS) / numberOfThreads, numberOfThreads + " threads running " + NUMCLASSSES + " classes in both mode at " + CLASS_MS + "ms per class ");

        NUMCLASSSES++;
        int lowest = (NUMCLASSSES * CLASS_MS) / numberOfThreads;
        validateBounds(runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, true, numberOfThreads, false)),
                lowest, numberOfThreads + " threads running " + NUMCLASSSES + " classes in both mode at " + CLASS_MS + "ms per class ");

        numberOfThreads++;
        lowest = (NUMCLASSSES * CLASS_MS) / numberOfThreads;
        validateBounds(runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, true, numberOfThreads, false)),
                    lowest, numberOfThreads + " threads running " + NUMCLASSSES + " classes in both mode at " + CLASS_MS + "ms per class ");
        numberOfThreads++;
        lowest = (NUMCLASSSES * CLASS_MS) / numberOfThreads;
        validateBounds(runIt( getClassList(SlowTest.class, NUMCLASSSES), jUnitCore, new ConfigurableParallelComputer(true, true, numberOfThreads, false)),
                    lowest, numberOfThreads + " threads running " + NUMCLASSSES + " classes in both mode at " + CLASS_MS + "ms per class ");

    }


    private void validateBounds(long actual, long absoluteLowest, String message){
        System.out.println("absoluteLowest = " + absoluteLowest);
        assertTrue("Actual runtime " + actual + " for " +  message + " violates minimal expected runtime of " + absoluteLowest +", this is not according to computer physics", actual >= absoluteLowest);
        final long max = (int) (absoluteLowest * 1.25);
        assertTrue("Actual runtime " + actual + " for " + message + " violates maximal expected runtime" + max, actual < max);
    }


    private JUnitCore getJunitCore(Result result) {
        RunListener listener = result.createListener();
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( listener);
        return jUnitCore;
    }

    private long runIt(Class[] realClasses, JUnitCore jUnitCore, Computer computer) throws ExecutionException {
        long start = System.currentTimeMillis();
        jUnitCore.run(computer, realClasses);
        if (computer instanceof ConfigurableParallelComputer){
             ((ConfigurableParallelComputer)computer).close();
        }
        return System.currentTimeMillis() - start;
    }

    private Class[] getClassList(Class testClass, int numItems) {
        List<Class> realClasses = new ArrayList<Class>();
        for (int i = 0; i < numItems; i ++){
            realClasses.add( testClass);
        }
        return realClasses.toArray(new Class[realClasses.size()]);
    }

}