package com.example.learnenglish.model;

import java.util.Arrays;

public class Meaning {
    public String partOfSpeech;
    public Definition[] definitions;
    public String[] synonyms;
    public String[] antonyms;

    @Override
    public String toString() {
        return "Meaning{" +
                "partOfSpeech='" + partOfSpeech + '\'' +
                ", definitions=" + Arrays.toString(definitions) +
                ", synonyms=" + Arrays.toString(synonyms) +
                ", antonyms=" + Arrays.toString(antonyms) +
                '}';
    }
}
