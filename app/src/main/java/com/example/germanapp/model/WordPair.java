package com.example.germanapp.model;

import java.io.Serializable;

public class WordPair implements Serializable {
    private final String germanWord;
    private final String englishWord;
    private final PriorityLevel priorityLevel;

    public WordPair(String germanWord, String englishWord, PriorityLevel priorityLevel) {
        this.germanWord = germanWord;
        this.englishWord = englishWord;
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
}
