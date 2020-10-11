package ua.yuhrysh.oslab1.manager;

import spos.lab1.demo.IntOps;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ComputationCommunicationThread implements Runnable {
    private Process process;
    private final int id;
    private final String path;
    private IntComputation computation;

    private static final String COMMAND = "java -cp 'lib/*:out/production/lab1/' ua.yuhrysh.oslab1.compute.ComputationProcess";
    
    public ComputationCommunicationThread(IntComputation computation, int id, String path) {
        this.id = id;
        this.path = path;
        this.computation = computation;
    }

    @Override
    public void run() {
        File inputPipe = new File(path, id + ".in");
        File outputPipe = new File(path, id + ".out");
        try {
            process = Runtime.getRuntime().exec(COMMAND + ' ' +
                    inputPipe.getAbsolutePath() + ' ' +
                    outputPipe.getAbsolutePath());
        } catch (IOException e) {
            System.err.print("Error: " + e.toString());
        }

        try (BufferedWriter input = new BufferedWriter(new FileWriter(inputPipe));
             BufferedReader output = new BufferedReader(new FileReader(outputPipe))) {

            input.write(computation.getType().getInternalName());
            input.write(computation.getArgument());

            int result = Integer.parseInt(output.readLine());
            
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        }
    }
}
