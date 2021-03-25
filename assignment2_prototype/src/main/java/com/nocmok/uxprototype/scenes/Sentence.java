package com.nocmok.uxprototype.scenes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sentence {

    private int size = 0;

    private List<String> words;

    public Sentence(String sentence) {
        this(new ArrayList<>(Arrays.asList(sentence.trim().split("\\s+"))));
    }

    public Sentence(List<String> words) {
        this.words = words;
        this.size = 0;
        for (String word : words) {
            size += word.length();
        }
    }

    public Sentence() {
        words = new ArrayList<>();
    }

    public void addWord(String word) {
        words.add(word);
        size += word.length();
    }

    public void dropLast() {
        size -= words.get(words.size() - 1).length();
        words.remove(words.size() - 1);
    }

    public void clear() {
        size = 0;
        words.clear();
    }

    /** number of characters */
    public int size() {
        return size;
    }

    /** number of words */
    public int words() {
        return words.size();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (String word : words) {
            builder.append(word);
        }
        return builder.toString();
    }
}
