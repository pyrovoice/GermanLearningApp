package com.example.germanapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserData implements Serializable {
    private ArrayList<WordPairTracking> userwordPool = new ArrayList<>();
    private ArrayList<UserWordPair> userCreatedWords = new ArrayList<>();

    public ArrayList<WordPairTracking> getUserwordPool() {
        return userwordPool;
    }

    public void setUserwordPool(ArrayList<WordPairTracking> userwordPool) {
        this.userwordPool = userwordPool;
    }

    public ArrayList<UserWordPair> getUserCreatedWords() {
        return userCreatedWords;
    }

    public void setUserCreatedWords(ArrayList<UserWordPair> userCreatedWords) {
        this.userCreatedWords = userCreatedWords;
    }
}
