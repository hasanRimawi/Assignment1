package com.example.learnenglish.model;

public class   Phonetic {
    public String audio;
    public String sourceUrl;
    public License license;
    public String text;

    @Override
    public String toString() {
        return "Phonetic{" +
                "audio='" + audio + '\'' +
                ", sourceUrl='" + sourceUrl + '\'' +
                ", license=" + license +
                ", text='" + text + '\'' +
                '}';
    }
}
