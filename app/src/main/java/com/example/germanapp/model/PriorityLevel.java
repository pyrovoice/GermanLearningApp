package com.example.germanapp.model;

public enum PriorityLevel {
    HIGHEST(5),
    HIGH(4),
    MEDIUM(3),
    LOW(2),
    LOWEST(1);

    public final Integer value;

    private PriorityLevel(int value) {
        this.value = value;
    }
}
