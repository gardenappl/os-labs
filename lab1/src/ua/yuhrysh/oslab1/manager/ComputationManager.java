package ua.yuhrysh.oslab1.manager;

import java.util.List;
import java.util.function.BinaryOperator;

public class ComputationManager {
    private final String path;
    private final BinaryOperator<Integer> operator;
    private final List<IntComputation> computations;

    private final ComputationRunnable[] runnables;
    private final Thread[] threads;
    private final int[] results;
    private final boolean[] resultAvailable;
    
    private boolean cancelled = false;


    public ComputationManager(String path, List<IntComputation> computations, BinaryOperator<Integer> operator) {
        this.path = path;
        this.computations = computations;
        this.operator = operator;

        threads = new Thread[computations.size()];
        results = new int[computations.size()];
        resultAvailable = new boolean[computations.size()];
        runnables = new ComputationRunnable[computations.size()];
    }

    public String getPath() {
        return path;
    }

    //Called from ComputationRunnable threads
    synchronized void onComputationFinished(int result, int id) {
        results[id] = result;
        resultAvailable[id] = true;
        System.err.println("Result on " + id + " is " + result);
        if (result == 0) {
            System.err.println("Result is 0, killing...");
            stop();
        }
    }
    
    synchronized void cancel() {
        System.err.println("Cancelling");
        cancelled = true;
        stop();
    }
    
    private void stop() {
        for (ComputationRunnable runnable : runnables) {
            runnable.killProcess();
        }
    }

    public void run() throws InterruptedException {
        for (int i = 0; i < computations.size(); i++) {
            runnables[i] = new ComputationRunnable(computations.get(i), i, this);
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }

        ConsoleUI consoleUI = new ConsoleUI(this);
        Thread uiThread = new Thread(consoleUI);
        uiThread.start();
        
        for (Thread thread : threads)
            thread.join();

        if (uiThread.isAlive()) {
            uiThread.interrupt();
            uiThread.join();
            consoleUI.quit();
        }
        
        if (!cancelled) {
            int result = results[0];
            for (int i = 1; i < results.length; i++)
                    result = operator.apply(result, results[i]);

            System.out.println("Result: " + result);
        } else {
            System.out.println("Cancelled.");
        }

        System.out.println("Calculation info:");
        for (int i = 0; i < computations.size(); i++) {
            if (resultAvailable[i]) {
                System.out.println(
                        computations.get(i).getType().getInternalName() +
                                '(' + computations.get(i).getArgument() + ") = " +
                                results[i]
                );
            } else {
                System.out.println(
                        computations.get(i).getType().getInternalName() +
                                '(' + computations.get(i).getArgument() + ") not done."
                );
            }
        }
    }
}
