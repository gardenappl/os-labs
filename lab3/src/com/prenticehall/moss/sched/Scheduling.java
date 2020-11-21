// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

package com.prenticehall.moss.sched;

import java.io.*;
import java.util.*;


public class Scheduling {
    private static final String resultsFile = "Summary-Results";
    private static final Random rng = new Random();
    private int maxRuntime;
    private int runTimeMean;
    private int runTimeStddev;
    private int blockTimeMean;
    private int blockTimeStddev;
    private int processCount;
    private int quantum;
    private ArrayList<Integer> ownerIds;
    private boolean initialized = false;

    public void init(File file) throws IOException, IllegalConfigFileException {
        String line;
        int lineNumber = 0;

        processCount = -1;
        maxRuntime = -1;
        runTimeMean = -1;
        runTimeStddev = -1;
        blockTimeMean = -1;
        blockTimeStddev = -1;
        quantum = -1;
        ownerIds.clear();


        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null) {
                lineNumber++;
                if (line.startsWith("num_process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processCount = Integer.parseInt(st.nextToken());
                } else if (line.startsWith("run_time_mean")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runTimeMean = Integer.parseInt(st.nextToken());
                } else if (line.startsWith("run_time_stddev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runTimeStddev = Integer.parseInt(st.nextToken());
                } else if (line.startsWith("block_time_mean")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    blockTimeMean = Integer.parseInt(st.nextToken());
                } else if (line.startsWith("block_time_stddev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    blockTimeStddev = Integer.parseInt(st.nextToken());
                } else if (line.startsWith("max_runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    maxRuntime = Integer.parseInt(st.nextToken());
                } else if (line.startsWith("process_owner")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    ownerIds.add(Integer.parseInt(st.nextToken()));
                }
            }

            if (processCount <= 0)
                throw new IllegalConfigFileException("Process count must be defined and > 0");
            if (runTimeMean <= 0)
                throw new IllegalConfigFileException("Mean run time must be defined and > 0");
            if (runTimeStddev < 0)
                throw new IllegalConfigFileException("Run time std. deviation must be defined and >= 0");
            if (blockTimeMean <= 0)
                throw new IllegalConfigFileException("Mean block time must be defined and > 0");
            if (blockTimeStddev < 0)
                throw new IllegalConfigFileException("Block time std. deviation must be defined and >= 0");
            if (maxRuntime < 0)
                throw new IllegalConfigFileException("Max. runtime must be defined and >= 0");
            if (ownerIds.size() != processCount)
                throw new IllegalConfigFileException("Wrong number of owner IDs");

            initialized = true;

        } catch (NumberFormatException e) {
            System.err.printf("Invalid number on line %d of %s:\n", lineNumber, file);
            System.err.println(e.getLocalizedMessage());
            throw new IllegalConfigFileException(e);
        }
    }

    public void run() throws IOException {
        if (!initialized)
            throw new IllegalStateException("Must be initialized first.");

        System.out.println("Working...");
        Vector<Process> processes = new Vector<>(processCount);

        for (int i = 0; i < processCount; i++) {
            int runTime = (int) (rng.nextGaussian() * runTimeStddev + runTimeMean);
            int blockTime = (int) (rng.nextGaussian() * runTimeStddev + runTimeMean);
            processes.addElement(new Process(runTime, blockTime, ownerIds.get(i)));
        }
        Results result = SchedulingAlgorithm.run(maxRuntime, quantum, processes);


        //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
        PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
        out.println("Scheduling Name: " + result.schedulingName);
        out.println("Simulation Run Time: " + result.compuTime);
        out.println("Mean: " + runTimeMean);
        out.println("Standard Deviation: " + runTimeStddev);
        out.println("Process #\t\tRuntime\tBlock time\tActual runtime\tTimes blocked");
        for (int i = 0; i < processes.size(); i++) {
            Process process = processes.elementAt(i);
            out.print(i);
            if (i < 100) {
                out.print("\t\t");
            } else {
                out.print("\t");
            }
            out.print(process.runTime);
            if (process.runTime < 100) {
                out.print(" (ms)\t\t");
            } else {
                out.print(" (ms)\t");
            }
            out.print(process.blockTime);
            if (process.blockTime < 100) {
                out.print(" (ms)\t\t");
            } else {
                out.print(" (ms)\t");
            }
            out.print(process.totalRunTime);
            if (process.totalRunTime < 100) {
                out.print(" (ms)\t\t");
            } else {
                out.print(" (ms)\t");
            }
            out.println(process.numBlocked + " times");
        }
        out.close();

        System.out.println("Completed.");
    }
}

