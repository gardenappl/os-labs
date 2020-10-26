package ua.yuhrysh.oslab1.manager;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.*;
import java.util.function.BinaryOperator;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        try {
            System.setErr(new PrintStream("error.log"));
        } catch (FileNotFoundException e) {
            System.err.println("Note: can't create error log! Will output debug messages to console.");
        }

        ArrayList<IntComputation> computations = new ArrayList<>();

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter function name ('f', 'g', 'a', 'zero' or 'freeze'), or 'start' to start computations: ");
            String input = scanner.nextLine();

            if (input.equalsIgnoreCase("start")) {
                if (computations.isEmpty()) {
                    System.out.println("Please enter at least one term.");
                    continue;
                } else {
                    break;
                }
            }

            IntComputation.Type computationType = null;
            for (IntComputation.Type type : IntComputation.Type.values()) {
                if (input.equalsIgnoreCase(type.getInternalName())) {
                    //System.out.println("equal to " + type.getInternalName());
                    computationType = type;
                }
            }
            if (computationType == null) {
                System.out.println("Invalid function name.");
                continue;
            }

            System.out.print("Enter function argument: ");
            int arg = -1;
            try {
                input = scanner.nextLine();
                arg = Integer.parseInt(input);
            } catch (NumberFormatException ignored) {
            }
            if ((computationType == IntComputation.Type.F || computationType == IntComputation.Type.G) &&
                    (arg < 0 || arg > 5)) {
                System.out.println("Invalid argument. Please enter integer in the [0; 5] range.");
                continue;
            }
            computations.add(new IntComputation(arg, computationType));
            if (computations.size() > 6) {
                System.out.println("Warning! Setup script only creates 6 FIFOs.");
                System.out.println("If you run this without any additional setup the program will not work.");
            }
        }

        BinaryOperator<Integer> operator = (Integer a, Integer b) -> a * b;

        new ComputationManager(System.getenv("user.dir"), computations, operator).run();
    }
}
