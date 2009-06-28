package org.junit.experimental;

import org.junit.runner.notification.RunListener;
import org.junit.runner.Result;
import org.junit.runner.Description;

import java.util.Map;
import java.util.HashMap;

public class ClassReport {
    private final Map<String, MethodReport> methodReports = new HashMap<String, MethodReport>();
    private final RunListener realtarget;
    private final Description description;
    private final Result classResult = new Result();

    public ClassReport(RunListener realtarget, Description description) {
        this.realtarget = realtarget;
        this.description = description;
    }

    public MethodReport getMethodReport(String methodName){
        MethodReport result;
        synchronized ( methodReports){
            result = methodReports.get( methodName);
            if (result == null)
               result = methodReports.put(methodName,  new MethodReport());
        }
        return result;
    }

    public void testRunFinished(RunListener listener) throws Exception {
        listener.testRunStarted( description);
        for (MethodReport methodReport : methodReports.values()){
            methodReport.replay( listener);
        }
        realtarget.testRunFinished(classResult);
    }

}
