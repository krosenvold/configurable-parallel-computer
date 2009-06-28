package org.junit.experimental;

import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.Failure;
import org.junit.runner.Description;
import org.junit.runner.Result;

import java.util.*;

/**
 * Demultiplexes threaded running og tests into something that does not look threaded.
 */
public class DemultiplexingRunListener extends RunListener {
    private final Map<String, ClassReport> classList = new  HashMap<String, ClassReport>();
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
        for (ClassReport classReport : classList.values()){
            classReport.testRunFinished( outerResult);
        }
    }

    private MethodReport getMethodReport( Description description) {
        ClassReport classReport = getClassReport( description);
        return classReport.getMethodReport( description.getMethodName());
    }
    private ClassReport getClassReport(Description description) {
        ClassReport result;
        synchronized (classList){
            result = innerGetClassReport( description.getClassName());
        }
        return result;
    }
    private ClassReport getOrCreateClassReport(Description description) throws Exception {
        ClassReport result;
        synchronized (classList){
            result = innerGetClassReport( description.getClassName());
            if (result == null){
                result = classList.put(description.getClassName(), new ClassReport(realtarget));
            }
        }
        return result;
    }
    private ClassReport innerGetClassReport(String className){
        return classList.get(className);
    }

 /*   public List<TestResult> sort(){
        List<TestResult> results = new ArrayList<TestResult>( testResults);
        Collections.sort( results, new TestResultComparator());
        return results;
    }
   */

    @Override
    public void testStarted(Description description) throws Exception {
        ClassReport classReport = getOrCreateClassReport(description);
        classReport.getMethodReport( description.getMethodName()).testStarted( description);
    }

    @Override
    public void testFinished(Description description) throws Exception {
        getMethodReport(description).testFinished( description);
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        getMethodReport(failure.getDescription()).testFailure( failure);
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        getMethodReport(failure.getDescription()).testAssumptionFailure( failure);
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        getMethodReport(description).testFinished( description);
     }

 
/*    class ClassReport {
    class TestResultComparator<T> implements Comparator<TestResult>{
        public int compare(TestResult lhs, TestResult rhs) {
            int i = lhs.getClassName().compareTo(rhs.getClassName());
            if (i != 0) return i;
            int tn = lhs.getTestName().compareTo(rhs.getClassName());
            if (tn != 0) return tn;
            return lhs.getResult().compareTo( rhs.getResult());
        }
    }*/
}
