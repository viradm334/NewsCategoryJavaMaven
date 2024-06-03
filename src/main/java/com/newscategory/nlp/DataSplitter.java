package com.newscategory.nlp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class DataSplitter {
    public static DataSplitResult splitData(int[][] x, int[] y, double testSize, int randomState) {
        int totalSize = x.length;
        int testSizeCount = (int) (totalSize * testSize);

        List<Integer> indices = new ArrayList<>();
        for (int i = 0; i < totalSize; i++) {
            indices.add(i);
        }

        Collections.shuffle(indices, new Random(randomState));

        int[][] xTrain = new int[totalSize - testSizeCount][x[0].length]; // Ensure correct size
        int[][] xTest = new int[testSizeCount][x[0].length]; // Ensure correct size
        int[] yTrain = new int[totalSize - testSizeCount];
        int[] yTest = new int[testSizeCount];

        for (int i = 0; i < totalSize - testSizeCount; i++) {
            xTrain[i] = x[indices.get(i)];
            yTrain[i] = y[indices.get(i)];
        }
        for (int i = 0; i < testSizeCount; i++) {
            xTest[i] = x[indices.get(totalSize - testSizeCount + i)];
            yTest[i] = y[indices.get(totalSize - testSizeCount + i)];
        }

        return new DataSplitResult(xTrain, xTest, yTrain, yTest);
    }

    static class DataSplitResult {
        int[][] xTrain;
        int[][] xTest;
        int[] yTrain;
        int[] yTest;

        DataSplitResult(int[][] xTrain, int[][] xTest, int[] yTrain, int[] yTest) {
            this.xTrain = xTrain;
            this.xTest = xTest;
            this.yTrain = yTrain;
            this.yTest = yTest;
        }
    }
}
