package ua.yuhrysh.oslab1.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class ComputationManager {
    private final String path;
    private ComputationRunnable[] runnables;
    private volatile Thread[] threads;
    private final BinaryOperator<Integer> operator;
    private final List<IntComputation> computations;
    private volatile int[] results;

    public ComputationManager(String path, List<IntComputation> computations, BinaryOperator<Integer> operator) {
        this.path = path;
        this.computations = computations;
        this.operator = operator;

        threads = new Thread[computations.size()];
        results = new int[computations.size()];
        runnables = new ComputationRunnable[computations.size()];
    }

    public String getPath() {
        return path;
    }

    //Called from ComputationRunnable threads
    synchronized void onComputationFinished(int result, int id) {
        results[id] = result;
        System.out.println("Result on " + id + " is " + result);
        if (id == 1) {
            for (ComputationRunnable runnable : runnables) {
                runnable.killProcess();
            }
            System.out.println("Cleaning up...");
            try {
                for (Thread thread : threads) {
                    if (thread != Thread.currentThread())
                        thread.join();
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted: " + e);
            }
            System.out.println("Done.");
        }
    }

    public void run() {
        for (int i = 0; i < computations.size(); i++) {
            runnables[i] = new ComputationRunnable(computations.get(i), i, this);
            threads[i] = new Thread(runnables[i]);
            threads[i].start();
        }
        
    }
}
