package org.junit.experimental;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerInterceptor;

public class ConfigurableParallelComputer extends Computer {
    private final boolean fClasses;
    private final boolean fMethods;
    private final boolean fixedPool;
    private final ExecutorService fService;
    private final RunnerInterceptor methodRunnerInterceptor;
    private final RunnerInterceptor classRunnerInterceptor;


    public ConfigurableParallelComputer() {
        this (true, true);
    }

    public ConfigurableParallelComputer(boolean fClasses, boolean fMethods) {
        this.fClasses = fClasses;
        this.fMethods = fMethods;
        System.out.println("Unlimited thread pool created");
        fixedPool = false;
        fService = Executors.newCachedThreadPool();
        this.classRunnerInterceptor = this.methodRunnerInterceptor = new SingleExecutorServiceRunner(fService);
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

    public void close(){
        if (this.methodRunnerInterceptor instanceof ConcurrentRunnerInterceptorBase){
            try {
                ((ConcurrentRunnerInterceptorBase) methodRunnerInterceptor).done();
            } catch (InterruptedException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
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

    private Runner parallelize(Runner runner, RunnerInterceptor runnerInterceptor) {
        if (runner instanceof ParentRunner<?>) {
            ((ParentRunner<?>) runner).setRunnerInterceptor( runnerInterceptor);
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

}
