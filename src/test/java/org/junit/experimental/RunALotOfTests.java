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
 */

package org.junit.experimental;

import java.util.List;
import java.util.ArrayList;

import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.runner.notification.RunListener;
import org.junit.runner.JUnitCore;
import org.junit.runner.Computer;
import org.junit.runner.Result;

/**
 * @author <a href="mailto:kristian@zeniorD0Tno">Kristian Rosenvold</a>
 */
public class RunALotOfTests {

	@Test
	public void testFullTestRun() throws Exception {
        final int NUMTESTS = 66;
        List<Class> realClasses = new ArrayList<Class>();


        Result result = new Result();
        RunListener listener = result.createListener();

        for (int i = 0; i < NUMTESTS; i ++){
            realClasses.add( Dummy.class);
        }
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( listener);

        Computer computer = new ConfigurableParallelComputer();
        long start = System.currentTimeMillis();
        jUnitCore.run(computer, realClasses.toArray(new Class[realClasses.size()]) );
        System.out.println("elapsed " + (System.currentTimeMillis() - start));

        assertEquals("No tests should fail, right ?",  0, result.getFailures().size());
        assertEquals("All tests should succeed, right ?",  NUMTESTS * 3, result.getRunCount());
    }

     @Test
    public void testOneMethod(){
         JUnitCore jUnitCore = new JUnitCore();
         Computer computer = new ConfigurableParallelComputer(true, true);
         jUnitCore.run( computer, new Class[] { Dummy.class, Dummy.class, Dummy.class});

     }


}