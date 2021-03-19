package com.nocmok.uxprototype.scenes;

public class Sentence {

    private StringBuilder builder;

    private int words = 0;

    public Sentence() {
        builder = new StringBuilder();
    }

    public void addWord(String word) {
        if (builder.length() > 0) {
            builder.append(" ");
        }
        builder.append(word);
        words += 1;
    }

    public void dropLast() {
        int newLen = Integer.max(0, builder.length() - 1);
        for (; newLen > 0; --newLen) {
            if (builder.charAt(newLen) == ' ') {
                break;
            }
        }
        if (builder.length() > newLen) {
            words -= 1;
        }
        builder.setLength(newLen);
    }

    public void clear() {
        builder.setLength(0);
    }

    public int size() {
        return builder.length();
    }

    public int words() {
        return words;
    }

    @Override
    public String toString() {
        return builder.toString();
    }
}
