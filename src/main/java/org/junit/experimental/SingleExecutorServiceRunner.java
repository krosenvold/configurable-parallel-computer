package org.junit.experimental;

import org.junit.runners.model.RunnerInterceptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
* User: kristian
* Date: Jun 30, 2009
* Time: 5:52:41 PM
* To change this template use File | Settings | File Templates.
*/
public class SingleExecutorServiceRunner extends ConcurrentRunnerInterceptorBase implements RunnerInterceptor {
    private final ExecutorService fService;
    private final List<Future<Object>> fResults = new ArrayList<Future<Object>>();

    SingleExecutorServiceRunner(ExecutorService fService) {
        this.fService = fService;
    }


    public void runChild(final Runnable childStatement) {
        fResults.add(fService.submit(new Callable<Object>() {
            public Object call() throws Exception {
                childStatement.run();
                return null;
            }
        }));
    }

    public void finished() {
        // DO nothin
//        for (Future<Object> each : fResults)
 //           try {
  //              each.get();
    //        } catch (Exception e) {
      //          e.printStackTrace();
         //   }
    }
    public void done() {
    }
}
