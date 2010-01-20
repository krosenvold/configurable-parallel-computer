/*
 * Copyright 2002-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Also licensed under CPL http://junit.sourceforge.net/cpl-v10.html
 */


package org.jdogma.junit;

import org.junit.runners.model.RunnerScheduler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/*
 * An asynchronous runner that does not wait for scheduled tests to complete.
 *
 * The rationale behind this
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class NonBlockingAsynchronousRunner implements RunnerScheduler {
    private final List<Future<Object>> futures = Collections.synchronizedList(new ArrayList<Future<Object>>());
    private final ExecutorService fService;

    public NonBlockingAsynchronousRunner(ExecutorService fService) {
        this.fService = fService;
    }

    public void schedule(final Runnable childStatement) {
        final Callable<Object> objectCallable = new Callable<Object>() {
            public Object call() throws Exception {
                childStatement.run();
                return null;
            }
        };
        futures.add(fService.submit(objectCallable));
    }


    public void finished() {
    }

    public void waitForCompletion() {
        for (Future<Object> each : futures)
            try {
               each.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
    }

}