package com.example.germanapp.model;

import java.io.Serializable;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        WordPair wordPair = (WordPair) o;
        return Objects.equals(englishWord, wordPair.englishWord) && Objects.equals(englishArticle, wordPair.englishArticle) && Objects.equals(germanWord, wordPair.germanWord) && Objects.equals(germanArticle, wordPair.germanArticle) && priorityLevel == wordPair.priorityLevel;
    }

    @Override
    public int hashCode() {
        return Objects.hash(englishWord, englishArticle, germanWord, germanArticle, priorityLevel);
    }
}
