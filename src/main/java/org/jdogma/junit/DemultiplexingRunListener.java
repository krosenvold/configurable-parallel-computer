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

import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Demultiplexes threaded running og tests into something that does not look threaded.
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */
public class DemultiplexingRunListener extends RunListener {
    private final Map<String, RecordingRunListener> classList = new HashMap<String, RecordingRunListener>();
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
        for (RecordingRunListener classReport : classList.values()) {
            classReport.replay( realtarget, true);
        }
    }

    @Override
    public void testStarted(Description description) throws Exception {
        getOrCreateClassReport(description).testStarted( description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        getClassReport( description).testFinished(description);
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


    RecordingRunListener getClassReport(Description description) {
        synchronized ( classList){
          return classList.get( description.getClassName());
        }
    }

    private RecordingRunListener getOrCreateClassReport(Description description) throws Exception {
        RecordingRunListener result;
        synchronized (classList) {
            result = classList.get(description.getClassName());
            if (result == null) {
                result = new RecordingRunListener();
                result.testRunStarted( description);
                classList.put(description.getClassName(), result);
            }
        }
        return result;
    }

    /*
        * @author Kristian Rosenvold, kristianAzeniorD0Tno
        */
    public static class ClassReport extends RunListener {
        private final RunListener realtarget;
        private final Result resultForThisClass = new Result();
        private final RunListener classRunListener = resultForThisClass.createListener();

        public ClassReport(RunListener realtarget) {
            this.realtarget = realtarget;
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
            realtarget.testRunFinished( result);
            classRunListener.testRunFinished( result);
        }

        @Override
        public void testStarted(Description description) throws Exception {
            realtarget.testStarted(description);
            classRunListener.testStarted( description);
        }

        @Override
        public void testFinished(Description description) throws Exception {
            realtarget.testFinished(description);
            classRunListener.testFinished( description);
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            realtarget.testFailure(failure);
            classRunListener.testFailure( failure);
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            realtarget.testAssumptionFailure(failure);
            classRunListener.testAssumptionFailure( failure);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            realtarget.testIgnored(description);
            classRunListener.testIgnored( description);
        }

        public void testRunFinished() throws Exception {
            realtarget.testRunFinished(resultForThisClass);
        }

        Result getResultForThisClass() {
            return resultForThisClass;
        }
    }
}
