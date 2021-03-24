package com.nocmok.uxprototype.scenes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.T9;
import com.nocmok.uxprototype.scenes.Benchmark.BenchmarkResult;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Session {

    @FunctionalInterface
    public interface SessionHandler {

        public void handle(Session session);

        public static SessionHandler emptyHandler = (session) -> {
        };
    }

    static class SessionMetrics {

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

        public int keysTyped() {
            return keysTyped;
        }

        public int keysTranscribed() {
            return keysTranscribed;
        }

        public boolean matched() {
            return sentenceMatched;
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

        public static String csvCols() {
            return "input, transcribed, duration, matched, kspc, performance";
        }

        public String toCsv() {
            return keysTyped + ", " + keysTranscribed + ", " + duration + ", " + sentenceMatched + ", " + kspc + ", "
                    + performance;
        }
    }

    private final static Set<KeyCode> keyFilter = new HashSet<>();

    static {
        keyFilter.add(KeyCode.S);
        keyFilter.add(KeyCode.D);
        keyFilter.add(KeyCode.F);
        keyFilter.add(KeyCode.G);
        keyFilter.add(KeyCode.H);
        keyFilter.add(KeyCode.J);
        keyFilter.add(KeyCode.K);
        keyFilter.add(KeyCode.L);
        keyFilter.add(KeyCode.SPACE);
        keyFilter.add(KeyCode.B);
        keyFilter.add(KeyCode.N);
        keyFilter.add(KeyCode.ENTER);
    }

    private Predictor predictor;

    private Node eventSource;

    private String targetSentence;

    private SessionHandler onSessionCompleteHandler = SessionHandler.emptyHandler;

    private StringBuilder transcription;

    private Benchmark benchmark;

    private T9 t9;

    private boolean attached = false;

    public Session(Predictor predictor, String target) {
        this.predictor = predictor;
        this.targetSentence = target;
        this.transcription = new StringBuilder();
        this.t9 = new T9(predictor);
    }

    public void attach(Node eventSource) {
        this.eventSource = eventSource;
        this.benchmark = Benchmark.attach(eventSource);
        attached = true;
        eventSource.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private boolean ignoreKey(KeyCode key) {
        return !keyFilter.contains(key);
    }

    private void onKeyPressed(KeyEvent event) {
        if (ignoreKey(event.getCode())) {
            return;
        }
        if (event.getCode().equals(KeyCode.ENTER)) {
            onEnterPressed();
        } else if (event.getCode().equals(KeyCode.SPACE)) {
            onSpacePressed();
        } else if (event.getCode().equals(KeyCode.N)) {
            onNextWord();
        } else if (event.getCode().equals(KeyCode.B)) {
            onPrevWord();
        } else {
            onCharTyped(event.getCode());
        }
    }

    private void onEnterPressed() {
        String word = t9.word();
        transcription.append(word);
        completeSession();
    }

    private void onSpacePressed() {
        String word = t9.word();
        transcription.append(word);
        transcription.append(" ");
        t9.clear();
    }

    private void onNextWord() {
        t9.nextWord();
    }

    private void onPrevWord() {
        t9.prevWord();
    }

    private void onCharTyped(KeyCode key) {
        char ch = Character.toLowerCase(key.getChar().charAt(0));
        t9.add(ch);
    }

    private void completeSession() {
        onSessionCompleteHandler.handle(this);
    }

    public void setOnSessionCompleteHandler(SessionHandler handler) {
        this.onSessionCompleteHandler = Optional.ofNullable(handler).orElse(SessionHandler.emptyHandler);
    }

    public SessionMetrics statistic() {       
        BenchmarkResult result = benchmark.result();
        String[] targetWords = targetSentence.trim().toLowerCase().split("\\s+");
        String[] typedWords = transcription.toString().trim().toLowerCase().split("\\s+");
        boolean sentenceMatched = Arrays.equals(targetWords, typedWords);
        SessionMetrics stat = new SessionMetrics(result.keysTyped(), transcription.length(), result.duration(),
                sentenceMatched);
        return stat;
    }

    public void detach() {
        if (!attached) {
            throw new IllegalStateException("attempt to detach session that was not attached");
        }
        this.benchmark.detach();
        this.eventSource.removeEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        attached = false;
    }

    public Predictor predictor() {
        return predictor;
    }

    public String targetSentence() {
        return targetSentence;
    }
}
