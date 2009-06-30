package org.junit.experimental;

import org.junit.runners.model.RunnerInterceptor;


public class DelayedClassRunner extends ConcurrentRunnerInterceptorBase implements RunnerInterceptor {
    public void runChild(final Runnable childStatement) {
        childStatement.run();
    }

    public void finished() {
    }

    public void done() throws InterruptedException {
    }

}

