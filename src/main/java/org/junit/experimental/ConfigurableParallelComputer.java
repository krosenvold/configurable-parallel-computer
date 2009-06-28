package org.junit.experimental;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.runner.Computer;
import org.junit.runner.Runner;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.junit.runners.model.RunnerInterceptor;

public class ConfigurableParallelComputer extends Computer {
    private final boolean fClasses;
    private final boolean fMethods;
    private final ExecutorService fService;


    public ConfigurableParallelComputer() {
        this.fClasses = true;
        this.fMethods = true;
        System.out.println("Unlimited thread pool created");
        fService = Executors.newCachedThreadPool(); 
    }

    public ConfigurableParallelComputer(boolean fClasses, boolean fMethods, Integer numberOfThreads, boolean perCore) {
        this.fClasses = fClasses;
        this.fMethods = fMethods;
        int totalThreads = numberOfThreads * (perCore ? Runtime.getRuntime().availableProcessors() : 1);
        System.out.println("Created thread pool with " + totalThreads + "thread");
        fService = Executors.newFixedThreadPool(totalThreads);
    }

    public void close(){
        fService.shutdown();
    }

    public static Computer classes(Integer numberOfThreads, boolean perCore) {
        return new ConfigurableParallelComputer(true, false, numberOfThreads, perCore);
    }

    public static Computer methods(Integer numberOfThreads, boolean perCore) {
        return new ConfigurableParallelComputer(false, true, numberOfThreads, perCore);
    }

    private static Runner parallelize(Runner runner, ExecutorService executorService) {
        if (runner instanceof ParentRunner<?>) {
            ((ParentRunner<?>) runner).setRunnerInterceptor(new MyRunnerINterceptor(executorService ));
        }
        return runner;
    }

    @Override
    public Runner getSuite(RunnerBuilder builder, java.lang.Class<?>[] classes)
            throws InitializationError {
        Runner suite = super.getSuite(builder, classes);
        return fClasses ? parallelize(suite, fService) : suite;
    }

    @Override
    protected Runner getRunner(RunnerBuilder builder, Class<?> testClass)
            throws Throwable {
        Runner runner = super.getRunner(builder, testClass);
        return fMethods ? parallelize(runner, fService) : runner;
    }

    public static class MyRunnerINterceptor implements RunnerInterceptor {
        private final ExecutorService fService;
        private final List<Future<Object>> fResults = new ArrayList<Future<Object>>();


        MyRunnerINterceptor(ExecutorService fService) {
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
            for (Future<Object> each : fResults)
                try {
                    each.get();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }
    }
}
