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
    private final Vector<TestResult> testResults = new Vector<TestResult>(); 

    @Override
    public void testRunStarted(Description description) throws Exception {
        super.testRunStarted(description);
    }

    @Override
    public void testRunFinished(Result result) throws Exception {
        super.testRunFinished(result);    
    }


    public List<TestResult> sort(){
        List<TestResult> results = new ArrayList<TestResult>( testResults); 
        Collections.sort( results, new TestResultComparator());
        return results;
    }


    @Override
    public void testStarted(Description description) throws Exception {
        testResults.add( new DescriptionResult(description, ResultType.started));
    }

    @Override
    public void testFinished(Description description) throws Exception {
        testResults.add( new DescriptionResult(description, ResultType.finished));
    }

    @Override
    public void testFailure(Failure failure) throws Exception {
        testResults.add( new FailureResult(failure, ResultType.failure));
    }

    @Override
    public void testAssumptionFailure(Failure failure) {
        testResults.add( new FailureResult( failure, ResultType.failure));
    }

    @Override
    public void testIgnored(Description description) throws Exception {
        testResults.add( new DescriptionResult( description, ResultType.ignored));
    }

    enum ResultType implements Comparable<ResultType> {
        started, finished, failure, assumption, ignored;

        public int compareTo(ResultType lhs, ResultType rhs) {
            if (lhs == rhs) return 0;
            if (lhs == started) return -1;
            return +1;
        }


    }

    abstract class TestResult {
        private final String className;
        private final ResultType result;

        TestResult(String className, ResultType result) {
            this.className = className;
            this.result = result;
        }

        public String getClassName() {
            return className;
        }
        public abstract String getTestName();

        public ResultType getResult() {
            return result;
        }
    }

    class DescriptionResult extends TestResult {
        private final Description description;

        DescriptionResult(Description description, ResultType result ) {
            super(description.getClassName(), result);
            this.description = description;
        }

        public Description getDescription() {
            return description;
        }

        public String getTestName() {
            return description.getMethodName();
        }

    }
    class FailureResult extends TestResult {
        private final Failure failure;

        FailureResult(Failure failure, ResultType resultType) {
            super(failure.getDescription().getClassName(), resultType);
            this.failure = failure;
        }

        public Failure getFailure() {
            return failure;
        }

        public String getTestName() {
            return failure.getDescription().getMethodName();
        }

    }

    class TestResultComparator<T> implements Comparator<TestResult>{
        public int compare(TestResult lhs, TestResult rhs) {
            int i = lhs.getClassName().compareTo(rhs.getClassName());
            if (i != 0) return i;
            int tn = lhs.getTestName().compareTo(rhs.getClassName());
            if (tn != 0) return tn;
            return lhs.getResult().compareTo( rhs.getResult());
        }
    }
}
