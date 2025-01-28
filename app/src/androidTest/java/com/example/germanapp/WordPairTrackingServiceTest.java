package com.example.germanapp;

import static org.junit.Assert.assertTrue;

import android.content.Context;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.germanapp.model.PriorityLevel;
import com.example.germanapp.model.WordPair;
import com.example.germanapp.model.WordPairTracking;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class WordPairTrackingServiceTest {
    @Test
    public void saveValidator() throws IOException, ClassNotFoundException {
        List<WordPairTracking> currentWordPool = new ArrayList<>();
        WordPairTracking wordPairTracking = new WordPairTracking(new WordPair("A", "a", PriorityLevel.MEDIUM));
        wordPairTracking.incrementMistakes();
        wordPairTracking.incrementMistakes();
        WordPairTracking wordPairTracking2 = new WordPairTracking(new WordPair("B", "B", PriorityLevel.MEDIUM));
        wordPairTracking2.incrementSuccesses();
        currentWordPool.add(wordPairTracking);
        currentWordPool.add(wordPairTracking2);
        String USER_FILE_NAME = "test";
        FileOutputStream outputStream = null;
        ObjectOutputStream oos = null;
        try {
            File file = new File(USER_FILE_NAME);
            if (!file.exists()) {
                file.createNewFile();
            }
            outputStream = App.getContext().openFileOutput(USER_FILE_NAME, Context.MODE_PRIVATE);
            oos = new ObjectOutputStream(outputStream);
            oos.writeObject(currentWordPool);

        } catch (FileNotFoundException e) {
            Log.println(Log.ERROR, null, "File read issue");
        } catch (IOException e) {
            Log.println(Log.ERROR, null, "File create issue");
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
        FileInputStream ois = App.getContext().openFileInput(USER_FILE_NAME);
        List<WordPairTracking> savedRetrievedValue = (List<WordPairTracking>) new ObjectInputStream(ois).readObject();
        assertTrue(savedRetrievedValue.size() != 0);
    }
}
