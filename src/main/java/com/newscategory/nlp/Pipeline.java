package com.newscategory.nlp;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;

import java.util.Properties;

public class Pipeline {
    private static StanfordCoreNLP stanfordCoreNLP;
    private static Properties properties;

    private Pipeline() {
    }

    static {
        properties = new Properties();
        properties.setProperty("annotators", "tokenize,ssplit,pos,lemma");
    }

    public static StanfordCoreNLP getPipeline() {
        if (stanfordCoreNLP == null) {
            stanfordCoreNLP = new StanfordCoreNLP(properties);
        }
        return stanfordCoreNLP;
    }
}
