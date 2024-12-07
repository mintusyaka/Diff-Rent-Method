package edu.solvers;

import edu.models.TransportTable;

public class DiffRentTools {
    public static void findMinimals(TransportTable table, boolean[][] maskMatrix) {
        int rows = table.getPriceMatrix().length;
        int cols = table.getPriceMatrix()[0].length;

        for(int i = 0; i < cols; i++) {
            int minJdx = 0;
            for(int j = 1; j < rows; j++) {
                if(table.getPriceMatrix()[j][i] < table.getPriceMatrix()[minJdx][i]) {
                    minJdx = j;
                }
            }
            for(int j = 0; j < rows; j++) {
                if(table.getPriceMatrix()[j][i] == table.getPriceMatrix()[minJdx][i]) {
                    maskMatrix[j][i] = true;
                }
            }
        }
    }

    public static void addMinRentElements(boolean[][] maskMatrix, boolean[][] maskMinRentElement) {
        for(int i = 0; i < maskMatrix.length; i++) {
            for(int j = 0; j < maskMatrix[i].length; j++) {
                if(maskMinRentElement[i][j]) {
                    maskMatrix[i][j] = true;
                }
            }
        }
    }

    public static void fillGoods(TransportTable table, boolean[][] maskMatrix, boolean[][] maskProceededGoods) {
        int[] goods = nextGood(maskMatrix, maskProceededGoods);
        while(goods != null) {
            table.setGoods(goods[0], goods[1], calculateGoods(table, goods));
            goods = nextGood(maskMatrix, maskProceededGoods);
        }
    }

    private static int[] findSingleColumnGood(boolean[][] maskMatrix, boolean[][] maskProceededGoods) {
        for(int i = 0; i < maskMatrix[0].length; i++) {
            int singleJdx = -1;
            for(int j = 0; j < maskMatrix.length; j++) {
                if(maskMatrix[j][i] && !maskProceededGoods[j][i])
                    if(singleJdx == -1) singleJdx = j;
                    else {
                        singleJdx = -1;
                        break;
                    }
            }
            if(singleJdx != -1) {
                maskProceededGoods[singleJdx][i] = true;
                return new int[]{singleJdx, i};
            }
        }
        return null; // single column doesn't exist
    }

    private static int[] findSingleRowGoods(boolean[][] maskMatrix, boolean[][] maskProceededGoods) {
        for(int i = 0; i < maskMatrix.length; i++) {
            int singleJdx = -1;
            for(int j = 0; j < maskMatrix[i].length; j++) {
                if(maskMatrix[i][j] && !maskProceededGoods[i][j])
                    if(singleJdx == -1) singleJdx = j;
                    else {
                        singleJdx = -1;
                        break;
                    }
            }
            if(singleJdx != -1) {
                maskProceededGoods[i][singleJdx] = true;
                return new int[]{i, singleJdx};
            }
        }
        return null; // single row doesn't exist
    }

    private static int[] nextGood(boolean[][] maskMatrix, boolean[][] maskProceededGoods) {
        int[] nextGood = findSingleColumnGood(maskMatrix, maskProceededGoods);
        if(nextGood == null) nextGood = findSingleRowGoods(maskMatrix, maskProceededGoods);
        return nextGood;
    }

    private static int calculateGoods(TransportTable table, int[] goods) {
        if(table.getProducer(goods[0]) < table.getConsumer(goods[1])) {
            int producerVal = table.getProducer(goods[0]);
            table.setProducer(goods[0], 0);
            table.setConsumer(goods[1], table.getConsumer(goods[1]) - producerVal);
            return producerVal;
        } else {
            int consumerVal = table.getConsumer(goods[1]);
            table.setConsumer(goods[1], 0);
            table.setProducer(goods[0], table.getProducer(goods[0]) - consumerVal);
            return consumerVal;
        }
    }

    public static void calculateProducersVals(TransportTable table, int[] producersSigned, boolean[][] maskMatrix,
                                              int[] tempProducers, int[] tempConsumers) {
        int[] consumersBefore = new int[table.getConsumers().length];
        fillArray(consumersBefore, table.getConsumers());

        for(int i = 0; i < table.getProducers().length; i++) {
            if(table.getProducer(i) == 0) {
                producersSigned[i] = getProducerSign(table, maskMatrix, i);
                if(producersSigned[i] == 0) {
                    producersSigned[i] = recalculateTable(table, maskMatrix, tempProducers, tempConsumers, i);
                }
                else {
                    table.setProducer(i, producersSigned[i]);
                    producersSigned[i] = 0;
                }
            }
        }

        fillArray(table.getConsumers(), consumersBefore);
    }

    private static int getProducerSign(TransportTable table, boolean[][] maskMatrix, int rowIdx) {
        int producerSign = 0;
        for(int j = 0; j < maskMatrix[rowIdx].length; j++) {
            if(maskMatrix[rowIdx][j]) {
                producerSign -= table.getConsumer(j);
                table.setConsumer(j, 0);
            }
        }
        return producerSign;
    }

