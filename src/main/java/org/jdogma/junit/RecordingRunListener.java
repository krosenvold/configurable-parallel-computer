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

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class RecordingRunListener extends RunListener {
    private volatile Description testRunStarted;
    private volatile Result testRunFinished;
    private final List<Description> testStarted = Collections.synchronizedList(new ArrayList<Description>());
    private final List<Description> testFinished =  Collections.synchronizedList(new ArrayList<Description>());
    private final List<Failure> testFailure =  Collections.synchronizedList(new ArrayList<Failure>());
    private final List<Failure> testAssumptionFailure =  Collections.synchronizedList(new ArrayList<Failure>());
    private final List<Description> testIgnored =  Collections.synchronizedList(new ArrayList<Description>());
    private final Result resultForThisClass = new Result();
    private final RunListener classRunListener = resultForThisClass.createListener();



    @Override
    public void testRunStarted(Description description) throws Exception {
        testRunStarted = description;
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        testRunFinished = result;
    }

    @Override
    public void testStarted(Description description) throws Exception {
        testStarted.add( description);
        classRunListener.testStarted( description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        testFinished.add( description);
        classRunListener.testFinished(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        testFailure.add( failure);
        classRunListener.testFailure( failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        testAssumptionFailure.add( failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        testIgnored.add(  description);
    }

    public void replay(RunListener target, boolean includeRunEvents) throws Exception {
        if (testRunStarted != null && includeRunEvents) target.testRunStarted (testRunStarted);

        for( Description description : testStarted) {
            target.testStarted( description);
        }
        for( Failure failure : testFailure) {
            target.testFailure( failure);
        }
        for( Description description : testIgnored) {
            target.testIgnored( description);
        }
        for( Failure failure : testAssumptionFailure) {
            target.testAssumptionFailure( failure);
        }
        for( Description description : testFinished) {
            target.testFinished( description);
        }
        target.testRunFinished( resultForThisClass);
    }


}
