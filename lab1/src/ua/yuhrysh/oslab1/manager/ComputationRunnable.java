package ua.yuhrysh.oslab1.manager;

import spos.lab1.demo.IntOps;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class ComputationRunnable implements Runnable {
    private Process process;
    private final int id;
    private final ComputationManager manager;
    private IntComputation computation;
    
    public ComputationRunnable(IntComputation computation, int id, ComputationManager manager) {
        this.id = id;
        this.manager = manager;
        this.computation = computation;
    }

    @Override
    public void run() {
        System.err.println("Hello");
        String path = manager.getPath();
        File inputPipe = new File(path, id + ".in");
        File outputPipe = new File(path, id + ".out");

        try {
            System.err.println(new File("").getAbsolutePath());
            process = new ProcessBuilder().command(
                    "java",
                    "-cp", "lib/*:out/production/lab1/",
                    "ua.yuhrysh.oslab1.compute.ComputationProcess",
                    inputPipe.getAbsolutePath(),
                    outputPipe.getAbsolutePath()
            )
                    .redirectError(new File(id + ".log"))
                    .start();

        } catch (IOException e) {
            System.err.print("Error: " + e.toString());
            return;
        }

        System.err.println("Started");
        try (BufferedWriter input = new BufferedWriter(new FileWriter(inputPipe));
             BufferedReader output = new BufferedReader(new FileReader(outputPipe))) {


            System.err.println("Opened");
            input.write(computation.getType().getInternalName());
            input.newLine();
            input.write(Integer.toString(computation.getArgument()));
            input.newLine();
            input.flush();
            System.err.println("Written");

            String result = output.readLine();
            System.err.println("Result is " + result);
            if (result != null)
                manager.onComputationFinished(Integer.parseInt(result), id);
            
        } catch (Exception e) {
            System.err.println("Error: " + e.toString());
            return;
        }
    }

    public void killProcess() {
        process.destroyForcibly();
    }
}
