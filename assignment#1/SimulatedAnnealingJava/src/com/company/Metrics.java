package com.company;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

class Point {
    int x;
    int y;

    public Point(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static int sub(Point a, Point b) {
        return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
    }
}

public class Metrics {
    public static ArrayList<String> words = new ArrayList<>();
    public static ArrayList<Integer> frequencies = new ArrayList<>();
    public static ArrayList<Point> QWERTYKeyboard = new ArrayList<>();
    public static ArrayList<Point> keyboard = new ArrayList<>();

    static {
        QWERTYKeyboard.add(new Point(1, 3)); // а
        QWERTYKeyboard.add(new Point(2, 7)); // б
        QWERTYKeyboard.add(new Point(1, 2)); // в
        QWERTYKeyboard.add(new Point(0, 6)); // г
        QWERTYKeyboard.add(new Point(1, 8)); // д
        QWERTYKeyboard.add(new Point(0, 4)); // е
        QWERTYKeyboard.add(new Point(1, 9)); // ж
        QWERTYKeyboard.add(new Point(0, 9)); // з
        QWERTYKeyboard.add(new Point(2, 4)); // и
        QWERTYKeyboard.add(new Point(0, 0)); // й
        QWERTYKeyboard.add(new Point(0, 3)); // к
        QWERTYKeyboard.add(new Point(1, 7)); // л
        QWERTYKeyboard.add(new Point(2, 3)); // м
        QWERTYKeyboard.add(new Point(0, 5)); // н
        QWERTYKeyboard.add(new Point(1, 6)); // о
        QWERTYKeyboard.add(new Point(1, 4)); // п
        QWERTYKeyboard.add(new Point(1, 5)); // р
        QWERTYKeyboard.add(new Point(2, 2)); // с
        QWERTYKeyboard.add(new Point(2, 5)); // т
        QWERTYKeyboard.add(new Point(0, 2)); // у
        QWERTYKeyboard.add(new Point(1, 0)); // ф
        QWERTYKeyboard.add(new Point(0, 10)); // х
        QWERTYKeyboard.add(new Point(0, 1)); // ц
        QWERTYKeyboard.add(new Point(2, 1)); // ч
        QWERTYKeyboard.add(new Point(0, 7)); // ш
        QWERTYKeyboard.add(new Point(0, 8)); // щ
        QWERTYKeyboard.add(new Point(0, 11)); // Ъ
        QWERTYKeyboard.add(new Point(1, 1)); // ы
        QWERTYKeyboard.add(new Point(2, 6)); // ь
        QWERTYKeyboard.add(new Point(1, 10)); // э
        QWERTYKeyboard.add(new Point(2, 8)); // ю
        QWERTYKeyboard.add(new Point(2, 0)); // я

        keyboard.add(new Point(1, 1));
        keyboard.add(new Point(1, 2));
        keyboard.add(new Point(1, 3));
        keyboard.add(new Point(1, 4));
        keyboard.add(new Point(1, 5));
        keyboard.add(new Point(1, 6));
        keyboard.add(new Point(1, 7));
        keyboard.add(new Point(1, 8));
    }

    public static void readCSV() {
        try (FileReader fr = new FileReader("dictionary.csv"); BufferedReader reader = new BufferedReader(fr)) {
            String line = reader.readLine();
            while ((line = reader.readLine()) != null) {
                String[] splitLine = line.split(",");
                words.add(splitLine[0].trim());
                frequencies.add(Integer.parseInt(splitLine[2].trim()));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static int getLetterIndex(char letter) {
        return letter - 'а';
    }

    public static String getNumberFromWord(String word, int[] layoutArray) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < word.length(); ++i) {
            result.append(layoutArray[getLetterIndex(word.charAt(i))]);
        }

        return result.toString();
    }

    public static long countMainMetric(int[] layoutArray) {
        long result = 0;
        HashMap<String, Integer> orderCounter = new HashMap<>();

        for (int i = 0; i < words.size(); ++i) {
            String wordNumber = getNumberFromWord(words.get(i), layoutArray);
            orderCounter.put(wordNumber, orderCounter.getOrDefault(wordNumber, 0) + 1);

            result += (long) (orderCounter.get(wordNumber) - 1) * frequencies.get(i);
        }

        return result;
    }

    public static double countKSPC(int[] layoutArray) {
        int numeratorSum = 0;
        int denominatorSum = 0;
        HashMap<String, Integer> orderCounter = new HashMap<>();

        for (int i = 0; i < words.size(); ++i) {
            String wordNumber = getNumberFromWord(words.get(i), layoutArray);
            orderCounter.put(wordNumber, orderCounter.getOrDefault(wordNumber, 0) + 1);

            numeratorSum += (words.get(i).length() + orderCounter.get(wordNumber) - 1) * frequencies.get(i);
            denominatorSum += words.get(i).length() * frequencies.get(i);
        }

        return (double) numeratorSum / denominatorSum;
    }

    public static int countLP(int[] layoutArray) {
        int resultLP = 0;
        for (int i = 0; i < 32; ++i) {
            resultLP += Point.sub(QWERTYKeyboard.get(i), keyboard.get(layoutArray[i]));
        }

        return resultLP;
    }

    public static double countEnergy(int[] layoutArray) {
        long mainMetric = countMainMetric(layoutArray);
        long lp = countLP(layoutArray);

        return (double)(mainMetric * 1000 + lp) / 1000000;
    }
}
