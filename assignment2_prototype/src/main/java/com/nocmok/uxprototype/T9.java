package com.nocmok.uxprototype;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Set;

import com.nocmok.uxprototype.Predictor.Word;

public class T9 {

    private Predictor predictor;

    private StringBuilder input;

    private String word;

    private List<Word> words;

    private ListIterator<Word> wordIt;

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
    }

    private char mapKey(char key) {
        return keysMapping.get(key);
    }

    /** if word not mathced */
    private String defaultWord() {
        var layout = predictor.getLayout();
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
        return word;
    }

    /** Word after current */
    public String nextWord() {
        words();
        word = wordIt.hasNext() ? wordIt.next().getWord() : word;
        return word;
    }

    /** Word before current */
    public String prevWord() {
        words();
        word = wordIt.hasPrevious() ? wordIt.previous().getWord() : word;
        return word;
    }

    public List<Word> words() {
        if (words.isEmpty()) {
            Set<Word> wordSet = predictor.getWordsForKeys(input.toString());
            if (wordSet.isEmpty()) {
                words.add(new Word(defaultWord(), -1));
            } else {
                words.addAll(wordSet);
            }
            wordIt = words.listIterator();
            word = wordIt.next().getWord();
        }
        return words;
    }

    public void clear() {
        input.setLength(0);
        words.clear();
        wordIt = null;
        word = null;
    }
}
