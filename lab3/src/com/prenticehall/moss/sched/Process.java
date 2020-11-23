package com.prenticehall.moss.sched;


class Process {
    //Process ends once totalRunTime reaches this value
    public final int maxTime;
    //Process blocks once currentRunTime reaches this value
    public final int runTime;
    public int currentRunTime = 0;
    public int totalRunTime = 0;
    //Process is unblocked once currentBlockedTime reaches this value
    public final int blockTime;
    public int currentBlockTime = 0;
    public int numBlocked = 0;
    //New parameter
    public final int userId;

    Process(int maxTime, int runTime, int blockTime, int userId) {
        this.runTime = runTime;
        this.blockTime = blockTime;
        this.userId = userId;
        this.maxTime = maxTime;
    }
}
