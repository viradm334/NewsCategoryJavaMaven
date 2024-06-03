package com.newscategory.nlp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

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
        } 
         catch (IOException e) {
            e.printStackTrace();
        }

        if (data.size() > 1) {
            String[] secondRow = data.get(1);
            System.out.println(secondRow[1]);
        }
        // Print the dataset after text preprocessing
        printDataset(data);

        // Split the dataset into training and test sets
        DataSplitter.TrainTestData splitData = DataSplitter.splitData(data, 0.3, 0);

        // Obtain train-test data
        String[] xTrain = splitData.xTrain;
        String[] xTest = splitData.xTest;

        // Print sizes of training and test sets (optional)
        System.out.println("Train : " + xTrain.length);
        System.out.println("Test : " + xTest.length);
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
}
