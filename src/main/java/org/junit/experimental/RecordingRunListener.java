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




package org.junit.experimental;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import org.junit.runner.Result;
/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class RecordingRunListener extends RunListener {
    private volatile Description testRunStarted;
    private volatile Result testRunFinished;
    private volatile Description testStarted;
    private volatile Description testFinished;
    private volatile Failure testFailure;
    private volatile Failure testAssumptionFailure;
    private volatile Description testIgnored;


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
        testStarted = description;
    }

    @Override
    public void testFinished(Description description) throws Exception {
        testFinished = description;
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        testFailure = failure;
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        testAssumptionFailure = failure;
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        testIgnored = description;
    }

    public void replay(RunListener target, boolean includeRunEvents) throws Exception {
        if (testRunStarted != null && includeRunEvents) target.testRunStarted (testRunStarted);
        if (testStarted != null) target.testStarted( testStarted);
        if (testFailure != null) target.testFailure( testFailure);
        if (testIgnored != null) target.testIgnored( testIgnored);
        if (testAssumptionFailure != null) target.testAssumptionFailure( testAssumptionFailure);
        if (testFinished != null) target.testFinished( testFinished);
        if (testRunFinished != null && includeRunEvents) target.testRunFinished( testRunFinished);
    }

}
