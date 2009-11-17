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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

/*
 * @author Kristian Rosenvold, kristianAzeniorD0Tno
 */
public class ConfigurableParallelComputer extends Computer {
    private final boolean fClasses;
    private final boolean fMethods;
    private final boolean fixedPool;
    private final ExecutorService fService;
    private final RunnerScheduler methodRunnerInterceptor;
    private final RunnerScheduler classRunnerInterceptor;


    public ConfigurableParallelComputer() {
        this (true, true);
    }

    public ConfigurableParallelComputer(boolean fClasses, boolean fMethods) {
        this.fClasses = fClasses;
        this.fMethods = fMethods;
        fixedPool = false;
        fService = Executors.newCachedThreadPool();
        this.classRunnerInterceptor = new SingleExecutorServiceRunner(fService);
        this.methodRunnerInterceptor = new SingleExecutorServiceRunner(fService);
    }

    public ConfigurableParallelComputer(boolean fClasses, boolean fMethods, Integer numberOfThreads, boolean perCore) {
        this.fClasses = fClasses;
        this.fMethods = fMethods;
        int totalThreads = numberOfThreads * (perCore ? Runtime.getRuntime().availableProcessors() : 1);
        fixedPool = true;
        fService = Executors.newFixedThreadPool(totalThreads);
        this.methodRunnerInterceptor = new DelayedRunner( fService);
        this.classRunnerInterceptor = new DelayedClassRunner();
    }

    public void close() throws ExecutionException {
        if (this.methodRunnerInterceptor instanceof ConcurrentRunnerInterceptorBase){
            try {
                ((ConcurrentRunnerInterceptorBase) methodRunnerInterceptor).done();
            } catch (InterruptedException e) {
                e.printStackTrace();  
            }
        }
        if (this.classRunnerInterceptor instanceof ConcurrentRunnerInterceptorBase){
            try {
                ((ConcurrentRunnerInterceptorBase) classRunnerInterceptor).done();
            } catch (InterruptedException e) {
                e.printStackTrace();  
            }
        }
        fService.shutdown();
        try {
        fService.awaitTermination(10, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e){
            throw new RuntimeException(e);
        }
    }

    public static Computer classes(Integer numberOfThreads, boolean perCore) {
        return new ConfigurableParallelComputer(true, false, numberOfThreads, perCore);
    }

    public static Computer methods(Integer numberOfThreads, boolean perCore) {
        return new ConfigurableParallelComputer(false, true, numberOfThreads, perCore);
    }

    private Runner parallelize(Runner runner, RunnerScheduler runnerInterceptor) {
        if (runner instanceof ParentRunner<?>) {
            ((ParentRunner<?>) runner).setScheduler( runnerInterceptor);
        }
        return runner;
    }

    @Override
    public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes) throws InitializationError {
        Runner suite = super.getSuite(builder, classes);
        return fClasses ? parallelize(suite, classRunnerInterceptor) : suite;
    }

    @Override
    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass) throws Throwable {
        Runner runner = super.getRunner(builder, testClass);
        return fMethods ? parallelize(runner, methodRunnerInterceptor) : runner;
    }

    @Override
    public String toString() {
        return "ConfigurableParallelComputer{" +
                "classes=" + fClasses +
                ", methods=" + fMethods +
                ", fixedPool=" + fixedPool +
                '}';
    }
}
