package com.example.germanapp.model;

import java.io.Serializable;

public class WordPairTracking implements Serializable {
    private final WordPair wordPair;
    private boolean isEnglishShown = true;
    private int successTracker = 0;
    private PriorityLevel priorityLevelOverride = null;

    public WordPairTracking(WordPair wordPair) {
        this.wordPair = wordPair;
    }

    public void toggleLanguage() {
        this.isEnglishShown = !this.isEnglishShown;
    }

    public boolean isEnglishShown() {
        return isEnglishShown;
    }

    public int getSuccessTracker() {
        return successTracker;
    }

    public void updateTracking(boolean isSuccess) {
        if(isSuccess){
            successTracker = Math.max(successTracker, 0);
            successTracker++;
        }else{
            successTracker--;
        }
    }

    public void setPriorityLevel(PriorityLevel priorityLevel) {
        this.priorityLevelOverride = priorityLevel;
    }

    public PriorityLevel getPriorityLevel(){
        if(priorityLevelOverride != null){
            return priorityLevelOverride;
        }
        return wordPair.getPriorityLevel();
    }

    public WordPair getWordPair() {
        return wordPair;
    }
}
