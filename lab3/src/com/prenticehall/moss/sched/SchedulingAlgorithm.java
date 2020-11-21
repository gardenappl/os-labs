// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

package com.prenticehall.moss.sched;

import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {
    private int totalRuntime = 0;
    private int userQuantTime = 0;
    private HashMap<Integer, Integer> processQuantTimes;
    private Vector<Process> processes;
    private final PrintStream printStream;

    public SchedulingAlgorithm(String resultsFile) throws IOException {
        this.printStream = new PrintStream(new FileOutputStream(resultsFile));
    }

    public Results run(int maxRuntime, int quantum, Vector<Process> processes) {
        this.processes = processes;

        Results result = new Results("Fair-share", 0);

        totalRuntime = 0;

        //Queue for users
        Deque<Integer> userRoundRobin = new LinkedList<>();
        Set<Integer> knownUsers = new HashSet<>(processes.size());

        for (Process process : processes) {
            int userId = process.userId;
            if (!knownUsers.contains(userId)) {
                knownUsers.add(userId);
                userRoundRobin.add(userId);
            }
        }

        //Queue for processes, separate queue for each user
        HashMap<Integer, Deque<Integer>> processRoundRobins = new HashMap<>(knownUsers.size());
        for (int userId : knownUsers)
            processRoundRobins.put(userId, new LinkedList<>());

        for (int processId = 0; processId < processes.size(); processId++) {
            Process process = processes.get(processId);
            processRoundRobins.get(process.userId).add(processId);
        }

        //How much time has the current user (and associated process) worked without interruption
        processQuantTimes = new HashMap<>(knownUsers.size());
        userQuantTime = 0;
        final int USER_QUANT_MULTIPLIER = 3;

        while (totalRuntime < maxRuntime && !userRoundRobin.isEmpty()) {
            //Do round-robin for users
            if (userQuantTime % (quantum * USER_QUANT_MULTIPLIER) == 0) {
                log("Quant time expired for user ID %d", userRoundRobin.peekFirst());
                scheduleNextUser(userRoundRobin);
            }

            int userId = userRoundRobin.peekFirst();
            Deque<Integer> processRoundRobin = processRoundRobins.get(userId);
            int processQuantTime = processQuantTimes.get(userId);


            //Do round-robin for processes
            if (processQuantTime % quantum == 0) {
                log("Quant time expired for process ID %d", processRoundRobin.peekFirst());
                scheduleNextProcess(processRoundRobin, userId);
            }

            int processId = processRoundRobin.peekFirst();


            //Update current process


            processQuantTimes.put(userId, processQuantTime + 1);
            userQuantTime++;
            totalRuntime++;
        }
        return result;
    }

    private String getProcessInfo(int processId) {
        Process process = processes.get(processId);
        return String.format("Process #%d (%d/%d ms, I/O block every %d ms)",
                processId, process.totalRunTime, process.runTime, process.blockTime);
    }

    /**
     * Simulate one ms passing for a process.
     * Will increment run time if it's currently scheduled,
     * or increment block time if it's I/O blocked.
     *
     * @param currentlyScheduled true if this process is currently running on the CPU
     */
    private static void updateProcess(Process process, boolean currentlyScheduled) {
        if (currentlyScheduled) {
            process.totalRunTime++;
            process.currentRunTime++;
        }
    }

    /**
     * Returns true if this process is currently blocked and waiting for I/O.
     *
     * @param process
     */
    private static boolean isBlockedIO(Process process) {
        return process.currentRunTime == process.runTime && process.currentBlockTime < process.blockTime;
    }

    /**
     * Print current run time and a formatted message
     *
     * @param format format string, passed into pritnf
     * @param o      format arguments, passed into printf
     */
    private void log(String format, Object... o) {
        printStream.printf("%d :: " + format, totalRuntime, o);
    }

    /**
     * Push current process to the end of the deque
     */
    private void scheduleNextProcess(Deque<Integer> processRoundRobin, int userId) {
        processQuantTimes.put(userId, 0);

        int processId = processRoundRobin.pollFirst();
        processRoundRobin.addLast(processId);

        int nextProcessId = processRoundRobin.peekFirst();
        log("Scheduling process %s", getProcessInfo(nextProcessId));
    }

    /**
     * Push current user to the end of the deque
     */
    private void scheduleNextUser(Deque<Integer> userRoundRobin) {
        userQuantTime = 0;

        int userId = userRoundRobin.pollFirst();
        userRoundRobin.addLast(userId);

        int nextUserId = userRoundRobin.peekFirst();
        log("Scheduling user #%d", nextUserId);
    }
}
