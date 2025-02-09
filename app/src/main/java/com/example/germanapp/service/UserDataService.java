package com.example.germanapp.service;

import android.util.Log;

import com.example.germanapp.App;
import com.example.germanapp.model.UserData;
import com.example.germanapp.model.UserWordPair;
import com.example.germanapp.model.WordPair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Optional;
import java.util.UUID;

public class UserDataService {
    private static UserDataService instance = null;
    final String USER_FILE_NAME = "user_file.txt";
    private UserData userData = null;
    private UserDataService() {
        try {
            File file = getSaveFilePath();
            if (!file.exists()) {
                createSaveFile(file);
                userData = new UserData();
            } else {
                loadUserData(file);
            }
        } catch (IOException | ClassNotFoundException e) {
            Log.println(Log.ERROR, null, "Error reading the save file: " + e.getMessage());
            userData = new UserData();
        }
    }

    public static UserDataService getInstance() {
        if (instance == null) {
            instance = new UserDataService();
        }
        return instance;
    }

    public Optional<UserData> getUserData() {
        return Optional.ofNullable(userData);
    }

    private void loadUserData(File file) throws IOException, ClassNotFoundException {
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            Object loadedObject = ois.readObject();
            if (loadedObject instanceof UserData) {
                Log.println(Log.DEBUG, null, "User data loaded");
                userData = (UserData) loadedObject;
            }
        } catch (ClassNotFoundException | IOException e) {
            try {
                if (fis != null) {
                    fis.close();
                }

                if (ois != null) {
                    ois.close();
                }
            } catch (IOException ioe) {
                Log.println(Log.ERROR, null, "Error closing streams: " + ioe.getMessage());
            }
            throw e;
        }
        try {
            fis.close();
            ois.close();
        } catch (IOException e) {
            Log.println(Log.ERROR, null, "Error closing streams: " + e.getMessage());
        }
    }

    public boolean saveUserData() {
        FileOutputStream outputStream = null;
        ObjectOutputStream oos = null;
        try {
            File file = getSaveFilePath();
            if (!file.exists()) {
                createSaveFile(file);
                if (!file.exists()) {
                    Log.println(Log.ERROR, null, "Cannot save: No file");
                    return false;
                }
            }
            outputStream = new FileOutputStream(file);
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(userData);
            return true;
        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, null, "File read issue (FNF): " + e.getMessage());
        } catch (IOException e) {
            Log.println(Log.ERROR, null, "File create issue (IO): " + e.getMessage());
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Log.println(Log.ERROR, null, "Error closing stream output");
                }
            }
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    Log.println(Log.ERROR, null, "Error closing stream oos");
                }
            }
        }
        return false;
    }
    private File getSaveFilePath() throws IOException {
        File dirPath = App.getContext().getExternalFilesDir(null);
        return new File(dirPath + "/" + USER_FILE_NAME);
    }
    private void createSaveFile(File file) throws IOException {
        try {
            boolean fileCreation = file.createNewFile();
            Log.println(Log.DEBUG, null, "Save file Creation: " + fileCreation);
        } catch (IOException e) {
            throw new IOException();
        }
    }

    public void eraseUserData(){
        File file = null;
        try {
            file = getSaveFilePath();
            if (file.exists()) {
                file.delete();
            }
            userData = new UserData();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean upsertUserWord(WordPair wordPair, UUID wordPairID) {
        UserWordPair userWordPair;
        if(wordPairID == null){
            userWordPair = new UserWordPair(wordPair);
            userData.getUserCreatedWords().add(userWordPair);
        }else{
            Optional<UserWordPair> foundWordOpt = userData.getUserCreatedWords().stream().filter(savedWord -> savedWord.getUuid().equals(wordPairID)).findFirst();
            if(foundWordOpt.isPresent()){
                userWordPair = foundWordOpt.get();
                userWordPair.setWordPair(wordPair);
            }
        }
        return saveUserData();
    }
}


