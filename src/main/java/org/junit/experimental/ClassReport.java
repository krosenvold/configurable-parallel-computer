package org.junit.experimental;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Result;
import org.junit.runner.Description;

public class ClassReport extends RunListener {
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
