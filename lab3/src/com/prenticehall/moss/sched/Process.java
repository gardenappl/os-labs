package com.prenticehall.moss.sched;


class Process {
    public final int runTime;
    public int currentRunTime = 0;
    public int totalRunTime = 0;
    public final int blockTime;
    public int currentBlockTime = 0;
    public int numBlocked = 0;
    public final int userId;

    Process(int runTime, int blockTime, int userId) {
        this.runTime = runTime;
        this.blockTime = blockTime;
        this.userId = userId;
    }
}
