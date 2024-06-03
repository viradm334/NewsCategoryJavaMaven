package com.newscategory.nlp;

import java.util.ArrayList;
import java.util.List;

public class DataSplitter {
    public static TrainTestData splitData(List<String[]> data, double testSize, long randomSeed) {
        List<String> xList = new ArrayList<>();
        List<String> yList = new ArrayList<>();

        // Extract text and category from the dataset
        for (String[] row : data) {
            xList.add(row[1]); // Text is in the second column
            yList.add(row[2]); // Category is in the third column
        }

        // Convert lists to arrays
        String[] x = xList.toArray(new String[0]);
        String[] y = yList.toArray(new String[0]);

        // Print shapes (optional)
        System.out.println("X.shape = " + x.length);
        System.out.println("y.shape = " + y.length);

        // Train-test split
        double testRatio = testSize;
        int trainSize = (int) Math.ceil(x.length * (1 - testRatio));
        String[] xTrain = new String[trainSize];
        String[] xTest = new String[x.length - trainSize];
        String[] yTrain = new String[trainSize];
        String[] yTest = new String[x.length - trainSize];

        // Populate train and test datasets
        System.arraycopy(x, 0, xTrain, 0, trainSize);
        System.arraycopy(x, trainSize, xTest, 0, x.length - trainSize);
        System.arraycopy(y, 0, yTrain, 0, trainSize);
        System.arraycopy(y, trainSize, yTest, 0, x.length - trainSize);

        // Print sizes (optional)
        System.out.println("x_train size = " + xTrain.length);
        System.out.println("x_test size = " + xTest.length);

        // Return train-test data
        return new TrainTestData(xTrain, xTest, yTrain, yTest);
    }

    public static class TrainTestData {
        String[] xTrain;
        String[] xTest;
        String[] yTrain;
        String[] yTest;

        public TrainTestData(String[] xTrain, String[] xTest, String[] yTrain, String[] yTest) {
            this.xTrain = xTrain;
            this.xTest = xTest;
            this.yTrain = yTrain;
            this.yTest = yTest;
        }
    }
}
