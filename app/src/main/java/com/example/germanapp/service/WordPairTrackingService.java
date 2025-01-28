package com.example.germanapp.service;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.germanapp.App;
import com.example.germanapp.model.PriorityLevel;
import com.example.germanapp.model.WordPair;
import com.example.germanapp.model.WordPairTracking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WordPairTrackingService {
    final String USER_FILE_NAME = "user_file.txt";
    ArrayList<WordPairTracking> currentWordPool = null;
    private int wordPoolTracker = 0;
    private static WordPairTrackingService instance = null;
    private WordPairTrackingService(){
        try {
            currentWordPool = loadUserData();
        } catch (Exception e) {
            Log.println(Log.ERROR, null, e.getMessage());
        }
        if(currentWordPool == null){
            List<WordPair> words = Arrays.asList(
                    new WordPair("machen", "to do", PriorityLevel.HIGHEST),
                    new WordPair("anbieten", "to offer", PriorityLevel.MEDIUM),
                    new WordPair("sein", "to be", PriorityLevel.HIGHEST),
                    new WordPair("führen", "to lead", PriorityLevel.HIGH),
                    new WordPair("Detektiv", "detective", PriorityLevel.LOWEST),
                    new WordPair("Stein", "a rock", PriorityLevel.HIGHEST)
            );
            currentWordPool =  words.stream()
                    .sorted(Comparator.comparing(w -> w.getPriorityLevel().value))
                    .map(WordPairTracking::new).collect(Collectors.toCollection(ArrayList::new));
        }
    }
    public static WordPairTrackingService getInstance(){
        if (instance == null){
            instance = new WordPairTrackingService();
        }
        return instance;
    }

    public Optional<WordPairTracking> getNextWord(){
        if(wordPoolTracker >= currentWordPool.size()){
            updatePool();
            wordPoolTracker = 0;
        }
        if(currentWordPool.isEmpty()){
            return Optional.empty();
        }
        return Optional.of(currentWordPool.get(wordPoolTracker++));
    }

    private void updatePool(){
        currentWordPool = currentWordPool.stream().filter(wordPairTracking -> wordPairTracking.getSuccesses() < 2)
                .collect(Collectors.toCollection(ArrayList::new));
        Collections.shuffle(currentWordPool);
    }
    private ArrayList<WordPairTracking> loadUserData() {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            File file = getSaveFilePath();
            if (!file.exists()){
                createSaveFile(file);
                return null;
            }
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            Object loadedObject = ois.readObject();
            if(loadedObject instanceof ArrayList && !((ArrayList<?>) loadedObject).isEmpty() && ((ArrayList<?>) loadedObject).get(0) instanceof WordPairTracking){
                Log.println(Log.DEBUG, null, "List loaded");
                return (ArrayList<WordPairTracking>) loadedObject;
            }
        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, null, "Error loading data (FNF): "+e.getMessage());
        } catch (ClassNotFoundException e) {
            Log.println(Log.ERROR, null, "Error loading data (CNF): "+e.getMessage());
        } catch (IOException e) {
            Log.println(Log.ERROR, null, "Error loading data (IO): "+e.getMessage());
        }finally {
            try {
                if(fis != null){
                    fis.close();
                }

                if(ois != null){
                    ois.close();
                }
            } catch (IOException e) {
                Log.println(Log.ERROR, null, "Error closing streams: "+e.getMessage());
            }
        }
        return null;
    }

    private void saveUserData() {
        FileOutputStream outputStream = null;
        ObjectOutputStream oos = null;
        try {
            File file = getSaveFilePath();
            if(!file.exists()){
                createSaveFile(file);
                if(!file.exists()){
                    Log.println(Log.ERROR, null, "Cannot save: No file");
                    return;
                }
            }
            outputStream = new FileOutputStream(file);
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(currentWordPool);

        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, null, "File read issue: " + e.getMessage());
        } catch (IOException e) {
            Log.println(Log.ERROR, null, "File create issue: " + e.getMessage());
        } finally{
            if(outputStream != null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.println(Log.ERROR, null, "Error closing stream output");
                }
            }
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.println(Log.ERROR, null, "Error closing stream oos");
                }
            }
        }
    }

    public void updateWordPairIncrement(WordPairTracking wordPair, boolean isSuccess){
        if(isSuccess){
            wordPair.incrementSuccesses();
        }else{
            wordPair.incrementMistakes();
        }
        saveUserData();
    }

    private File getSaveFilePath() throws IOException {
        File dirPath = App.getContext().getExternalFilesDir(null);
        return new File(dirPath+"/"+USER_FILE_NAME);
    }

    private void createSaveFile(File file) throws IOException {
        try {
            boolean fileCreation = file.createNewFile();
            Log.println(Log.DEBUG, null, "Save file Creation: " + fileCreation);
        } catch (IOException e) {
            throw new IOException();
        }
    }
}
