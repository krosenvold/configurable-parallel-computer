package org.jdogma.junit;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

public class TestMethod
{
    private Description description;

    private volatile Failure testFailure;
    private volatile Failure testAssumptionFailure;

    private volatile Description finished;

    private volatile Description ignored;

    private final DemultiplexingRunListener.TestDescription parent;

    public TestMethod( Description description, DemultiplexingRunListener.TestDescription current )
    {
        this.description = description;
        this.parent = current;
    }


    public void testFinished( Description description )
        throws Exception
    {
        this.finished = description;
    }



    public void testIgnored( Description description )
        throws Exception
    {
        ignored = description;
    }

    public void testFailure( Failure failure )
        throws Exception
    {
        this.testFailure = failure;
    }

    public void testAssumptionFailure( Failure failure )
    {
        this.testAssumptionFailure = failure;
    }

    public void replay(RunListener runListener)
        throws Exception
    {
        if (ignored != null){
            runListener.testIgnored(  ignored );
        } else {
            runListener.testStarted( description );
            if (testFailure != null){
                runListener.testFailure(  testFailure );
            }
            if (testAssumptionFailure != null){
                runListener.testAssumptionFailure(  testAssumptionFailure );
            }
            runListener.testFinished(  finished );
        }
    }

    public DemultiplexingRunListener.TestDescription getParent()
    {
        return parent;
    }
}