    private static int recalculateTable(TransportTable table, boolean[][] maskMatrix, int[] tempProducers, int[] tempConsumers, int row) {
        int sumOfGoods = table.getGoodsSum();

        int[][] tempGoods = new int[table.getGoodsMatrix().length][table.getGoodsMatrix()[0].length];
        int[] producers = new int[table.getProducers().length];
        int[] consumers = new int[table.getConsumers().length];

        fillMatrix(tempGoods, table.getGoodsMatrix());
        fillArray(producers, table.getProducers());
        fillArray(consumers, table.getConsumers());


        fillMatrix(table.getGoodsMatrix(), new int[tempGoods.length][tempGoods[0].length]);
        fillArray(table.getProducers(), tempProducers);
        fillArray(table.getConsumers(), tempConsumers);

        table.setProducer(row, table.getProducer(row) + 1);

        boolean[][] maskProceededGoods = new boolean[maskMatrix.length][maskMatrix[0].length];
        fillGoods(table, maskMatrix, maskProceededGoods);

        int secondSumOfGoods = table.getGoodsSum();

        fillMatrix(table.getGoodsMatrix(), tempGoods);
        fillArray(table.getProducers(), producers);
        fillArray(table.getConsumers(), consumers);

        if(sumOfGoods == secondSumOfGoods) return 1;
        else return -1;
    }

    public static void clearData(TransportTable table, boolean[][] maskMatrix, boolean[][] maskProceededGoods, int[] tempProducers, int[] tempConsumers) {
        DiffRentTools.fillMatrix(table.getGoodsMatrix(), new int[table.getGoodsMatrix().length][table.getGoodsMatrix()[0].length]);
        DiffRentTools.fillMatrix(maskMatrix, new boolean[maskMatrix.length][maskMatrix[0].length]);
        DiffRentTools.fillMatrix(maskProceededGoods, new boolean[maskProceededGoods.length][maskProceededGoods[0].length]);
        DiffRentTools.fillArray(table.getProducers(), tempProducers);
        DiffRentTools.fillArray(table.getConsumers(), tempConsumers);
    }

    public static void fillMatrix(int[][] toFillMatrix, int[][] fillerMatrix) {
        for(int i = 0; i < toFillMatrix.length; i++) {
            System.arraycopy(fillerMatrix[i], 0, toFillMatrix[i], 0, toFillMatrix[i].length);
        }
    }
    public static void fillMatrix(boolean[][] toFillMatrix, boolean[][] fillerMatrix) {
        for(int i = 0; i < toFillMatrix.length; i++) {
            System.arraycopy(fillerMatrix[i], 0, toFillMatrix[i], 0, toFillMatrix[i].length);
        }
    }

    public static void fillArray(int[] toFillArr, int[] fillerArr) {
        System.arraycopy(fillerArr, 0, toFillArr, 0, toFillArr.length);
    }

    public static double findMinRentElement(TransportTable table, boolean[][] maskMatrix, boolean[][] maskMinRentElement, int[] producersSigned) {
        double minRent = Double.MAX_VALUE;
        int[] rentElement = new int[2];

        for(int i = 0; i < maskMatrix[0].length; i++) {
            if(isGoodsItemInPosRow(table, maskMatrix, producersSigned, i))
                continue;
            int[] minElementInPositiveColumn = findMinElementInColumnOfPositiveRows(table, producersSigned, i);
            int[] minElementInNegativeColumn = findMinElementInColumnOfNegativeRows(table, maskMatrix, producersSigned, i);
            if(minElementInPositiveColumn[0] != -1 && minElementInNegativeColumn[0] != -1) {
                if(minRent > Math.abs(table.getPrice(minElementInPositiveColumn[0], i) - table.getPrice(minElementInNegativeColumn[0], i))) {
                    minRent = Math.abs(table.getPrice(minElementInPositiveColumn[0], i) - table.getPrice(minElementInNegativeColumn[0], i));
                    rentElement[0] = minElementInPositiveColumn[0];
                    rentElement[1] = i;
                }
            }
        }

        maskMinRentElement[rentElement[0]][rentElement[1]] = true;

        return minRent;
    }

    private static boolean isGoodsItemInPosRow(TransportTable table, boolean[][] maskMatrix, int[] producersSigned, int colIdx) {
        for(int i = 0; i < producersSigned.length; i++) {
            if(producersSigned[i] > 0 || table.getProducer(i) > 0) {
                if(maskMatrix[i][colIdx]) return true;
            }
        }
        return false;
    }

    private static int[] findMinElementInColumnOfPositiveRows(TransportTable table, int[] producersSigned, int colIdx) {
        int[] minElement = new int[2];
        minElement[0] = -1;
        minElement[1] = colIdx;

        for(int i = 0; i < producersSigned.length; i++) {
            if(producersSigned[i] > 0 || table.getProducer(i) > 0) {
                if(minElement[0] == -1) {
                    minElement[0] = i;
                }
                if(table.getPrice(i, colIdx) < table.getPrice(minElement[0], colIdx)) {
                    minElement[0] = i;
                }
            }
        }

        return minElement;
    }

    private static int[] findMinElementInColumnOfNegativeRows(TransportTable table, boolean[][] maskMatrix, int[] producersSigned, int colIdx) {
        int[] minElement = new int[2];
        minElement[0] = -1;
        minElement[1] = colIdx;

        for(int i = 0; i < producersSigned.length; i++) {
            if((producersSigned[i] < 0 || table.getProducer(i) < 0) && maskMatrix[i][colIdx]) {
                if(minElement[0] == -1) {
                    minElement[0] = i;
                }
                if(table.getPrice(i, colIdx) < table.getPrice(minElement[0], colIdx)) {
                    minElement[0] = i;
                }
            }
        }

        return minElement;
    }

    public static void addRentToPrices(TransportTable table, double rent, int[] producersSigned) {
        for(int i = 0; i < table.getPriceMatrix().length; i++) {
            for(int j = 0; j < table.getPriceMatrix()[i].length; j++) {
                if(producersSigned[i] > 0 || table.getProducer(i) > 0) {
                    table.setPrice(i, j, table.getPrice(i, j) + rent);
                }
            }
        }
    }
}
