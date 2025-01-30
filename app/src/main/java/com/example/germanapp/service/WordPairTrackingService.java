package com.example.germanapp.service;

import com.example.germanapp.model.UserData;
import com.example.germanapp.model.WordPair;
import com.example.germanapp.model.WordPairTracking;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class WordPairTrackingService {
    ArrayList<WordPairTracking> currentWordPool = new ArrayList<>();
    private int wordPoolTracker = 0;
    //TODO: Move those to configuration
    private final int WORDPAIR_SUCCESS_CUTOFF = 3;
    private final int CURRENT_WORDPAIR_POOL_SIZE = 10;
    private final int CURRENT_WORDPAIR_POOL_SIZE_REPOPULATE = 4;
    private static WordPairTrackingService instance = null;
    ArrayList<WordPairTracking> userDataWordPool = null;
    List<WordPair> systemWordPool;

    private WordPairTrackingService() {
        Optional<UserData> userDataOpt = UserDataService.getInstance().getUserData();
        userDataOpt.ifPresent(userData -> userDataWordPool = userData.getUserwordPool());
        buildSystemWordPoolBacklog();
    }

    public static WordPairTrackingService getInstance() {
        if (instance == null) {
            instance = new WordPairTrackingService();
        }
        return instance;
    }

    private void buildSystemWordPoolBacklog() {
        List<WordPair> loadedSystemWordPool = WordLoaderService.getInstance().getSystemWordPairs();
        Collections.shuffle(loadedSystemWordPool);
        userDataWordPool.forEach(uwt -> {
            loadedSystemWordPool.remove(uwt.getWordPair());
        });
        loadedSystemWordPool.sort(Comparator.comparing(WordPair::getPriorityLevel));
        systemWordPool = loadedSystemWordPool;
    }

    public Optional<WordPairTracking> getNextWord() {
        if (wordPoolTracker >= currentWordPool.size()) {
            updatePool();
            wordPoolTracker = 0;
        }
        if (currentWordPool.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(currentWordPool.get(wordPoolTracker++));
    }

    private void updatePool() {
        removeFromCurrentPoolLearnedWords();
        if (currentWordPool.size() <= CURRENT_WORDPAIR_POOL_SIZE_REPOPULATE) {
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

    private void populatePoolFromSystemPool() {
        int nbrWordsToTakeFromPool = Math.min(CURRENT_WORDPAIR_POOL_SIZE - currentWordPool.size(), systemWordPool.size());
        if (nbrWordsToTakeFromPool == 0) {
            return;
        }
        List<WordPair> subList = systemWordPool.subList(0, nbrWordsToTakeFromPool - 1);
        List<WordPairTracking> addedWords = subList
                .stream()
                .map(WordPairTracking::new)
                .collect(Collectors.toList());
        systemWordPool.removeAll(subList);
        userDataWordPool.addAll(addedWords);
        currentWordPool.addAll(addedWords);
    }

    private boolean isWordLearned(WordPairTracking wordPairTracking) {
        return wordPairTracking.getSuccessTracker() > 0 && wordPairTracking.getNbrTries() <= 1 ||
                wordPairTracking.getSuccessTracker() >= WORDPAIR_SUCCESS_CUTOFF;
    }


    public void updateWordPairIncrement(WordPairTracking wordPair, boolean isSuccess) {
        wordPair.updateTracking(isSuccess);
        UserDataService.getInstance().saveUserData();
    }
}
