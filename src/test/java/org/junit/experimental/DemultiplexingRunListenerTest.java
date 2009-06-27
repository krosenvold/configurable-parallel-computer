package org.junit.experimental;

import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Computer;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: kristian
 * Date: Jun 27, 2009
 * Time: 11:57:11 PM
 * To change this template use File | Settings | File Templates.
 */
public class DemultiplexingRunListenerTest {
    @Test
    public void testTestStarted() throws Exception {

        DemultiplexingRunListener listener = new DemultiplexingRunListener();
        Description ruNDescr = Description.createSuiteDescription(DemultiplexingRunListenerTest.class);
        Description description1 = Description.createTestDescription( DemultiplexingRunListenerTest.class, "testStub1");
        Description description2 = Description.createTestDescription( Dummy.class, "testStub2");

        listener.testRunStarted(ruNDescr);
        listener.testStarted(description1);
        listener.testStarted(description2);
        listener.testFinished(description1);
        listener.testFinished(description2);
        Result temp = new Result();
        listener.testRunFinished( temp);

        List<DemultiplexingRunListener.TestResult> resultList = listener.sort();

        System.out.println("resultList = " + resultList);

        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( listener);

        jUnitCore.run(new Class[] {DemultiplexingRunListenerTest.class});

        // Add your code here
    }
    @Test
    public void testRegularJunitCoreRun() throws Exception {

        DemultiplexingRunListener listener = new DemultiplexingRunListener();
        JUnitCore jUnitCore = new JUnitCore();
        jUnitCore.addListener( listener);

        jUnitCore.run(new Class[] {Dummy.class});

        // Add your code here
    }

    @Test
    public void testStub1() {
        // Add your code here
    }
    @Test
    public void testStub2() {
        // Add your code here
    }
}
