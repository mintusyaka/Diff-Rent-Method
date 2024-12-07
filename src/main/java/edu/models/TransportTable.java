package edu.models;

public class TransportTable {
    private double[][] priceMatrix;
    private int[][] goodsMatrix;
    private int[] producers;
    private int[] consumers;

    public TransportTable() {
        priceMatrix = null;
        goodsMatrix = null;
        producers = null;
        consumers = null;
    }

    public TransportTable(double[][] priceMatrix, int[] producers, int[] consumers) throws RuntimeException {
        if( priceMatrix.length != consumers.length || priceMatrix[0].length != producers.length)
        {
            throw new RuntimeException("Wrong parameters length.");
        }
        this.priceMatrix = priceMatrix;
        this.producers = producers;
        this.consumers = consumers;
        TransportTableTools.makeGoodsMatrixEmpty(this);
    }



    //PRICES
    public double getPrice(int i, int j) throws RuntimeException {
        if(i >= 0 && i < priceMatrix.length && j >= 0 && j < priceMatrix[i].length)
            return priceMatrix[i][j];
        else
            throw new RuntimeException("Wrong index to select price.");
    }

    public void setPrice(int i, int j, double value) throws RuntimeException {
        if(i >= 0 && i < priceMatrix.length && j >= 0 && j < priceMatrix[i].length)
            priceMatrix[i][j] = value;
        else
            throw new RuntimeException("Wrong index to select price.");
    }

    public double[][] getPriceMatrix() {
        return priceMatrix;
    }



    //GOODS
    public int getGoods(int i, int j) throws RuntimeException {
        if(i >= 0 && i < goodsMatrix.length && j >= 0 && j < goodsMatrix[i].length)
            return goodsMatrix[i][j];
        else
            throw new RuntimeException("Wrong index to select goods.");
    }

    public void setGoods(int i, int j, int value) throws RuntimeException {
        if(i >= 0 && i < goodsMatrix.length && j >= 0 && j < goodsMatrix[i].length)
            goodsMatrix[i][j] = value;
        else
            throw new RuntimeException("Wrong index to select goods.");
    }

    public int[][] getGoodsMatrix() {
        return goodsMatrix;
    }



    //CONSUMERS
    public int getConsumer(int i) throws RuntimeException {
        if(i >= 0 && i < consumers.length)
            return consumers[i];
        else
            throw new RuntimeException("Wrong index to select consumer.");
    }

    public void setConsumer(int i, int value) throws RuntimeException {
        if(i >= 0 && i < consumers.length)
            consumers[i] = value;
        else
            throw new RuntimeException("Wrong index to select consumer.");
    }

    public int[] getConsumers() {
        return consumers;
    }



    //PRODUCERS
    public int getProducer(int i) throws RuntimeException {
        if(i >= 0 && i < producers.length)
            return producers[i];
        else
            throw new RuntimeException("Wrong index to select producer.");
    }

    public void setProducer(int i, int value) throws RuntimeException {
        if(i >= 0 && i < producers.length)
            producers[i] = value;
        else
            throw new RuntimeException("Wrong index to select producer.");
    }

    public int[] getProducers() {
        return producers;
    }

    private static class TransportTableTools {
        private static void makeGoodsMatrixEmpty(TransportTable table) {
            table.goodsMatrix = new int[table.priceMatrix.length][table.priceMatrix[0].length];

            for (int i = 0; i < table.priceMatrix.length; i++) {
                for (int j = 0; j < table.priceMatrix[i].length; j++) {
                    table.goodsMatrix[i][j] = 0;
                }
            }
        }
    }



    // PRINT
    public void printGoods() {
        for (int[] matrix : goodsMatrix) {
            for (int i : matrix) System.out.print(i + " ");
            System.out.println();
        }
    }

}
