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

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    private volatile Map<String, TestMethod> annotatedDescriptionMap;

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
        final TestMethod testDescription = getTestDescription(description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        final TestMethod testDescription = getTestDescription(description);
        testDescription.testFinished(description);
        testDescription.getParent().setDone(realtarget);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        final TestMethod testDescription = getTestDescription(description);
        testDescription.testIgnored(description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        getTestDescription(failure.getDescription()).testFailure(failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        final TestMethod testDescription = getTestDescription(failure.getDescription());
        testDescription.testAssumptionFailure(failure);
    }


    private TestMethod getTestDescription(Description description) {
        final TestMethod result = annotatedDescriptionMap.get(description.getDisplayName());
        if (result == null)
            throw new IllegalStateException("No TestDescription found for " + description + ", inconsistent junit behaviour. Unknown junit version?");
        return result;
    }


    static Map<String, TestMethod> createAnnotatedDescriptions(Description description) {
        Map<String, TestMethod> result = new HashMap<String, TestMethod>();
        createTestDescription(description, result );
        return result;
    }

    private static void createTestDescription( Description description, Map<String, TestMethod> result ) {
        final ArrayList<Description> children = description.getChildren();

        TestDescription current = new TestDescription( Description.createSuiteDescription( description.getDisplayName()));

        for (Description item : children) {
            if (item.isTest()) {
                TestMethod testMethod = new TestMethod( item, current );
                if (item.getDisplayName() != null){
                    result.put( item.getDisplayName(), testMethod);
                }
                current.addTestMethod( testMethod );
            } else {
                createTestDescription(item, result );
            }
        }
    }

    public static class TestDescription
    {
        private final Description description;
        private final RecordingRunListener recordingRunListener;
        private AtomicInteger numberOfCompletedChildren = new AtomicInteger(0);
        private List<TestMethod> testMethods = new ArrayList<TestMethod>( );

        public TestDescription( Description description ) {
            this.description = description;
            recordingRunListener = new RecordingRunListener(description);
        }

        private void addTestMethod(TestMethod testMethod){
            testMethods.add( testMethod);
        }

        private boolean incrementCompletedChildrenCount() {
            return testMethods.size() == numberOfCompletedChildren.incrementAndGet();
        }

        boolean setDone( RunListener target )
        {
            final boolean result = incrementCompletedChildrenCount();
            if ( result )
            {
                try
                {
                    recordingRunListener.replayStart( target );
                    for ( TestMethod testMethod : testMethods )
                    {
                        testMethod.replay( target );
                    }
                    recordingRunListener.replayEnd( target );
                }
                catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            }
            return result;
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

        public RecordingRunListener( Description testRunStarted )
        {
            this.testRunStarted = testRunStarted;
        }

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

        public void replayStart(RunListener target) throws Exception {
            target.testRunStarted(testRunStarted);

        }
        public void replayEnd(RunListener target) throws Exception {
            target.testRunFinished(resultForThisClass);
        }



    }

}
