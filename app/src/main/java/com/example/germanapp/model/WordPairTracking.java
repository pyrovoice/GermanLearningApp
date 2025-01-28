package com.example.germanapp.model;

import java.io.Serializable;

public class WordPairTracking implements Serializable {
    final WordPair wordPair;
    private boolean isEnglishShown = true;
    private int successes = 0;
    private int mistakes = 0;

    public WordPairTracking(WordPair wordPair) {
        this.wordPair = wordPair;

    }
    public void toggleLanguage(){
        this.isEnglishShown = !this.isEnglishShown;
    }

    public void incrementSuccesses(){
        successes++;
    }
    public void incrementMistakes(){
        mistakes++;
    }

    public int getSuccesses() {
        return successes;
    }

    public int getMistakes() {
        return mistakes;
    }

    public boolean isEnglishShown() {
        return isEnglishShown;
    }

    public WordPair getWordPair() {
        return wordPair;
    }
}
