// This file contains the main() function for the Scheduling
// simulation.  Init() initializes most of the variables by
// reading from a provided file.  SchedulingAlgorithm.Run() is
// called from main() to run the simulation.  Summary-Results
// is where the summary results are written, and Summary-Processes
// is where the process scheduling summary is written.

// Created by Alexander Reeder, 2001 January 06

package phall.moss.sched;

import java.io.*;
import java.util.*;


public class Scheduling {

    private static int processnum = 5;
    private static int meanDev = 1000;
    private static int standardDev = 100;
    private static int runtime = 1000;
    private static final Vector<Process> processVector = new Vector<>();
    private static Results result = new Results("null", "null", 0);
    private static final String resultsFile = "Summary-Results";
    private static final Random rng = new Random();

    private static void Init(String file) {
        File f = new File(file);
        String line;
        int lineNumber = 0;

        try {
            BufferedReader in = new BufferedReader(new FileReader(f));
            //DataInputStream in = new DataInputStream(new FileInputStream(f));
            while ((line = in.readLine()) != null) {
                lineNumber++;
                if (line.startsWith("numprocess")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    processnum = Integer.parseInt(st.nextToken());
                }
                if (line.startsWith("meandev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    meanDev = Integer.parseInt(st.nextToken());
                }
                if (line.startsWith("standdev")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    standardDev = Integer.parseInt(st.nextToken());
                }
                if (line.startsWith("process")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    int ioblocking = Integer.parseInt(st.nextToken());
                    int cputime = (int)(rng.nextGaussian() * standardDev + meanDev);
                    processVector.addElement(new Process(cputime, ioblocking, 0, 0, 0));
                }
                if (line.startsWith("runtime")) {
                    StringTokenizer st = new StringTokenizer(line);
                    st.nextToken();
                    runtime = Integer.parseInt(st.nextToken());
                }
            }
            in.close();
        } catch (NumberFormatException e) {
            System.err.printf("Invalid number on line %d of %s:\n", lineNumber, file);
            System.err.println(e.getLocalizedMessage());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void debug() {
        int i = 0;

        System.out.println("processnum " + processnum);
        System.out.println("meandevm " + meanDev);
        System.out.println("standdev " + standardDev);
        int size = processVector.size();
        for (i = 0; i < size; i++) {
            Process process = processVector.elementAt(i);
            System.out.println("process " + i + " " + process.cputime + " " + process.ioblocking + " " + process.cpudone + " " + process.numblocked);
        }
        System.out.println("runtime " + runtime);
    }

    public static void main(String[] args) {
        int i = 0;

        if (args.length != 1) {
            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
            System.exit(-1);
        }
        File f = new File(args[0]);
        if (!(f.exists())) {
            System.out.println("Scheduling: error, file '" + f.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(f.canRead())) {
            System.out.println("Scheduling: error, read of " + f.getName() + " failed.");
            System.exit(-1);
        }
        System.out.println("Working...");
        Init(args[0]);
        if (processVector.size() < processnum) {
            i = 0;
            while (processVector.size() < processnum) {
                int cputime = (int)(rng.nextGaussian() * standardDev + meanDev);
                processVector.addElement(new Process(cputime, i * 100, 0, 0, 0));
                i++;
            }
        }
        result = SchedulingAlgorithm.Run(runtime, processVector, result);
        try {
            //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
            PrintStream out = new PrintStream(new FileOutputStream(resultsFile));
            out.println("Scheduling Type: " + result.schedulingType);
            out.println("Scheduling Name: " + result.schedulingName);
            out.println("Simulation Run Time: " + result.compuTime);
            out.println("Mean: " + meanDev);
            out.println("Standard Deviation: " + standardDev);
            out.println("Process #\tCPU Time\tIO Blocking\tCPU Completed\tCPU Blocked");
            for (i = 0; i < processVector.size(); i++) {
                Process process = (Process) processVector.elementAt(i);
                out.print(Integer.toString(i));
                if (i < 100) {
                    out.print("\t\t");
                } else {
                    out.print("\t");
                }
                out.print(Integer.toString(process.cputime));
                if (process.cputime < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(Integer.toString(process.ioblocking));
                if (process.ioblocking < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.print(Integer.toString(process.cpudone));
                if (process.cpudone < 100) {
                    out.print(" (ms)\t\t");
                } else {
                    out.print(" (ms)\t");
                }
                out.println(process.numblocked + " times");
            }
            out.close();
        } catch (IOException e) { /* Handle exceptions */ }
        System.out.println("Completed.");
    }
}

