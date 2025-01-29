package com.example.germanapp.service;

import android.content.res.AssetManager;

import com.example.germanapp.App;
import com.example.germanapp.model.PriorityLevel;
import com.example.germanapp.model.WordPair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class WordLoaderService {
    private List<WordPair> systemWordPairs = new ArrayList<>();
    final String NUMBER_REGEX = "[+-]?\\d+";

    private static WordLoaderService instance = null;
    private WordLoaderService(){
        loadSystemWords();
    }

    public static WordLoaderService getInstance(){
        if(instance == null){
            instance = new WordLoaderService();
        }
        return instance;
    }

    public List<WordPair> getSystemWordPairs(){
        return this.systemWordPairs;
    }

    private void loadSystemWords(){
        try {
            AssetManager assets = App.getContext().getAssets();
            InputStream assetStream = assets.open("EnglishGermanPairs.txt");
            systemWordPairs = parseTextToList(assetStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<WordPair> parseTextToList(InputStream assetStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(assetStream));
        String line;
        List<WordPair> list = new ArrayList<>();
        PriorityLevel[] priorityEnum = PriorityLevel.values();
        while ((line = reader.readLine()) != null){
            String[] sp = line.split(";");
            if(sp.length != 5){
                continue;
            }
            PriorityLevel prio = getPriorityLevel(sp[4], priorityEnum);
            list.add(new WordPair(sp[0], sp[1], sp[2], sp[3], prio));
        }
        return list;
    }

    private PriorityLevel getPriorityLevel(String sp, PriorityLevel[] priorityEnum) {
        PriorityLevel prio = PriorityLevel.MEDIUM;
        if(sp != null && !sp.isEmpty() && sp.matches(NUMBER_REGEX)){
            int val = Integer.parseInt(sp);
            if(val >= 0 && val <=4){
                prio = priorityEnum[val];
            }
        }
        return prio;
    }
}
