package ua.yuhrysh.oslab1.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BinaryOperator;

public class ComputationManager {
    private final String path;
    private ArrayList<ComputationCommunicationThread> threads;
    private final BinaryOperator<Integer> operator;
    private final List<IntComputation> computations;

    public ComputationManager(String path, List<IntComputation> computations, BinaryOperator<Integer> operator) {
        this.path = path;
        this.computations = computations;
        this.operator = operator;
    }

    
}
