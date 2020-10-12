package ua.yuhrysh.oslab1.manager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

public class ConsoleUI implements Runnable {
    //Non-blocking console IO implementation taken from
    //https://www.darkcoding.net/software/non-blocking-console-io-is-not-possible/

    private static String ttyConfig;
    
    private final ComputationManager manager;
    
    public ConsoleUI(ComputationManager manager) {
        this.manager = manager;
    }

    public void run() {
        try {
            setTerminalToCBreak();
            printInstructions();

            int i = 0;
            inputLoop:
            while (!Thread.interrupted()) {
                if (System.in.available() != 0) {

                    switch (System.in.read()) {
                        //escape key
                        case 0x1B:
                            manager.cancel();
                            break inputLoop;

                        case 'q':
                            if (quitPrompt()) {
                                manager.cancel();
                                break inputLoop;
                            } else {
                                printInstructions();
                            }
                    }
                }
                Thread.sleep(100);
            }
        }
        catch (IOException e) {
            System.err.println("Error: " + e);
        }
        catch (InterruptedException e) {
        }
        finally {
            quit();
        }
    }
    
    private void printInstructions() {
        clearScreen();
        System.out.println("Press q for exit prompt, or ESC to exit immediately.");
    }
    
    private boolean quitPrompt() throws IOException, InterruptedException {
        clearScreen();
        System.out.println("/--------------------------------------\\");
        System.out.println("| Are you sure you want to exit? (y/n) |");
        System.out.println("\\--------------------------------------/");
        System.out.println();
        System.out.println();
        long millisStart = System.currentTimeMillis();
        
        while (!Thread.interrupted()) {
            if (System.in.available() != 0) {
                switch (System.in.read()) {
                    case 'n':
                        return false;
                    case 'y':
                        return true;
                }
            }
            long millisCurrent = System.currentTimeMillis();
            long secondsElapsed = (millisCurrent - millisStart) / 1000;
            if (secondsElapsed > 15)
                break;

            //Move cursor up, erase line, print new line
            System.out.println("\033[1A \033[2K Program will exit in " + (15 - secondsElapsed) + " seconds.");

            Thread.sleep(100);
        }
        return true;
    }
    
    public void quit() {
        try {
            clearScreen();
            stty( ttyConfig.trim() );
        }
        catch (Exception e) {
            System.err.println("Error while restoring tty config: " + e);
        }
    }



    private static void clearScreen() {
        //Clear screen and move cursor to 0, 0
        System.out.println("\033[2J \033[H");
    }

    private static void setTerminalToCBreak() throws IOException, InterruptedException {
        ttyConfig = stty("-g");

        // set the console to be character-buffered instead of line-buffered
        stty("-icanon min 1");

        // disable character echoing
        stty("-echo");
    }

    /**
     *  Execute the stty command with the specified arguments
     *  against the current active terminal.
     */
    private static String stty(final String args)
            throws IOException, InterruptedException {
        String cmd = "stty " + args + " < /dev/tty";

        return exec(new String[] {
                "sh",
                "-c",
                cmd
        });
    }

    /**
     *  Execute the specified command and return the output
     *  (both stdout and stderr).
     */
    private static String exec(final String[] cmd)
            throws IOException, InterruptedException {
        ByteArrayOutputStream bout = new ByteArrayOutputStream();

        Process p = Runtime.getRuntime().exec(cmd);
        int c;
        InputStream in = p.getInputStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        in = p.getErrorStream();

        while ((c = in.read()) != -1) {
            bout.write(c);
        }

        p.waitFor();

        return new String(bout.toByteArray());
    }
}
