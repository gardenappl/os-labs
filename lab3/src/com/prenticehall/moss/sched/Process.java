package com.prenticehall.moss.sched;


class Process {
    public final int runTime;
    public final int blockTime;
    public int numBlocked = 0;
    public int currentRunTime = 0;
    public int totalRunTime = 0;
    public final int userId = 0;

    Process(int runTime, int blockTime, int userId) {
        this.runTime = runTime;
        this.blockTime = blockTime;
    }
}
