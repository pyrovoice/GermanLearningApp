package com.example.germanapp.model;

import java.io.Serializable;
import java.util.UUID;

public class UserWordPair implements Serializable {
    private WordPair wordPair;
    private UUID uuid;

    public UserWordPair(WordPair wordPair) {
        this.wordPair = wordPair;
        uuid = UUID.randomUUID();
    }

    public UserWordPair(WordPair wordPair, UUID uuid) {
        this.wordPair = wordPair;
        this.uuid = uuid;
    }

    public WordPair getWordPair() {
        return wordPair;
    }

    public void setWordPair(WordPair wordPair) {
        this.wordPair = wordPair;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }
}
