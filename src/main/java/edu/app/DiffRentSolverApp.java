package edu.app;

import edu.models.TransportTable;
import edu.solvers.DiffRentSolver;
import edu.solvers.TransportTaskSolver;

public class DiffRentSolverApp {
    public static void main(String[] args) {
        double[][] priceMatrix = {
                {2, 2, 5, 7},
                {5, 6.1, 2, 3},
                {4, 4, 3, 6.2},
                {8, 2, 2, 7}
        };
        int[] producers = {170, 129, 115, 240};
        int[] consumers = {117, 140, 310, 87};

        TransportTable table = new TransportTable(priceMatrix, producers, consumers);

        TransportTaskSolver solver = new DiffRentSolver(table);
        solver.solve();
    }
}
