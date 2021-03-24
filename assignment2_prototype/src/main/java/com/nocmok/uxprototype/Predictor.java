package com.nocmok.uxprototype;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Collections;
import java.util.Comparator;

public class Predictor {

    public static class Word {
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

    private static final Map<Character, Integer> encoding = new HashMap<>();

    static {
        encoding.put('s', 0);
        encoding.put('d', 1);
        encoding.put('f', 2);
        encoding.put('g', 3);
        encoding.put('h', 4);
        encoding.put('j', 5);
        encoding.put('k', 6);
        encoding.put('l', 7);
    }

    private Map<String, Set<Word>> dictionary;

    private Map<String, List<String>> layout;

    private Predictor(List<Word> words, int[] layoutArray) {
        this.dictionary = generateDictionary(words, layoutArray);
    }

    public Predictor(List<Word> words, Map<String, List<String>> layout) {
        this(words, flattenMap(layout));
        this.layout = layout;
    }

    private static int[] flattenMap(Map<String, List<String>> map) {
        int[] layout = new int[33];
        for (var entry : map.entrySet()) {
            char key = entry.getKey().charAt(0);
            for (String chstr : entry.getValue()) {
                char ch = chstr.charAt(0);
                layout[getLetterIndex(ch)] = encoding.get(key);
            }
        }
        return layout;
    }

    private Map<String, Set<Word>> generateDictionary(List<Word> wordList, int[] layoutArray) {
        Map<String, Set<Word>> dictionary = new HashMap<>();
        Comparator<Word> comparator = (word, t1) -> {
            if (word.getFrequency() == t1.getFrequency()) {
                return (-1 * word.getWord().compareTo(t1.getWord()));
            }
            return (word.getFrequency() < t1.getFrequency() ? 1 : -1);
        };

        for (Word currentWord : wordList) {
            String wordNumber = encodeWord(currentWord.getWord(), layoutArray);

            if (!dictionary.containsKey(wordNumber)) {
                dictionary.put(wordNumber, new TreeSet<>(comparator));
            }

            Set<Word> set = dictionary.get(wordNumber);
            set.add(currentWord);
            dictionary.put(wordNumber, set);
        }

        return dictionary;
    }

    /**
     * 
     * @param codeSequence ^(0, 1, 2, 3, 4, 5, 6, 7)$
     * @return
     */
    public Set<Word> getWordsForCode(String codeSequence) {
        return dictionary.getOrDefault(codeSequence, Collections.emptySet());
    }

    private String toCodeSequence(String keySequence) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < keySequence.length(); ++i) {
            builder.append(encoding.get(keySequence.charAt(i)));
        }
        return builder.toString();
    }

    /**
     * 
     * @param keySequence ^(s, d, f, g, h, j, k, l)$
     * @return
     */
    public Set<Word> getWordsForKeys(String keySequence) {
        return getWordsForCode(toCodeSequence(keySequence));
    }

    private static int getLetterIndex(char letter) {
        return letter - 'а';
    }

    private String encodeWord(String word, int[] layoutArray) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); ++i) {
            result.append(layoutArray[getLetterIndex(word.charAt(i))]);
        }
        return result.toString();
    }

    public Map<String, List<String>> getLayout() {
        return layout;
    }
}