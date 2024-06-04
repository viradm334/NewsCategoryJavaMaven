package com.newscategory.nlp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.opencsv.CSVReader;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import opennlp.tools.tokenize.SimpleTokenizer;

public class NewsCategory {
    public static void main(String[] args) throws Exception {
        List<String[]> data = new ArrayList<>();

        // Initialize StanfordCoreNLP
        StanfordCoreNLP stanfordCoreNLP = Pipeline.getPipeline();

        try (CSVReader csvReader = new CSVReader(new FileReader("src/main/resources/BBC News Train.csv"))) {
            String[] lines;
            boolean isFirstRow = true;

            while ((lines = csvReader.readNext()) != null) {
                // Skip header row
                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                // Process each field in the row
                if (lines.length > 1) {
                    // Existing preprocessing
                    lines[1] = DataPreprocessor.removeTags(lines[1]);
                    lines[1] = DataPreprocessor.removeSpecialCharacters(lines[1]);
                    lines[1] = DataPreprocessor.lowerCase(lines[1]);
                    lines[1] = DataPreprocessor.removeStopwords(lines[1]);
                    lines[1] = DataPreprocessor.removeNewLines(lines[1]);

                    // Tokenization and lemmatization
                    lines[1] = DataPreprocessor.lemmatizeText(stanfordCoreNLP, lines[1]);
                }
                data.add(lines); // Add each processed row to the ArrayList
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Print the dataset after text preprocessing
        printDataset(data);

        // Convert data to required format
        String[] texts = new String[data.size()];
        int[] categoryIds = new int[data.size()];

        for (int i = 0; i < data.size(); i++) {
            texts[i] = data.get(i)[1];
            categoryIds[i] = getCategoryId(data.get(i)[2]);
        }

        // Transform text to features
        int maxFeatures = 5000;
        int[][] x = transformTextToFeatures(texts, maxFeatures);

        System.out.println("X.shape = " + x.length + " x " + x[0].length);
        System.out.println("y.shape = " + categoryIds.length);

        // Split the dataset into training and test sets
        DataSplitter.DataSplitResult splitData = DataSplitter.splitData(x, categoryIds, 0.3, 0);

        // Obtain train-test data
        int[][] xTrain = splitData.xTrain;
        int[][] xTest = splitData.xTest;

        // Prepare the training and testing data in the required format for DecisionTreeTrainer
        ArrayList<String[]> trainData = prepareData(xTrain, splitData.yTrain);
        ArrayList<String[]> testData = prepareData(xTest, splitData.yTest);

        // Define the classes and attributes (using the integer representation)
        String class1 = "0";
        String class2 = "1";

        // Count the number of instances of each class
        int noOfClass1 = countClassInstances(trainData, class1);
        int noOfClass2 = countClassInstances(trainData, class2);

        // Ensure there are instances for both classes
        if (noOfClass1 == 0 || noOfClass2 == 0) {
            throw new IllegalArgumentException("One of the classes has zero instances. Check your data preparation.");
        }

        // Prepare the discrete values and remaining attributes
        HashMap<Integer, ArrayList<String>> discreteValues = computeDiscreteValues(trainData);
        ArrayList<Integer> remainingAttributes = getRemainingAttributes(trainData);

        // Train the decision tree
        DecisionTreeTrainer decisionTree = new DecisionTreeTrainer(trainData, testData, noOfClass1, noOfClass2, class1, class2, discreteValues, remainingAttributes);

        // Print analysis
        decisionTree.printTrainingTime();
        decisionTree.printAnalysis();
    }

    private static void printDataset(List<String[]> data) {
        // Print header
        System.out.println("ArticleId\tText\tCategory\tCategoryId");

        // Print the rows of the dataset
        for (String[] row : data) {
            String articleId = row[0];
            String text = row[1];
            String category = row[2];
            int categoryId = getCategoryId(category);

            System.out.println(articleId + "\t" + text + "\t" + category + "\t" + categoryId);
        }

        System.out.println(data.size() + " rows × 4 columns");
    }

    private static int getCategoryId(String category) {
        switch (category.toLowerCase()) {
            case "business":
                return 0;
            case "tech":
                return 1;
            case "politics":
                return 2;
            case "sport":
                return 3;
            case "entertainment":
                return 4;
            default:
                return -1;
        }
    }

    private static int[][] transformTextToFeatures(String[] texts, int maxFeatures) {
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        List<int[]> featuresList = new ArrayList<>();

        for (String text : texts) {
            String[] tokens = tokenizer.tokenize(text);
            int[] features = new int[maxFeatures]; // Fixed size array

            for (int i = 0; i < Math.min(tokens.length, maxFeatures); i++) {
                features[i] = tokens[i].hashCode(); // Simple feature extraction using hash codes
            }
            featuresList.add(features);
        }

        return featuresList.toArray(new int[featuresList.size()][]);
    }

    private static ArrayList<String[]> prepareData(int[][] x, int[] y) {
        ArrayList<String[]> data = new ArrayList<>();
        for (int i = 0; i < x.length; i++) {
            String[] row = new String[x[i].length + 1];
            for (int j = 0; j < x[i].length; j++) {
                row[j] = String.valueOf(x[i][j]);
            }
            row[x[i].length] = String.valueOf(y[i]);
            data.add(row);
        }
        return data;
    }

    private static int countClassInstances(ArrayList<String[]> data, String className) {
        int count = 0;
        for (String[] row : data) {
            if (row[row.length - 1].equals(className)) {
                count++;
            }
        }
        return count;
    }

    private static HashMap<Integer, ArrayList<String>> computeDiscreteValues(ArrayList<String[]> data) {
        HashMap<Integer, ArrayList<String>> discreteValues = new HashMap<>();
        for (int i = 0; i < data.get(0).length - 1; i++) {
            ArrayList<String> values = new ArrayList<>();
            for (String[] row : data) {
                if (!values.contains(row[i])) {
                    values.add(row[i]);
                }
            }
            discreteValues.put(i, values);
        }
        return discreteValues;
    }

    private static ArrayList<Integer> getRemainingAttributes(ArrayList<String[]> data) {
        ArrayList<Integer> remainingAttributes = new ArrayList<>();
        for (int i = 0; i < data.get(0).length - 1; i++) {
            remainingAttributes.add(i);
        }
        return remainingAttributes;
    }
}
