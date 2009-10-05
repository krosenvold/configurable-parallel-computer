package org.junit.experimental;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutionException;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerScheduler;

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
        if (fClasses || fMethods)
            System.out.println("Unlimited thread pool created");
        else
            System.out.println("Single thread will be used");
        fixedPool = false;
        fService = Executors.newCachedThreadPool();
        this.classRunnerInterceptor = new SingleExecutorServiceRunner(fService);
        this.methodRunnerInterceptor = new SingleExecutorServiceRunner(fService);
    }

    public ConfigurableParallelComputer(boolean fClasses, boolean fMethods, Integer numberOfThreads, boolean perCore) {
        this.fClasses = fClasses;
        this.fMethods = fMethods;
        int totalThreads = numberOfThreads * (perCore ? Runtime.getRuntime().availableProcessors() : 1);
        System.out.println("Created thread pool with " + totalThreads + " threads");
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
