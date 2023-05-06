package com.example.learnenglish.model;

import java.util.Arrays;

public class Definition {
    public String definition;
    public String[] synonyms;
    public String[] antonyms;
    public String example;

    @Override
    public String toString() {
        return "Definition{" +
                "definition='" + definition + '\'' +
                ", synonyms=" + Arrays.toString(synonyms) +
                ", antonyms=" + Arrays.toString(antonyms) +
                ", example='" + example + '\'' +
                '}';
    }
}
