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

import java.util.*;

/**
 * Demultiplexes threaded running og tests into something that does not look threaded.
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */
public class DemultiplexingRunListener extends RunListener {
    private final Map<String, ClassReport> classList = new HashMap<String, ClassReport>();
    private final RunListener realtarget;

    public DemultiplexingRunListener(RunListener realtarget) {
        this.realtarget = realtarget;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        // Do nothing. We discard this event because it's basically meaningless
    }

    @Override
    public void testRunFinished(Result outerResult) throws Exception {
        for (ClassReport classReport : classList.values()) {
            classReport.testRunFinished();
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        RunListener classReport = getOrCreateClassReport(description);
        classReport.testStarted( description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        RunListener classReport = getClassReport( description);
        classReport.testFinished( description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        getClassReport( failure.getDescription()).testFailure( failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        getClassReport(failure.getDescription()).testAssumptionFailure( failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        getClassReport(description).testIgnored(description);
    }


    ClassReport getClassReport(Description description) {
        ClassReport result;
        synchronized (classList) {
            result = innerGetClassReport(description.getClassName());
        }
        return result;
    }

    private ClassReport getOrCreateClassReport(Description description) throws Exception {
        ClassReport result;
        synchronized (classList) {
            result = innerGetClassReport(description.getClassName());
            if (result == null) {
                realtarget.testRunStarted( description);
                result = new ClassReport(realtarget);
                classList.put(description.getClassName(), result);
            }
        }
        return result;
    }

    private ClassReport innerGetClassReport(String className) {
        return classList.get(className);
    }
}
