package com.nocmok.uxprototype.scenes;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.PrototypeApp;
import com.nocmok.uxprototype.Utils;
import com.nocmok.uxprototype.layouts.LayoutHint;
import com.nocmok.uxprototype.layouts.Layouts;

import javafx.fxml.FXMLLoader;

public class SessionScene extends Scene {

    private final static Parent placeholder = new Label("failed to load layout");

    private final static Set<KeyCode> acceptableKeys = new HashSet<>();

    static {
        acceptableKeys.add(KeyCode.S);
        acceptableKeys.add(KeyCode.D);
        acceptableKeys.add(KeyCode.F);
        acceptableKeys.add(KeyCode.G);
        acceptableKeys.add(KeyCode.H);
        acceptableKeys.add(KeyCode.J);
        acceptableKeys.add(KeyCode.K);
        acceptableKeys.add(KeyCode.L);
        acceptableKeys.add(KeyCode.SPACE);
        acceptableKeys.add(KeyCode.ENTER);
    }

    private Predictor predictor;

    private List<String> sentences;

    private TextArea textBox;

    private TextArea sentenceBox;

    private Button completeButton;

    private Button nextButton;

    private Label progressLabel;

    private LayoutHint hint;

    private T9Parser parser;

    private Sentence sentence;

    private Sentence sampleSentence;

    private Sentence sentenceToDisplay;

    private int sentenceTracker;

    private Benchmark benchmark;

    private List<SessionMetrics> statistics;

    public SessionScene(Predictor predictor, List<String> sentences) {
        super(placeholder);

        this.predictor = predictor;
        this.sentences = sentences.size() > 0 ? sentences : List.of("");
        this.sentenceTracker = -1;
        this.statistics = new ArrayList<>(sentences.size());

        try {
            setRoot(FXMLLoader.load(Layouts.get("session_layout.fxml")));
            this.getRoot().requestFocus();
        } catch (IOException e) {
            throw new RuntimeException("failed to load layout for scene, due to i/o error", e);
        }

        this.textBox = (TextArea) lookup("#text");
        this.sentenceBox = (TextArea) lookup("#sentence");
        this.completeButton = (Button) lookup("#complete_button");
        this.nextButton = (Button) lookup("#next_button");
        this.progressLabel = (Label) lookup("#progress");
        this.hint = (LayoutHint) lookup("#hint");
        hint.setLayout(predictor.getLayout());

        this.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        this.addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyReleased);
        this.addEventFilter(MouseEvent.MOUSE_CLICKED, this::onMouseClicked);
        completeButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onSessionCompleted);
        nextButton.addEventHandler(MouseEvent.MOUSE_CLICKED, this::onNextSentence);

        nextSentence();
    }

    private void onMouseClicked(MouseEvent event) {
        getRoot().requestFocus();
    }

    private boolean ignoreKey(KeyCode code) {
        return !acceptableKeys.contains(code);
    }

    private void onKeyPressed(KeyEvent event) {
        if (ignoreKey(event.getCode())) {
            return;
        }
        if (event.getCode().equals(KeyCode.SPACE)) {
            onSpaceTyped();
        } else if (event.getCode().equals(KeyCode.ENTER)) {
            benchmark.type();
            onNextWordRequested();
        } else {
            benchmark.type();
            hint.lightCell(event.getCode().getChar().charAt(0));
            onCharacterTyped(event.getCode().getChar().charAt(0));
        }
    }

    private void onKeyReleased(KeyEvent event) {
        hint.unlight();
    }

    private void onNextWordRequested() {
        parser.nextWord();
        if (parser.word() != null) {
            sentenceToDisplay.addWord(parser.word());
            textBox.setText(sentenceToDisplay.toString());
            sentenceToDisplay.dropLast();
        }
    }

    private void onCharacterTyped(char ch) {
        parser.add(ch);
        if (parser.word() != null) {
            sentenceToDisplay.addWord(parser.word());
            textBox.setText(sentenceToDisplay.toString());
            sentenceToDisplay.dropLast();
        }
    }

    private void onSpaceTyped() {
        if (parser.word() != null) {
            sentence.addWord(parser.word());
        }
        sentenceToDisplay.addWord(" ");
        parser.clear();

        /** TODO */
        if (!benchmark.stopped() && sampleSentence.equals(sentence)) {
            completeSentence();
        }
    }

    private void onSessionCompleted(MouseEvent event) {
        completeSession();
    }

    /** TODO */
    private void completeSession() {
        if (!benchmark.stopped()) {
            completeSentence();
        }
        if (!saveStatistic()) {
            return;
        }
        PrototypeApp.getApp().home();
    }

    private boolean saveStatistic() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранить");
        File dst = fileChooser.showSaveDialog(PrototypeApp.getApp().stage());
        if (dst == null) {
            return false;
        }
        String csv = statisticToCsv(statistics);
        Utils.writeStringToFile(dst, Charset.forName("UTF-8"), csv);
        return true;
    }

    private String statisticToCsv(List<SessionMetrics> statistics) {
        StringBuilder csv = new StringBuilder();
        csv.append(SessionMetrics.csvCols());
        csv.append("\n");
        for (SessionMetrics metrics : statistics) {
            csv.append(metrics.toCsv());
            csv.append("\n");
        }
        return csv.toString();
    }

    private void completeSentence() {
        Benchmark.BenchmarkResult result = benchmark.stop();
        boolean sentenceMatched = sentence.equals(sampleSentence);
        SessionMetrics metrics = new SessionMetrics(result.keysTyped(), sentence.size(), result.duration(),
                sentenceMatched);
        statistics.add(metrics);
    }

    private void onNextSentence(MouseEvent event) {
        if (!benchmark.stopped()) {
            completeSentence();
        }
        nextSentence();
    }

    private void nextSentence() {
        if (sentenceTracker + 1 >= sentences.size()) {
            return;
        }
        sentenceTracker = sentenceTracker + 1;
        sentenceBox.setText(sentences.get(sentenceTracker));
        textBox.clear();
        progressLabel.setText(String.format("%d/%d", sentenceTracker + 1, sentences.size()));
        sampleSentence = new Sentence(sentences.get(sentenceTracker));
        sentenceToDisplay = new Sentence();
        sentence = new Sentence();
        parser = new T9Parser(predictor);
        benchmark = new Benchmark();
    }
}