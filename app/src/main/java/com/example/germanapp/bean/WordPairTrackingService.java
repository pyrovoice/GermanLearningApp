package com.example.germanapp.bean;

import com.example.germanapp.model.PriorityLevel;
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
    private final int CURRENT_WORDPAIR_POOL_SIZE = 12;
    private final int CURRENT_WORDPAIR_POOL_SIZE_REPOPULATE = 7;
    private static WordPairTrackingService instance = null;
    List<WordPairTracking> userDataWordPool = null;
    List<WordPair> systemWordPool;

    private WordPairTrackingService() {
        userDataWordPool = buildUserWordPool();
        buildSystemWordPoolBacklog();
    }

    public static WordPairTrackingService getInstance() {
        if (instance == null) {
            instance = new WordPairTrackingService();
        }
        return instance;
    }

    private List<WordPairTracking> buildUserWordPool() {
        Optional<UserData> userDataOpt = UserDataService.getInstance().getUserData();
        if(userDataOpt.isEmpty()){
            return new ArrayList<>();
        }
        List<WordPairTracking> userData;
        userData = userDataOpt.get().getUserwordPool();
        userData = userData.stream().filter(wordPairTracking -> !isWordLearned(wordPairTracking)).collect(Collectors.toList());
        Collections.shuffle(userData);
        userData.sort(Comparator.comparing(wordPair -> -wordPair.getPriorityLevel().ordinal()));
        return userData;
    }

    private void buildSystemWordPoolBacklog() {
        List<WordPair> loadedSystemWordPool = WordLoaderService.getInstance().getSystemWordPairs();
        userDataWordPool.forEach(uwt -> {
            loadedSystemWordPool.remove(uwt.getWordPair());
        });
        Collections.shuffle(loadedSystemWordPool);
        loadedSystemWordPool.sort(Comparator.comparing(wordPair -> -wordPair.getPriorityLevel().ordinal()));
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
        List<WordPair> subList = systemWordPool.subList(0, nbrWordsToTakeFromPool);
        List<WordPairTracking> addedWords = subList
                .stream()
                .map(WordPairTracking::new)
                .collect(Collectors.toList());
        systemWordPool.removeAll(subList);
        UserDataService.getInstance().addWordsToUserPool(addedWords);
        currentWordPool.addAll(addedWords);
    }

    private boolean isWordLearned(WordPairTracking wordPairTracking) {
        return wordPairTracking.getSuccessTracker() >= WORDPAIR_SUCCESS_CUTOFF;
    }


    public void updateWordPairIncrement(WordPairTracking wordPair, boolean isSuccess) {
        wordPair.updateTracking(isSuccess);
        UserDataService.getInstance().saveUserData();
    }

    public void lowerPriorityAndRemoveFromPool(WordPairTracking wordPairTracking) {
        wordPairTracking.setPriorityLevel(PriorityLevel.getPriorityLevelBelow(wordPairTracking.getPriorityLevel()));
        currentWordPool.remove(wordPairTracking);
        UserDataService.getInstance().saveUserData();
    }
}
