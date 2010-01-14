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
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demultiplexes threaded running og tests into something that does not look threaded.
 * Essentially makes a threaded junit core RunListener behave like something like a
 * junit4 reporter can handle.
 * <p/>
 * This version is non-ketchup mode, outputting test results as the individual suites complete.
 *
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */
public class DemultiplexingRunListener extends RunListener {
    private final RunListener realtarget;

    private volatile Map<String, AnnotatedDescription> annotatedDescriptionMap;

    public DemultiplexingRunListener(RunListener realtarget) {
        this.realtarget = realtarget;
    }

    @Override
    public void testRunStarted(Description description) throws Exception {
        annotatedDescriptionMap = createAnnotatedDescriptions(description);
    }

    @Override
    public void testRunFinished(Result outerResult) throws Exception {
        // SHould be finished through all the tests.
    }

    @Override
    public void testStarted(Description description) throws Exception {
        final AnnotatedDescription annotatedDescription = getAnnotatedDescription(description);
        annotatedDescription.startIfUnstarted();
        annotatedDescription.getRecordingRunListener().testStarted(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        final AnnotatedDescription annotatedDescription = getAnnotatedDescription(description);
        annotatedDescription.getRecordingRunListener().testFinished(description);
        annotatedDescription.setDone(realtarget);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        final AnnotatedDescription annotatedDescription = getAnnotatedDescription(description);
        annotatedDescription.getRecordingRunListener().testIgnored(description);
        annotatedDescription.setDone(realtarget);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        final AnnotatedDescription annotatedDescription = getAnnotatedDescription(failure.getDescription());
        annotatedDescription.getRecordingRunListener().testFailure(failure);
        annotatedDescription.setDone(realtarget);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        final AnnotatedDescription annotatedDescription = getAnnotatedDescription(failure.getDescription());
        annotatedDescription.getRecordingRunListener().testAssumptionFailure(failure);
        annotatedDescription.setDone(realtarget);
    }


    private AnnotatedDescription getAnnotatedDescription(Description description) {
        final AnnotatedDescription result = annotatedDescriptionMap.get(description.getDisplayName());
        if (result == null)
            throw new IllegalStateException("No AnnotatedDescription found for " + description + ", inconsistent junit behaviour. Unknown junit version?");
        return result;
    }


    static Map<String, AnnotatedDescription> createAnnotatedDescriptions(Description description) {
        Map<String, AnnotatedDescription> result = new HashMap<String, AnnotatedDescription>();
        createAnnotatedDescriptions(description, result, null);
        return result;
    }

    private static void createAnnotatedDescriptions(Description description, Map<String, AnnotatedDescription> result, AnnotatedDescription parent) {
        final ArrayList<Description> children = description.getChildren();
        AnnotatedDescription current = new AnnotatedDescription(parent, description);
        if (description.getDisplayName() != null)
            result.put(description.getDisplayName(), current);

        for (Description item : children) {
            createAnnotatedDescriptions(item, result, current);
        }
    }

    static class AnnotatedDescription {
        private final AnnotatedDescription parent;
        private final Description description;
        private final RecordingRunListener recordingRunListener;
        private AtomicBoolean started = new AtomicBoolean(false);
        private AtomicInteger numberOfCompletedChildren = new AtomicInteger(0);


        public AnnotatedDescription(AnnotatedDescription parent, Description description) {
            this.parent = parent;
            this.description = description;
            recordingRunListener = new RecordingRunListener();
        }

        public void startIfUnstarted() throws Exception {
            if (started.compareAndSet(false, true)) {
                getRecordingRunListener().testRunStarted(parent.getDescription());
            }
        }

        private boolean incrementCompletedChildrenCount() {
            return description.getChildren().size() == numberOfCompletedChildren.incrementAndGet();
        }

        boolean setDone(RunListener target) {
            if (description.isTest()) {
                return parent.setDone(target);
            } else {
                final boolean result = incrementCompletedChildrenCount();
                if (result) {
                   try {
                       recordingRunListener.replay(target);
                   } catch (Exception e) {
                       throw new RuntimeException(e);
                   }
               }
                return result;
            }
        }

        public RecordingRunListener getRecordingRunListener() {
            return description.isTest() ? parent.getRecordingRunListener() : recordingRunListener;
        }

        private Description getDescription() {
            return description;
        }
    }


    public static class RecordingRunListener extends RunListener {
        private volatile Description testRunStarted;
        private final List<Description> testStarted = Collections.synchronizedList(new ArrayList<Description>());
        private final List<Description> testFinished = Collections.synchronizedList(new ArrayList<Description>());
        private final List<Failure> testFailure = Collections.synchronizedList(new ArrayList<Failure>());
        private final List<Failure> testAssumptionFailure = Collections.synchronizedList(new ArrayList<Failure>());
        private final List<Description> testIgnored = Collections.synchronizedList(new ArrayList<Description>());
        private final Result resultForThisClass = new Result();
        private final RunListener classRunListener = resultForThisClass.createListener();


        @Override
        public void testRunStarted(Description description) throws Exception {
            this.testRunStarted = description;
        }

        @Override
        public void testRunFinished(Result result) throws Exception {
            throw new IllegalStateException("This method should not be called on the recorder");
        }

        @Override
        public void testStarted(Description description) throws Exception {
            testStarted.add(description);
            classRunListener.testStarted(description);
        }

        @Override
        public void testFinished(Description description) throws Exception {
            testFinished.add(description);
            classRunListener.testFinished(description);
        }

        @Override
        public void testFailure(Failure failure) throws Exception {
            testFailure.add(failure);
            classRunListener.testFailure(failure);
        }

        @Override
        public void testAssumptionFailure(Failure failure) {
            testAssumptionFailure.add(failure);
        }

        @Override
        public void testIgnored(Description description) throws Exception {
            testIgnored.add(description);
        }

        public void replay(RunListener target) throws Exception {
            target.testRunStarted(testRunStarted);

            for (Description description : testStarted) {
                target.testStarted(description);
            }
            for (Failure failure : testFailure) {
                target.testFailure(failure);
            }
            for (Description description : testIgnored) {
                target.testIgnored(description);
            }
            for (Failure failure : testAssumptionFailure) {
                target.testAssumptionFailure(failure);
            }
            for (Description description : testFinished) {
                target.testFinished(description);
            }
            target.testRunFinished(resultForThisClass);
        }


    }

}
