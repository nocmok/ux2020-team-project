package com.nocmok.uxprototype.scenes;

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

    private int keysTyped;

    private long start;

    private boolean started = false;

    private boolean stopped = false;

    public void type() {
        if (!started) {
            start = System.currentTimeMillis();
        }
        started = true;
        keysTyped += 1;
    }

    public BenchmarkResult stop() {
        stopped = true;
        long stop = System.currentTimeMillis();
        return new BenchmarkResult(keysTyped, (int) (stop - start));
    }

    public boolean stopped(){
        return stopped;
    }
}
