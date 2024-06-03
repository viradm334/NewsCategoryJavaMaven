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
    }
}
