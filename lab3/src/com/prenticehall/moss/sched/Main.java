package com.prenticehall.moss.sched;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: 'java Scheduling <INIT FILE>'");
            System.exit(-1);
        }
        File file = new File(args[0]);
        if (!(file.exists())) {
            System.out.println("Scheduling: error, file '" + file.getName() + "' does not exist.");
            System.exit(-1);
        }
        if (!(file.canRead())) {
            System.out.println("Scheduling: error, read of " + file.getName() + " failed.");
            System.exit(-1);
        }

        try {
            Scheduling scheduling = new Scheduling();
            scheduling.init(file);
            scheduling.run();
        } catch (IllegalConfigFileException e) {
            System.err.print("Illegal config file!");
            e.printStackTrace();
            System.exit(-1);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }
}
