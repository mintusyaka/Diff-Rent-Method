package edu.solvers;

import edu.models.TransportTable;

public class DiffRentSolver implements  TransportTaskSolver {
    private TransportTable table;
    private boolean[][] maskMatrix;
    private boolean[][] maskMinRentElements;
    private boolean[][] maskProceededGoods;

    private final int[] tempProducers;
    private final int[] tempConsumers;

    private int[] producersSigned;

    public DiffRentSolver(TransportTable table) {
        this.table = table;
        maskMatrix = new boolean[table.getPriceMatrix().length][table.getPriceMatrix()[0].length];
        maskMinRentElements = new boolean[table.getPriceMatrix().length][table.getPriceMatrix()[0].length];
        maskProceededGoods = new boolean[table.getPriceMatrix().length][table.getPriceMatrix()[0].length];
        tempProducers = table.getProducers().clone();
        tempConsumers = table.getConsumers().clone();
        producersSigned = new int[table.getProducers().length];
    }

    @Override
    public TransportTable solve() {
        while(true) {
            DiffRentTools.findMinimals(table, maskMatrix);
            DiffRentTools.addMinRentElements(maskMatrix, maskMinRentElements);
            DiffRentTools.fillGoods(table, maskMatrix, maskProceededGoods);

            if (isSolved()) break;
            else {
                DiffRentTools.calculateProducersVals(table, producersSigned, maskMatrix, tempProducers, tempConsumers);

                double minRent = DiffRentTools.findMinRentElement(table, maskMatrix, maskMinRentElements, producersSigned);
                DiffRentSolverTools.print(table, maskMatrix, producersSigned);
                System.out.println("Min Rent = " + minRent);
                System.out.println();

                if (minRent == Double.MAX_VALUE) return null;
                else if (minRent > 0) {
                    DiffRentTools.addRentToPrices(table, minRent, producersSigned);
                }

                DiffRentTools.clearData(table, maskMatrix, maskProceededGoods, tempProducers, tempConsumers);
            }
        }

        DiffRentSolverTools.print(table, maskMatrix, producersSigned);

        return table;
    }

    private boolean isSolved() {
        return DiffRentSolverTools.isAllowable(table);
    }

    private static class DiffRentSolverTools {
        private static boolean isAllowable(TransportTable table) {
            for(int i = 0; i < table.getConsumers().length; i++) {
                if(table.getConsumer(i) != 0)
                    return false;
            }

            for(int i = 0; i < table.getProducers().length; i++) {
                if(table.getProducer(i) != 0)
                    return false;
            }

            return true;
        }

        public static void print(TransportTable table, boolean[][] maskMatrix, int[] producersSigned) {
            for(int i = 0; i < table.getPriceMatrix().length; i++) {
                for(int j = 0; j < table.getPriceMatrix()[i].length; j++) {
                    System.out.print(table.getPriceMatrix()[i][j]);
                    if(maskMatrix[i][j]) {
                        System.out.print("/" + table.getGoodsMatrix()[i][j]);
                        System.out.print("\t");
                    }
                    else
                        System.out.print("\t\t");
                }
                if(table.getProducer(i) == 0) {
                    if(producersSigned[i] < 0)
                        System.out.print("-0");
                    else
                        System.out.print("+0");
                }
                else {
                    if(table.getProducer(i) > 0)
                        System.out.print("+" + table.getProducer(i));
                    else
                        System.out.print(table.getProducer(i));

                }
                System.out.println();

            }
            for(int i = 0; i < table.getConsumers().length; i++) {
                System.out.print(table.getConsumer(i) + "\t\t");
            }
            System.out.println();
        }
    }
}
