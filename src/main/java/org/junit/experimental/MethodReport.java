package org.junit.experimental;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

/**
 * Created by IntelliJ IDEA.
 * User: kristian
 * Date: Jun 28, 2009
 * Time: 8:41:19 AM
 * To change this template use File | Settings | File Templates.
 */
public class MethodReport {
    private volatile Description testStartedDescription;
    private volatile Description testFinishedDescription;
    private volatile Failure testFailure;
    private volatile Failure testAssumptionFailure;
    private volatile Description testIgnoredDescription;

    public void testStarted(org.junit.runner.Description description) {
       testStartedDescription = description;
    }

    public void testFinished(org.junit.runner.Description description) {
        testFinishedDescription = description;
    }

    public void testFailure(org.junit.runner.notification.Failure failure)
    {
        testFailure = failure;
    }

    public void testAssumptionFailure(org.junit.runner.notification.Failure failure)
    {
        testAssumptionFailure = failure;
    }

    public void testIgnored(org.junit.runner.Description description)
    {
        testIgnoredDescription = description;
    }

    public void replay(RunListener runListener) throws Exception {
        if (testStartedDescription != null) runListener.testStarted( testStartedDescription);
        if (testFinishedDescription != null) runListener.testFinished( testFinishedDescription);
        if (testFailure != null) runListener.testFailure( testFailure);
        if (testAssumptionFailure != null) runListener.testAssumptionFailure(testAssumptionFailure);
        if (testIgnoredDescription != null) runListener.testIgnored( testIgnoredDescription);
    }

}
