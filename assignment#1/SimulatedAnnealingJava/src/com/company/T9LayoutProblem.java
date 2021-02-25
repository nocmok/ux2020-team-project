package com.company;

import java.io.IOException;
import java.util.*;

public class T9LayoutProblem {
    public static Random rand = new Random();
    public static char[] toKeyName = new char[]{'s', 'd', 'f', 'g', 'h', 'j', 'k', 'l'};

    public static double getProbability(double delta, double t) {
        return Math.exp(-delta / t);
    }

    public static boolean isTransition(double probability) {
        return rand.nextDouble() <= probability;
    }

    public static boolean validateLayout(int[] layoutArray) {
        boolean[] used = new boolean[8];
        Arrays.fill(used, false);

        for (int i = 0; i < 32; ++i) {
            used[layoutArray[i]] = true;
        }
        for (int i = 0; i < 8; ++i) {
            if (!used[i])
                return false;
        }

        return true;
    }

    public static int[] generateCandidate(int[] layoutArray) {
        int[] result;
        do {
            result = Arrays.copyOf(layoutArray, layoutArray.length);

            int randIndex = rand.nextInt(32);
            int randValue = rand.nextInt(8);

            result[randIndex] = randValue;
        } while (!validateLayout(result));

        return result;
    }

    public static double decreaseTemperature(double initTemperature, int i) {
        return initTemperature / i * 1.1;
    }

    public static int[] createInitLayout() {
        int[] result = new int[32];

        do {
            for (int i = 0; i < 32; ++i) {
                result[i] = rand.nextInt(8);
            }
        } while (!validateLayout(result));

        return result;
    }

    public static int[] simulateAnnealing(double initTemperature, double endTemperature, int steps) {
        int[] currentLayout = createInitLayout();
        double currentEnergy = Metrics.countEnergy(currentLayout);

        double minEnergy = currentEnergy;
        int[] resultLayout = Arrays.copyOf(currentLayout, currentLayout.length);

        double temperature = initTemperature;

        int[] candidateLayout;
        double candidateEnergy;

        for (int i = 1; i <= steps; ++i) {
            if (i % 100 == 0) {
                System.out.println("epoch = " + i + ", kspc = " + Metrics.countKSPC(resultLayout) +
                        ", lp = " + Metrics.countLP(resultLayout));
            }

            candidateLayout = generateCandidate(currentLayout);
            candidateEnergy = Metrics.countEnergy(candidateLayout);

//            if (i % 100 == 0) {
//                System.out.println("epoch = " + i + ", kspc = " + Metrics.countKSPC(resultLayout) +
//                        ", lp = " + Metrics.countLP(resultLayout) + ", p = " +
//                        getProbability(candidateEnergy - currentEnergy, temperature) + ", t = " + temperature
//                        + ", de = " + (candidateEnergy - currentEnergy));
//            }

            if (candidateEnergy < currentEnergy) {
                currentLayout = candidateLayout;
                currentEnergy = candidateEnergy;
            } else {
                double probability = getProbability(candidateEnergy - currentEnergy, temperature);
                if (isTransition(probability)) {
                    currentLayout = candidateLayout;
                    currentEnergy = candidateEnergy;
                }
            }

            temperature = decreaseTemperature(initTemperature, i);

            if (currentEnergy < minEnergy) {
                minEnergy = currentEnergy;
                resultLayout = Arrays.copyOf(currentLayout, currentLayout.length);
            }

            if (temperature <= endTemperature) {
                break;
            }
        }

        return resultLayout;
    }

    public static char getCharFromInt(int value) {
        return (char) ('а' + value);
    }

    public static String layoutToString(int[] layoutArray) {
        HashMap<Integer, ArrayList<Integer>> keyToChars = new HashMap<>();
        for (int i = 0; i < 32; ++i) {
            ArrayList<Integer> arr = keyToChars.getOrDefault(layoutArray[i], new ArrayList<>());
            arr.add(i);

            keyToChars.put(layoutArray[i], arr);
        }

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{\n");


        for (var entry : keyToChars.entrySet()) {
            ArrayList<Integer> arr = entry.getValue();
            stringBuilder.append("\"").append(toKeyName[entry.getKey()]).append("\"").append(" : [");

            stringBuilder.append("\"").append(getCharFromInt(arr.get(0))).append("\"");
            for (int i = 1; i < arr.size(); ++i) {
                stringBuilder.append(", \"").append(getCharFromInt(arr.get(i))).append("\"");
            }
            stringBuilder.append("],\n");
        }
        stringBuilder.delete(stringBuilder.length() - 2, stringBuilder.length());
        stringBuilder.append("\n}");

        return stringBuilder.toString();
    }

    public static void solveT9LayoutProblem() {
        int[] layoutArray = simulateAnnealing(1000, 0.00001, 5000);
        System.out.println();
        System.out.println(layoutToString(layoutArray));
        System.out.println();
        System.out.println("kspc = " + Metrics.countKSPC(layoutArray));
        System.out.println("lp = " + Metrics.countLP(layoutArray));
    }

    public static void main(String[] args) throws IOException {
        Metrics.readCSV();

        solveT9LayoutProblem();

//        int[] baselineLayout = new int[] {2, 6, 1, 5, 7, 3, 7, 7, 4, 0, 2, 6, 3, 4, 5, 3, 4, 2, 4, 1, 0, 7, 0, 1, 6, 7, 7, 0, 5, 7, 7, 0};
//        System.out.println(Metrics.countKSPC(baselineLayout));
    }
}
