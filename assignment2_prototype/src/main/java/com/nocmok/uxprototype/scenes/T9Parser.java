package com.nocmok.uxprototype.scenes;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.Word;

public class T9Parser {

    private Predictor predictor;

    private StringBuilder input;

    private String word;

    private Iterator<Word> wordsIter;

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

    public T9Parser(Predictor predictor) {
        this.predictor = predictor;
        this.input = new StringBuilder();
    }

    private char mapKey(char key) {
        return keysMapping.get(key);
    }

    /** if word not mathced */
    private String defaultWord(CharSequence input){
        var layout = predictor.getLayout();
        StringBuilder builder = new StringBuilder();
        for(int i = 0; i < input.length(); ++i){
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
        Set<Word> words = predictor.getWordsForKeys(input.toString()); 
        wordsIter = words.iterator();
        word = words.size() > 0 ? wordsIter.next().getWord() : defaultWord(input);
    }

    public String word() {
        return word;
    }

    public String nextWord() {
        word = Optional.ofNullable(wordsIter).orElse(Collections.emptyIterator()).hasNext() ? wordsIter.next().getWord() : word;
        return word;
    }

    public void clear() {
        input.setLength(0);
        wordsIter = null;
        word = null;
    }
}
