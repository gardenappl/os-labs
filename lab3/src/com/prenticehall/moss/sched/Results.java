package com.prenticehall.moss.sched;


class Results {
    public String schedulingName;
    public int totalTime;

    Results(String schedulingName, int compuTime) {
        this.schedulingName = schedulingName;
        this.totalTime = compuTime;
    }
}
