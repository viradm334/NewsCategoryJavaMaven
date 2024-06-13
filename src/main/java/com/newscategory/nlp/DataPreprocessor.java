package com.newscategory.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

import java.util.List;
import java.util.StringJoiner;

public class DataPreprocessor {
    public static String removeTags(String text) {
        return text.replaceAll("<[^>]*>", "");
    }

    public static String removeSpecialCharacters(String text) {
        return text.replaceAll("[^a-zA-Z0-9\\s]", "");
    }

    public static String lowerCase(String text) {
        return text.toLowerCase();
    }

    public static String removeStopwords(String text) {
        // Implement your stopwords removal logic here
        return text;
    }

    public static String removeNewLines(String text) {
        return text.replace("\n", " ").replace("\r", " ");
    }

    public static String lemmatizeText(StanfordCoreNLP stanfordCoreNLP, String text) {
        Annotation annotation = new Annotation(text);
        stanfordCoreNLP.annotate(annotation);
        List<CoreLabel> tokens = annotation.get(CoreAnnotations.TokensAnnotation.class);
        StringJoiner lemmatizedText = new StringJoiner(" ");
        for (CoreLabel token : tokens) {
            lemmatizedText.add(token.get(CoreAnnotations.LemmaAnnotation.class));
        }
        return lemmatizedText.toString();
    }
}
