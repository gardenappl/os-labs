// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

package com.prenticehall.moss.sched;

import java.util.Vector;
import java.io.*;

public class SchedulingAlgorithm {

    public static Results run(int runtime, Vector<Process> processVector) throws FileNotFoundException {
        String resultsFile = "Summary-Processes";
        Results result = new Results("Fair-share", 0);

        //BufferedWriter out = new BufferedWriter(new FileWriter(resultsFile));
        //OutputStream out = new FileOutputStream(resultsFile);
        
        try (PrintStream out = new PrintStream(new FileOutputStream(resultsFile))) {

        } catch (FileNotFoundException e) {
            throw e;
        }
        return result;
    }
    
    private static String getProcessInfo(Process process) {
        return String.format("%d/%d ms, I/O block every %d ms", process.totalRunTime, process.runTime, process.blockTime);
    }
}
