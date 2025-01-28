package com.example.germanapp.model;

import java.io.Serializable;
import java.util.ArrayList;

public class UserData implements Serializable {
    private ArrayList<WordPairTracking> userwordPool = new ArrayList<>();

    public ArrayList<WordPairTracking> getUserwordPool() {
        return userwordPool;
    }

    public void setUserwordPool(ArrayList<WordPairTracking> userwordPool) {
        this.userwordPool = userwordPool;
    }
}
