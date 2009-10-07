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

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.junit.Test;
import org.junit.experimental.ParallelComputer;
import static org.junit.Assert.*;
import org.junit.runner.notification.RunListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Computer;
import org.junit.runner.Result;
import org.jdogma.junit.ConfigurableParallelComputer;

/**
 * Simple concurrency test of junit core.
 * @author <a href="mailto:kristianAzeniorD0Tno">Kristian Rosenvold</a>
 */
public class JUnitCoreConcurrencyTest {
    private static final int NUMTESTS = 1000;

    @Test
    public void testOneMethod(){
        JUnitCore jUnitCore = new JUnitCore();
        Computer computer = new ConfigurableParallelComputer(true, true);
        jUnitCore.run( computer, new Class[] { Dummy.class, Dummy.class, Dummy.class});
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
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        ParallelComputer computer = new ParallelComputer(true, true);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }

    @Test
    public void testFixedThreadPool() throws Exception {
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(false, true, 2, true);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }

    @Test
    public void testClassesUnlimited() throws Exception {
        Result result = new Result();
        Class[] realClasses = getClassList();
        JUnitCore jUnitCore = getJunitCore(result);
        ConfigurableParallelComputer computer = new ConfigurableParallelComputer(true, false);
        timedRun(NUMTESTS, result, realClasses, jUnitCore, computer);
    }

    @Test
    public void testBothUnlimited() throws Exception {
        Result result = new Result();
        Class[] realClasses = getClassList();
        DiagnosticRunListener diagnosticRunListener = new DiagnosticRunListener(false, result.createListener());
        JUnitCore jUnitCore = getJunitCore(result, diagnosticRunListener);
        // TODO: There seems to be a concurrency issue here that pops up sometimes on c2d, but never on i7
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

    private void timedRun(int NUMTESTS, Result result, Class[] realClasses, JUnitCore jUnitCore, Computer computer) throws ExecutionException {
        long start = System.currentTimeMillis();
        jUnitCore.run(computer, realClasses);
        if (computer instanceof ConfigurableParallelComputer){
             ((ConfigurableParallelComputer)computer).close();
        }
        System.out.println(" XelapsedX " + (System.currentTimeMillis() - start) + "  for " + computer.toString());
        assertEquals("No tests should fail, right ?",  0, result.getFailures().size());
        assertEquals("All tests should succeed, right ?",  0, result.getIgnoreCount());
        assertEquals("All tests should succeed, right ?",  NUMTESTS * 3, result.getRunCount());
    }

    private Class[] getClassList() {
        List<Class> realClasses = new ArrayList<Class>();
        for (int i = 0; i < NUMTESTS; i ++){
            realClasses.add( Dummy.class);
        }
        return realClasses.toArray(new Class[realClasses.size()]);
    }


}