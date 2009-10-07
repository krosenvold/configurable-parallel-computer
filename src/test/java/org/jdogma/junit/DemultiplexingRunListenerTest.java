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
import org.junit.internal.TextListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.Computer;
import org.junit.runner.notification.RunListener;
import static org.mockito.Mockito.*;
import org.jdogma.junit.DemultiplexingRunListener;

/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class DemultiplexingRunListenerTest {
    @Test
    public void testTestStarted() throws Exception {
        RunListener real = mock(RunListener.class);
        DemultiplexingRunListener listener = new DemultiplexingRunListener(real);

        Description testRunDescription = Description.createSuiteDescription(DemultiplexingRunListenerTest.class);
        Description description1 = Description.createTestDescription( DemultiplexingRunListenerTest.class, "testStub1");
        Description description2 = Description.createTestDescription( Dummy.class, "testStub2");

        listener.testRunStarted(testRunDescription);
        listener.testStarted(description1);
        listener.testStarted(description2);
        listener.testFinished(description1);
        listener.testFinished(description2);
        Result temp = new Result();
        listener.testRunFinished( temp);

        verify(real).testRunStarted( description1);
        verify(real).testStarted( description1);
        verify(real).testRunStarted( description2);
        verify(real).testStarted( description2);
    }
}
