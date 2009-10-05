package org.junit.experimental;

import org.junit.runners.model.RunnerScheduler;


public class DelayedClassRunner extends ConcurrentRunnerInterceptorBase implements RunnerScheduler {
    public void schedule(final Runnable childStatement) {
        childStatement.run();
    }

    public void finished() {
    }

    public void done() throws InterruptedException {
    }

}

