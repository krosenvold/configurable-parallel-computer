package org.junit.experimental;

import org.junit.runners.model.RunnerScheduler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by IntelliJ IDEA.
* User: kristian
* Date: Jun 30, 2009
* Time: 5:52:41 PM
* To change this template use File | Settings | File Templates.
*/
public class SingleExecutorServiceRunner extends ConcurrentRunnerInterceptorBase implements RunnerScheduler {
    private final ExecutorService fService;
    private final ConcurrentLinkedQueue<Future<Object>> fResults = new ConcurrentLinkedQueue<Future<Object>>();

    SingleExecutorServiceRunner(ExecutorService fService) {
        this.fService = fService;
    }


    public void schedule(final Runnable childStatement) {
        fResults.add(fService.submit(new Callable<Object>() {
            public Object call() throws Exception {
                childStatement.run();
                return null;
            }
        }));
    }

    public void finished() {
        // DO nothin
    }
    public void done() {
        for (Future<Object> each : fResults)
            try {
                each.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
    }
}
