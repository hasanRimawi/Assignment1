package com.example.learnenglish.model;

import java.util.Arrays;

public class Root {
    public String word;
    public String phonetic;
    public Phonetic[] phonetics;
    public String origin;
    public Meaning[] meanings;
    public License license;
    public String[] sourceUrls;

    @Override
    public String toString() {
        return "Root{" +
                "word='" + word + '\'' +
                ", phonetics=" + Arrays.toString(phonetics) +
                ", meanings=" + Arrays.toString(meanings) +
                ", license=" + license +
                ", sourceUrls=" + Arrays.toString(sourceUrls) +
                '}';
    }
}
