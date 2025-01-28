package com.example.germanapp.service;

import android.util.Log;

import com.example.germanapp.App;
import com.example.germanapp.model.UserData;
import com.example.germanapp.model.WordPairTracking;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Optional;

public class DataSaverService {
    private static DataSaverService instance = null;
    final String USER_FILE_NAME = "user_file.txt";



    public UserData userData = null;
    private DataSaverService() {
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

    public static DataSaverService getInstance() {
        if (instance == null) {
            instance = new DataSaverService();
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

    public void saveUserData() {
        FileOutputStream outputStream = null;
        ObjectOutputStream oos = null;
        try {
            File file = getSaveFilePath();
            if (!file.exists()) {
                createSaveFile(file);
                if (!file.exists()) {
                    Log.println(Log.ERROR, null, "Cannot save: No file");
                    return;
                }
            }
            outputStream = new FileOutputStream(file);
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(userData);

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
    }
    private File getSaveFilePath() throws IOException {
        File dirPath = App.getContext().getExternalFilesDir(null);
        Log.println(Log.DEBUG, null, dirPath.getPath());
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
}


