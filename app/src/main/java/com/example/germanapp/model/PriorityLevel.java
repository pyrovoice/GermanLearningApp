package com.example.germanapp.model;

import androidx.annotation.NonNull;

import java.util.Arrays;
import java.util.Optional;

public enum PriorityLevel {
    LOWEST,
    LOW,
    MEDIUM,
    HIGH,
    HIGHEST;

    public static String getStringValue(PriorityLevel priorityLevel){
        switch (priorityLevel){
            case LOWEST: return "Lowest";
            case LOW: return "Low";
            case MEDIUM: return "Medium";
            case HIGH: return "High";
            case HIGHEST: return "Highest";
        }
        return "";
    }
    private static String[] stringValues = null;
    public static String[] getStringValues(){
        if (stringValues == null){
            stringValues = Arrays.stream(PriorityLevel.values())
                    .map(PriorityLevel::getStringValue)
                    .toArray(String[]::new);
        }
        return stringValues;
    }

    @NonNull
    @Override
    public String toString() {
        return getStringValue(this);
    }

    public static PriorityLevel getPriorityLevelBelow(PriorityLevel priorityLevel){
        if(priorityLevel.ordinal() == 0){
            return priorityLevel;
        }
        return Arrays.stream(PriorityLevel.values()).filter(p -> p.ordinal() == priorityLevel.ordinal()-1).findAny().get();
    }
}
