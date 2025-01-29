package com.example.germanapp.model;

import java.io.Serializable;

public class WordPairTracking implements Serializable {
    final WordPair wordPair;
    private boolean isEnglishShown = true;
    private int successTracker = 0;
    private int nbrTries = 0;

    public WordPairTracking(WordPair wordPair) {
        this.wordPair = wordPair;
    }

    public void toggleLanguage() {
        this.isEnglishShown = !this.isEnglishShown;
    }

    public boolean isEnglishShown() {
        return isEnglishShown;
    }

    public WordPair getWordPair() {
        return wordPair;
    }

    public int getSuccessTracker() {
        return successTracker;
    }

    public int getNbrTries() {
        return nbrTries;
    }

    public void updateTracking(boolean isSuccess) {
        this.nbrTries++;
        if(isSuccess && successTracker < 2){
            successTracker++;
        }else if (!isSuccess && successTracker > -1){
            successTracker--;
        }
    }
}
