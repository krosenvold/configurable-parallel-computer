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
import org.junit.experimental.ParallelComputer;
import org.junit.runner.Computer;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static org.junit.Assert.assertEquals;

/**
 * Simple concurrency test of junit core.
 * @author <a href="mailto:kristianAzeniorD0Tno">Kristian Rosenvold</a>
 */
public class JUnitCoreConcurrencyTest {
    private static final int NUMTESTS = 1000;


    // I'm sorry about all the sout's in this test; but if you deadlock when building you will appreciate it.

    @Test
    public void testAnythingYouWantToPlayWith() throws Exception { 
        Result result = new Result();
        Class[] realClasses = new Class[]  {Dummy.class, Dummy2.class};
        
        DiagnosticRunListener diagnosticRunListener = new DiagnosticRunListener(false, result.createListener());
        JUnitCore jUnitCore = getJunitCore(result, diagnosticRunListener);
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(true, false);
        jUnitCore.run(computer, realClasses);
        computer.close();
        assertEquals("All tests should succeed, right ?",  5, result.getRunCount());
    }

    @Test
    public void testOneMethod() throws ExecutionException {
        JUnitCore jUnitCore = new JUnitCore();
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(false, true);
        jUnitCore.run( computer, new Class[] { Dummy.class, Dummy.class, Dummy.class});
        computer.close();
    }

    @Test
    public void testSerial() throws Exception {
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        Computer computer = new Computer();
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }


    @Test
    public void testFullTestRunPC() throws Exception {
        System.out.println("testFullTestRunPC");
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        Computer computer = new ConfigurableParallelComputer(true, true);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }

    @Test
    public void testWithFailingAssertionCPC() throws Exception {
        System.out.println("testWithFailingAssertionCPC");
        runWithFailingAssertion(new ConfigurableParallelComputer(false, true, 6, true));
        runWithFailingAssertion(new ConfigurableParallelComputer(true, false, 6, true));
    }

    @Test
    public void testWithFailingAssertionC() throws Exception {
        System.out.println("testWithFailingAssertionCPC");
        final ParallelComputer computer = new ParallelComputer(false, true);
        runWithFailingAssertion(computer);
        runWithFailingAssertion(new ParallelComputer(true, false));
    }


    private void runWithFailingAssertion(Computer computer) throws ExecutionException {
        Result result = new Result();
        Class[] realClasses = getClassList(FailingAssertions.class);
        JUnitCore jUnitCore = getJunitCore(result);
        runIt( realClasses, jUnitCore, computer);
        assertEquals("No tests should fail, right ?",  NUMTESTS, result.getFailures().size());
        assertEquals("All tests should succeed, right ?",  0, result.getIgnoreCount());
        assertEquals("All tests should succeed, right ?",  NUMTESTS * 3, result.getRunCount());
    }

    @Test
    public void testWithFailure() throws Exception {
        System.out.println("testWithFailure");
        Computer computer = new ConfigurableParallelComputer(false, true, 4, true);
        Result result = new Result();
        Class[] realClasses = getClassList(Failure.class);
        JUnitCore jUnitCore = getJunitCore(result);
        runIt( realClasses, jUnitCore, computer);
        assertEquals("No tests should fail, right ?",  NUMTESTS, result.getFailures().size());
        assertEquals("All tests should succeed, right ?",  0, result.getIgnoreCount());
        assertEquals("All tests should succeed, right ?",  NUMTESTS * 3, result.getRunCount());
    }

    @Test
    public void testFixedThreadPool() throws Exception {
        System.out.println("testFixedThreadPool");
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(false, true, 2, true);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }

    @Test
    public void testClassesUnlimited() throws Exception {
        System.out.println("testClassesUnlimited");
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(true, false);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }

    @Test
    public void testBothUnlimited() throws Exception {
        System.out.println("testBothUnlimited");
        Result result = new Result();
        Class[] realClasses = getClassList();
        DiagnosticRunListener diagnosticRunListener = new DiagnosticRunListener(false, result.createListener());
        JUnitCore jUnitCore = getJunitCore(result, diagnosticRunListener);
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(true, true);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
        System.out.println("diagnosticRunListener = " + diagnosticRunListener);
    }

    private JUnitCore getJunitCore(Result result) {
        RunListener listener = result.createListener();
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( listener);
        return jUnitCore;
    }
    private JUnitCore getJunitCore(Result result, RunListener listener) {
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( listener);
        return jUnitCore;
    }

    private void runIt(Class[] realClasses, JUnitCore jUnitCore, Computer computer) throws ExecutionException {
        long start = System.currentTimeMillis();
        jUnitCore.run(computer, realClasses);
        if (computer instanceof ConfigurableParallelComputer){
             ((ConfigurableParallelComputer)computer).close();
        }
        System.out.println(" XelapsedX " + (System.currentTimeMillis() - start) + "  for " + computer.toString());
    }

    private void timedRun(int NUMTESTS, Result result, Class[] realClasses, JUnitCore jUnitCore, Computer computer) throws ExecutionException {
        runIt( realClasses, jUnitCore, computer);    
        assertEquals("No tests should fail, right ?",  0, result.getFailures().size());
        assertEquals("All tests should succeed, right ?",  0, result.getIgnoreCount());
        assertEquals("All tests should succeed, right ?",  NUMTESTS * 3, result.getRunCount());
    }

    private Class[] getClassList() {
        return getClassList( Dummy.class);
    }
    private Class[] getClassList(Class testClass) {
        List<Class> realClasses = new ArrayList<Class>();
        for (int i = 0; i < NUMTESTS; i ++){
            realClasses.add( testClass);
        }
        return realClasses.toArray(new Class[realClasses.size()]);
    }


}