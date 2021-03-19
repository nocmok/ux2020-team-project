package com.nocmok.uxprototype;

public class Word {
    private final String word;
    private final int frequency;
 
    public Word(String word, int frequency) {
        this.word = word;
        this.frequency = frequency;
    }
 
    public String getWord() {
        return word;
    }
 
    public int getFrequency() {
        return frequency;
    }
}

