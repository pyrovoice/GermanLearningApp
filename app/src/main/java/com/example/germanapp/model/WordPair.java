package com.example.germanapp.model;

import java.io.Serializable;

public class WordPair implements Serializable {
    private final String englishWord;
    private final String englishArticle;
    private final String germanWord;
    private final String germanArticle;
    private final PriorityLevel priorityLevel;

    public WordPair(String englishArticle, String englishWord, String germanArticle, String germanWord, PriorityLevel priorityLevel) {
        this.englishWord = englishWord;
        this.englishArticle = englishArticle;
        this.germanWord = germanWord;
        this.germanArticle = germanArticle;
        this.priorityLevel = priorityLevel;
    }
    public String getGermanWord() {
        return germanWord;
    }

    public String getEnglishWord() {
        return englishWord;
    }

    public PriorityLevel getPriorityLevel() {
        return priorityLevel;
    }

    public String getGermanArticle() {
        return germanArticle;
    }

    public String getEnglishArticle() {
        return englishArticle;
    }
}
