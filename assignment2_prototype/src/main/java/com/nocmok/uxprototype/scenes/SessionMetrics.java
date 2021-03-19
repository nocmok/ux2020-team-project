package com.nocmok.uxprototype.scenes;

public class SessionMetrics {

    private double kspc;

    /** duration / keysTranscribed */
    private double performance;

    private int keysTyped;

    private int keysTranscribed;

    private boolean sentenceMatched;

    private int duration;

    public SessionMetrics(int keysTyped, int keysTranscribed, int duration, boolean matched) {
        this.keysTyped = keysTyped;
        this.keysTranscribed = keysTranscribed;
        this.sentenceMatched = matched;
        this.kspc = (keysTranscribed != 0) ? (double) keysTyped / keysTranscribed : Double.NaN;
        this.performance = (keysTranscribed != 0) ? (double) duration / keysTranscribed : Double.NaN;
        this.duration = duration;
    }

    public int keysTyped(){
        return keysTyped;
    }

    public int keysTranscribed(){
        return keysTranscribed;
    }

    public boolean matched(){
        return matched();
    }

    public double keyStrokesPerChar() {
        return kspc;
    }

    public int duration() {
        return duration;
    }

    public double typingPerformance() {
        return performance;
    }

    public static String csvCols(){
        return "input, transcribed, duration, matched, kspc, performance";
    }

    public String toCsv(){
        return keysTyped + ", " + keysTranscribed + ", " + duration + ", " + sentenceMatched + ", " + kspc + ", " + performance;
    }
}
