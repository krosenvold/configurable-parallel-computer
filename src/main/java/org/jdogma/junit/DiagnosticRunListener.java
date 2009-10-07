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
/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class DiagnosticRunListener extends RunListener {
    private void print(String event, Description description) {
        System.out.println(Thread.currentThread().toString() +  ", event = " + event + ", " + description.toString());
    }
    private void print(String event, Result description) {
        System.out.println(Thread.currentThread().toString() +  ", event = " + event + ", " + description.toString());
    }
    private void print(String event, Failure description) {
        System.out.println(Thread.currentThread().toString() +  ", event = " + event + ", " + description.toString());
    }
    @Override
    public void testRunStarted(Description description) throws Exception {
        print("testRunStarted", description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        print("testRunStarted", result);
    }

    @Override
    public void testStarted(Description description) throws Exception {
        print("testStarted", description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        print("testFinished", description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        print("testFailure", failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        print("testAssumptionFailure", failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        print("testIgnored", description);
    }
}
