package org.junit.experimental;

import org.junit.runners.model.RunnerInterceptor;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
* User: kristian
* Date: Jun 30, 2009
* Time: 5:52:10 PM
* To change this template use File | Settings | File Templates.
*/
public class DelayedRunner extends ConcurrentRunnerInterceptorBase implements RunnerInterceptor {
    private final List<Callable<Object>> fResults = Collections.synchronizedList(new ArrayList<Callable<Object>>());
    private final ExecutorService fService;

    public DelayedRunner(ExecutorService fService) {
        this.fService = fService;
    }

    public void runChild(final Runnable childStatement) {
        fResults.add(new Callable<Object>() {
            public Object call() throws Exception {
                childStatement.run();
                return null;
            }
        });
    }


    public void finished() {
    }

    public void done() throws InterruptedException, ExecutionException {
        List<Future<Object>> futures = fService.invokeAll(fResults);
        for (Future<Object> each : futures)
               each.get();
        fService.shutdown();
    }

}
