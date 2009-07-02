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


package org.junit.experimental;

import org.junit.runners.model.RunnerInterceptor;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import com.sun.corba.se.impl.orbutil.concurrent.Sync;

/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */

public class SingleExecutorServiceRunner extends ConcurrentRunnerInterceptorBase implements RunnerInterceptor {
    private final ExecutorService fService;
    private final ConcurrentLinkedQueue<Future<Object>> fResults = new ConcurrentLinkedQueue<Future<Object>>();

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
