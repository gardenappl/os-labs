package ua.yuhrysh.oslab1.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<IntComputation> computations = Arrays.asList(
                //new IntComputation(2, IntComputation.Type.F), //this returns 0
                new IntComputation(3, IntComputation.Type.F), //this gets stuck
                new IntComputation(0, IntComputation.Type.F),
                new IntComputation(0, IntComputation.Type.G),
                new IntComputation(1, IntComputation.Type.F),
                new IntComputation(1, IntComputation.Type.G));

        BinaryOperator<Integer> operator = (Integer a, Integer b) -> a * b;

        new ComputationManager(System.getenv("user.dir"), computations, operator).run();
    }
}
