package com.nocmok.uxprototype.scenes;

import java.util.HashSet;
import java.util.Set;

import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class Benchmark {

    static class BenchmarkResult {

        private int keysTyped;

        /** millis */
        private int duration;

        BenchmarkResult(int keysTyped, int duration) {
            this.keysTyped = keysTyped;
            this.duration = duration;
        }

        public int keysTyped() {
            return keysTyped;
        }

        public int duration() {
            return duration;
        }
    }

    public static Benchmark attach(Node eventSource) {
        return new Benchmark(eventSource);
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

    private Node eventSource;

    private int keysTyped;

    private long start;

    private boolean started = false;

    private boolean stopped = false;

    private BenchmarkResult result;

    private Benchmark(Node eventSource) {
        this.eventSource = eventSource;

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
            stop();
        } else {
            type();
        }
    }

    private void type() {
        if (!started) {
            start = System.currentTimeMillis();
        }
        started = true;
        keysTyped += 1;
    }

    private void stop() {
        stopped = true;
        long stop = System.currentTimeMillis();
        long time = started ? (stop - start) : 0;
        this.result = new BenchmarkResult(keysTyped, (int) time);
    }

    public BenchmarkResult result() {
        if (!stopped()) {
            throw new IllegalStateException("attempt to get benchmark result before it is stopped");
        }
        return this.result;
    }

    public boolean stopped() {
        return stopped;
    }

    public void detach() {
        eventSource.removeEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }
}
