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
import org.junit.runner.Description;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;

import java.util.Map;

import static junit.framework.Assert.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class DemultiplexingRunListenerTest {

    class RootSuite {
        
    }

    @Test
    public void testTestStarted() throws Exception {
        RunListener real = mock(RunListener.class);

        //DiagnosticRunListener diagnosticRunListener = new DiagnosticRunListener();
        DemultiplexingRunListener listener = new DemultiplexingRunListener(real);


        Description rootSuite = Description.createSuiteDescription(RootSuite.class);
        Description testClass1 = Description.createSuiteDescription(Dummy.class);
        Description testClass2 = Description.createSuiteDescription(Dummy2.class);
        rootSuite.addChild( testClass1);
        rootSuite.addChild( testClass2);

        Description testMethod1_1 = getDescription1_1();
        Description testMethod1_2 = getDescription1_2();
        testClass1.addChild( testMethod1_1);
        testClass1.addChild( testMethod1_2);

        Description testMethod2 = getDescription2();
        testClass2.addChild(testMethod2);

        
        listener.testRunStarted(rootSuite);
        
        listener.testStarted(testMethod1_1);
        listener.testStarted(testMethod1_2);
        listener.testFinished( testMethod1_1);
        listener.testFinished( testMethod1_2);

        listener.testStarted(testMethod2);
        listener.testFinished( testMethod2);

        Result temp = new Result();
        listener.testRunFinished( temp);

        verify(real).testRunStarted( testClass1);
        verify(real).testStarted( testMethod1_1);
        verify(real).testStarted( testMethod1_2);
        verify(real).testFinished( testMethod1_1);
        verify(real).testFinished( testMethod1_2);

        verify(real).testRunStarted( testClass2);
        verify(real).testStarted( testMethod2);
        verify(real).testFinished( testMethod2);

        verify(real, times(2)).testRunFinished( any( Result.class));
    }

    @Test
    public void testCreateAnnotatedDescriptions(){
        Description testRunDescription = Description.createSuiteDescription(DemultiplexingRunListenerTest.class);
        Description description1_1 = getDescription1_1();
        Description description1_2 = getDescription1_2();
        Description description2 = getDescription2();
        testRunDescription.addChild( description1_1);
        testRunDescription.addChild( description1_2);
        testRunDescription.addChild( description2);

        final Map<String,DemultiplexingRunListener.AnnotatedDescription> map = DemultiplexingRunListener.createAnnotatedDescriptions(testRunDescription);
        assertNotNull( map);

        DiagnosticRunListener target = new DiagnosticRunListener();
        DemultiplexingRunListener.AnnotatedDescription annotatedDescription1_1 = map.get(description1_1.getDisplayName());
        assertFalse( annotatedDescription1_1.setDone(target));
        DemultiplexingRunListener.AnnotatedDescription annotatedDescription1_2 = map.get(description1_2.getDisplayName());
        assertFalse( annotatedDescription1_2.setDone(target));
        DemultiplexingRunListener.AnnotatedDescription annotatedDescription2 = map.get(description2.getDisplayName());
        assertTrue( annotatedDescription2.setDone(target));
    }

    @Test
    public void testJunitCoreAssumptions() throws Exception {
        Result result = new Result();
        DiagnosticRunListener diagnosticRunListener = new DiagnosticRunListener();

        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( diagnosticRunListener);
        Computer computer = new Computer();

        jUnitCore.run(computer, new Class[] { NothingGood.class});

        assertEquals(1, diagnosticRunListener.getNumTestIgnored().get());
        assertEquals(3, diagnosticRunListener.getNumTestFinished().get());
        assertEquals(2, diagnosticRunListener.getNumTestFailed().get());
        assertEquals(1, diagnosticRunListener.getNumTestAssumptionsFailed().get());
        assertEquals(3, diagnosticRunListener.getNumTestStarted().get());
    }


    private Description getDescription2() {
        return Description.createTestDescription( Dummy2.class, "testStub2");
    }

    private Description getDescription1_2() {
        return Description.createTestDescription( Dummy.class, "testDummy1_1");
    }

    private Description getDescription1_1() {
        return Description.createTestDescription( Dummy.class, "testDummy1_2");
    }
}
