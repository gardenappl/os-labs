// Run() is called from Scheduling.main() and is where
// the scheduling algorithm written by the user resides.
// User modification should occur within the Run() function.

package com.prenticehall.moss.sched;

import java.util.*;
import java.io.*;

public class SchedulingAlgorithm {
    private final PrintStream printStream;
    private Results result;

    private ArrayList<Process> processes;

    //How much time has the current user (and associated process) worked without interruption
    private int userQuantTime = 0;
    private HashMap<Integer, Integer> processQuantTimes;
    //Queues for users with ready processes, and the ready processes themselves
    private Deque<Integer> userRoundRobin;
    private HashMap<Integer, Deque<Integer>> processRoundRobins;

    
    public SchedulingAlgorithm(String resultsFile) throws IOException {
        this.printStream = new PrintStream(new FileOutputStream(resultsFile));
    }

    public Results run(int maxRuntime, int quantum, int quantumUser, Vector<Process> processes) {
        this.processes = new ArrayList<>(processes);

        result = new Results("Fair-share", 0);

        userRoundRobin = new LinkedList<>();
        Set<Integer> knownUsers = new HashSet<>(processes.size());

        for (Process process : processes) {
            int userId = process.userId;
            if (!knownUsers.contains(userId)) {
                knownUsers.add(userId);
                userRoundRobin.add(userId);
            }
        }

        processRoundRobins = new HashMap<>(knownUsers.size());
        for (int userId : knownUsers)
            processRoundRobins.put(userId, new LinkedList<>());

        for (int processId = 0; processId < processes.size(); processId++) {
            Process process = processes.get(processId);
            processRoundRobins.get(process.userId).add(processId);
        }

        processQuantTimes = new HashMap<>(knownUsers.size());
        for (int userId : knownUsers)
            processQuantTimes.put(userId, 0);
        userQuantTime = 0;

        boolean lastStepAllEmpty = false;
        while (result.totalTime < maxRuntime && !processes.isEmpty()) {
            //Ensure current user has ready processes
            if (userRoundRobin.isEmpty()) {
                if (!lastStepAllEmpty) {
                    log("Process queues for all users are empty, no current process ID...");
                    lastStepAllEmpty = true;
                }
                updateAllProcesses(-1);
                continue;
            }
            
            //Do round-robin for users
            if (userQuantTime != 0 && userQuantTime % quantumUser == 0) {
                log("Quant time expired for user ID %d", userRoundRobin.peekFirst());
                scheduleNextUser(userRoundRobin);
            }

            int userId = userRoundRobin.peekFirst();
            Deque<Integer> processRoundRobin = processRoundRobins.get(userId);
            

            //Do round-robin for processes
            int processQuantTime = processQuantTimes.get(userId);
            if (processQuantTime != 0 && processQuantTime % quantum == 0) {
                log("Quant time expired for %s", getProcessInfo(processRoundRobin.peekFirst()));
                scheduleNextProcess(processRoundRobin, userId);
            }

            int processId = processRoundRobin.peekFirst();

            updateAllProcesses(processId);
        }
        return result;
    }
    
    private void updateAllProcesses(int selectedProcessId) {
        for (int processId = 0; processId < processes.size(); processId++) {
            Process process = processes.get(processId);

            if (isBlockedIO(process)) {
                //log("blocked %s", getProcessInfo(processId));
                process.currentBlockTime++;

                if (!isBlockedIO(process)) {
                    //Process is now ready
                    process.currentRunTime = 0;
                    process.currentBlockTime = 0;
                    log("Process is now ready, pushing to queue: %s", getProcessInfo(processId));
                    processRoundRobins.get(process.userId).addLast(processId);
                    if (!userRoundRobin.contains(process.userId)) {
                        log("User %d now has ready processes, pushing to queue...", process.userId);
                        userRoundRobin.addLast(process.userId);
                    }
                }
            } else if (processId == selectedProcessId) {
                //log("running %s", getProcessInfo(processId));
                process.currentRunTime++;
                process.totalRunTime++;
                int processQuantTime = processQuantTimes.get(process.userId);
                processQuantTimes.put(process.userId, processQuantTime + 1);
                userQuantTime++;
                
               
                if (process.totalRunTime == process.maxTime) {
                    //Process completed

                    processQuantTimes.put(process.userId, 0);

                    log("Process completed! %s", getProcessInfo(processId));
                    processId--;
                    processes.remove(process);

                    processRoundRobins.get(process.userId).removeFirst();
                    if (processRoundRobins.get(process.userId).isEmpty()) {
                        log("User %d now has 0 ready processes, removing from queue...", process.userId);
                        userRoundRobin.removeFirst();
                    }
                } else if (isBlockedIO(process)) {
                    //Process no longer ready
                    process.numBlocked++;
                    processQuantTimes.put(process.userId, 0);

                    log("Process is now blocked, removing from queue: %s", getProcessInfo(processId));

                    processRoundRobins.get(process.userId).removeFirst();
                    if (processRoundRobins.get(process.userId).isEmpty()) {
                        log("User %d now has 0 ready processes, removing from queue...", process.userId);
                        userRoundRobin.removeFirst();
                    }
                }
            } /*else {
                log("ready %s", getProcessInfo(processId));
            }*/
        }
        result.totalTime++;
    }

    private String getProcessInfo(int processId) {
        Process process = processes.get(processId);
        return String.format("Process #%d (total %d/%d ms, running %d/%d ms, blocking %d/%d ms, owner: %d)",
                processId, process.totalRunTime, process.maxTime,
                process.currentRunTime, process.runTime,
                process.currentBlockTime, process.blockTime, process.userId);
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
     * @param args      format arguments, passed into printf
     */
    private void log(String format, Object... args) {
        log(String.format(format, args));
    }

    /**
     * Print current run time and an object
     *
     * @param message object which will be printed as a message
     */
    private void log(Object message) {
        if (message == null)
            log("null");
        else
            log(message.toString());
    }


    /**
     * Print current run time and a string. Prints to output file and to standard output.
     *
     * @param message message string
     */
    private void log(String message) {
        System.out.println(result.totalTime + " :: " + message);
        printStream.println(result.totalTime + " :: " + message);
    }
    

    /**
     * Push current process to the end of the deque
     * @return processId of next process
     */
    private int scheduleNextProcess(Deque<Integer> processRoundRobin, int userId) {
        processQuantTimes.put(userId, 0);

        int processId = processRoundRobin.removeFirst();
        processRoundRobin.addLast(processId);

        int nextProcessId = processRoundRobin.peekFirst();
        log("Scheduling process %s", getProcessInfo(nextProcessId));
        return nextProcessId;
    }

    /**
     * Push current user to the end of the deque
     * @return id of next user
     */
    private int scheduleNextUser(Deque<Integer> userRoundRobin) {
        userQuantTime = 0;

        int userId = userRoundRobin.removeFirst();
        userRoundRobin.addLast(userId);

        int nextUserId = userRoundRobin.peekFirst();
        log("Scheduling user #%d", nextUserId);
        log("Next process: %s", getProcessInfo(processRoundRobins.get(nextUserId).peekFirst()));
        return nextUserId;
    }
}
