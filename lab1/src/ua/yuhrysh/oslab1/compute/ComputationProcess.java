package ua.yuhrysh.oslab1.compute;

import spos.lab1.demo.IntOps;

import java.io.*;

public class ComputationProcess {
    public static void main(String[] args) {
        System.err.println("Hello from process");
        if (args.length < 2) {
            System.err.println("Error: Please provide 2 arguments.");
            System.err.println("Usage: ua.yuhrysh.oslab1.compute.ComputationProcess <input fifo> <output fifo>");
            System.exit(1);
            return;
        }
        
        try (BufferedReader input = new BufferedReader(new FileReader(args[0]));
                BufferedWriter output = new BufferedWriter(new FileWriter(args[1]))) {
            System.err.println("Begin");
            String funcName = input.readLine();
            int funcArg = Integer.parseInt(input.readLine());
            int result;
            try {
                switch (funcName) {
                    case "f" -> result = IntOps.funcF(funcArg);
                    case "g" -> result = IntOps.funcG(funcArg);
                    case "zero" -> result = funcZero(funcArg);
                    case "a" -> result = funcA(funcArg);
                    case "freeze" -> result = funcFreeze(funcArg);
                    default -> {
                        System.err.println("Wrong function name");
                        System.exit(1);
                        return;
                    }
                }
            } catch (InterruptedException e) {
                System.err.println("Interrupted.");
                System.exit(1);
                return;
            }
            output.write(Integer.toString(result) + '\n');
            System.err.println("Result: " + result);
        } catch (IOException e) {
            System.err.println("Error: " + e.toString());
        }
    }
    
    private static int funcFreeze(int argument) throws InterruptedException {
        while (true) {
            Thread.sleep(1000);
        }
    }

    private static int funcA(int argument) throws InterruptedException {
        Thread.sleep(1000 * argument);
        return argument * 2;
    }
    
    private static int funcZero(int argument) throws InterruptedException {
        Thread.sleep(1000 * argument);
        return 0;
    }
}

