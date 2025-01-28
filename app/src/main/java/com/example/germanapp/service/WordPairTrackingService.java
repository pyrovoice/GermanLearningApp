package com.example.germanapp.service;

import static androidx.core.app.ActivityCompat.finishAffinity;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.example.germanapp.App;
import com.example.germanapp.model.PriorityLevel;
import com.example.germanapp.model.UserData;
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
import java.util.stream.Stream;

public class WordPairTrackingService {
    ArrayList<WordPairTracking> currentWordPool = new ArrayList<>();
    private int wordPoolTracker = 0;
    //TODO: Move those to configuration
    private final int WORDPAIR_SUCCESS_CUTOFF = 3;
    private final int CURRENT_WORDPAIR_POOL_SIZE = 10;
    private final int CURRENT_WORDPAIR_POOL_SIZE_REPOPULATE = 4;
    private static WordPairTrackingService instance = null;
    ArrayList<WordPairTracking> userDataWordPool = null;
    List<WordPair> systemWordPool = null;
    private WordPairTrackingService(){
        Optional<UserData> userDataOpt = DataSaverService.getInstance().getUserData();
        userDataOpt.ifPresent(userData -> userDataWordPool = userData.getUserwordPool());
        systemWordPool = getSystemWordPool();
    }

    private List<WordPair> getSystemWordPool() {
        return Arrays.asList(
                new WordPair("machen", "to do", PriorityLevel.HIGHEST),
                new WordPair("anbieten", "to offer", PriorityLevel.MEDIUM),
                new WordPair("sein", "to be", PriorityLevel.HIGHEST),
                new WordPair("führen", "to lead", PriorityLevel.HIGH),
                new WordPair("Detektiv", "detective", PriorityLevel.LOWEST),
                new WordPair("Stein", "a rock", PriorityLevel.HIGHEST)
        );
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
        removeFromCurrentPoolLearnedWords();
        if(currentWordPool.size() <= CURRENT_WORDPAIR_POOL_SIZE_REPOPULATE){
            populatePoolFromUserPool();
            populatePoolFromSystemPool();
        }
        currentWordPool.forEach(WordPairTracking::toggleLanguage);
        Collections.shuffle(currentWordPool);
    }

    private void removeFromCurrentPoolLearnedWords() {
        currentWordPool = currentWordPool.stream()
                .filter(wordPairTracking -> !isWordLearned(wordPairTracking))
                .collect(Collectors.toCollection(ArrayList::new));
    }
    private void populatePoolFromUserPool() {
        currentWordPool.addAll(userDataWordPool.stream()
                .filter(wordPairTracking -> !isWordLearned(wordPairTracking))
                .filter(wordPairTracking -> !currentWordPool.contains(wordPairTracking))
                .limit(CURRENT_WORDPAIR_POOL_SIZE - currentWordPool.size())
                .collect(Collectors.toList()));
    }

    private void populatePoolFromSystemPool(){
        List<WordPairTracking> addedWords = systemWordPool.stream()
                .sorted(Comparator.comparing(WordPair::getPriorityLevel))
                .filter(wordPair -> currentWordPool.stream().noneMatch(c -> c.getWordPair() == wordPair))
                .filter(wordPair -> userDataWordPool.stream().noneMatch(c -> c.getWordPair() == wordPair))
                .limit(CURRENT_WORDPAIR_POOL_SIZE - currentWordPool.size())
                .map(WordPairTracking::new)
                .collect(Collectors.toList());
        userDataWordPool.addAll(addedWords);
        currentWordPool.addAll(addedWords);
    }

    private boolean isWordLearned(WordPairTracking wordPairTracking) {
        return wordPairTracking.getSuccessTracker() > 0 && wordPairTracking.getNbrTries() <= 1 ||
                wordPairTracking.getSuccessTracker() >= WORDPAIR_SUCCESS_CUTOFF;
    }


    public void updateWordPairIncrement(WordPairTracking wordPair, boolean isSuccess){
        wordPair.updateTracking(isSuccess);
        DataSaverService.getInstance().saveUserData();
    }
}
