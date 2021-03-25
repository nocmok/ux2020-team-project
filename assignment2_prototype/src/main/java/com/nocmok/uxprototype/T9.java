package com.nocmok.uxprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.nocmok.uxprototype.Predictor.Word;

public class T9 {

    private Predictor predictor;

    private StringBuilder input;

    private List<Word> words;

    private int wordTracker;

    private static final Map<Character, Character> keysMapping = new HashMap<>();

    static {
        keysMapping.put('s', 's');
        keysMapping.put('d', 'd');
        keysMapping.put('f', 'f');
        keysMapping.put('g', 'g');
        keysMapping.put('h', 'h');
        keysMapping.put('j', 'j');
        keysMapping.put('k', 'k');
        keysMapping.put('l', 'l');

        keysMapping.put('ы', 's');
        keysMapping.put('в', 'd');
        keysMapping.put('а', 'f');
        keysMapping.put('п', 'g');
        keysMapping.put('р', 'h');
        keysMapping.put('о', 'j');
        keysMapping.put('л', 'k');
        keysMapping.put('д', 'l');
    }

    public T9(Predictor predictor) {
        this.predictor = predictor;
        this.input = new StringBuilder();
        this.words = new ArrayList<Word>();
        this.wordTracker = 0;
    }

    private char mapKey(char key) {
        return keysMapping.get(key);
    }

    /** if word not mathced */
    private String defaultWord() {
        Map<String, List<String>> layout = predictor.getLayout();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < input.length(); ++i) {
            builder.append(layout.get(String.valueOf(input.charAt(i))).get(0));
        }
        return builder.toString();
    }

    /**
     * 
     * @param ch ^(ы в а п р о л д)$ or ^(s d f g h j k l)$
     */
    public void add(char ch) {
        input.append(mapKey(Character.toLowerCase(ch)));
        words.clear();
    }

    /** Current word */
    public String word() {
        words();
        return words.get(wordTracker).getWord();
    }

    /** Word after current */
    public String nextWord() {
        words();
        wordTracker = Integer.min(wordTracker + 1, words.size() - 1);
        return words.get(wordTracker).getWord();
    }

    /** Word before current */
    public String prevWord() {
        words();
        wordTracker = Integer.max(wordTracker - 1, 0);
        return words.get(wordTracker).getWord();
    }

    public List<Word> words() {
        if (words.isEmpty()) {
            Set<Word> wordSet = predictor.getWordsForKeys(input.toString());
            if (wordSet.isEmpty()) {
                words.add(new Word(defaultWord(), -1));
            } else {
                words.addAll(wordSet);
            }
            wordTracker = 0;
        }
        return words;
    }

    public int wordPosition() {
        return wordTracker;
    }

    public void clear() {
        input.setLength(0);
        words.clear();
    }
}
