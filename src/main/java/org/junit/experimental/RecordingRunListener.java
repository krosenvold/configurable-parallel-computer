package org.junit.experimental;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import org.junit.runner.Result;

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
