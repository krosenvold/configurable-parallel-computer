package org.junit.experimental;

import org.junit.runners.model.RunnerInterceptor;

import java.util.concurrent.Callable;
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
public class DelayedRunner implements RunnerInterceptor {
    private final List<Callable<Object>> fResults = Collections.synchronizedList(new ArrayList<Callable<Object>>());

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
}
