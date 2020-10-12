package ua.yuhrysh.oslab1.manager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BinaryOperator;

public class Main {
    public static void main(String[] args) {
        List<IntComputation> computations = Arrays.asList(
                new IntComputation(0, IntComputation.Type.F),
                new IntComputation(0, IntComputation.Type.G));

        new ComputationManager(System.getenv("user.dir"), computations, Integer::sum).run();
    }
}
