package com.nocmok.uxprototype.scenes;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.PrototypeApp;
import com.nocmok.uxprototype.Utils;
import com.nocmok.uxprototype.Predictor.Word;
import com.nocmok.uxprototype.layouts.Layouts;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;

public class MainScene extends Scene {

    private final static Parent placeholder = new Label("failed to load layout");

    private final static String aLayoutPath = "keyboard_layouts/control_layout.json";

    private final static String bLayoutPath = "keyboard_layouts/test_layout.json";

    private final static String dictPath = "corpus/dictionary.csv";

    private final static String sentencesPath = "corpus/sentences.txt";

    private List<String> sentences;

    public MainScene() {
        super(placeholder);
        loadLayout("main_layout.fxml");

        Charset charset = Charset.forName("UTF-8");

        List<Word> words = Utils.readDictionaryCSV(getResourceAsStream(dictPath), charset);
        this.sentences = getSentences();

        Predictor aPredictor = new Predictor(words, Utils.parseLayoutJson(getResourceAsStream(aLayoutPath), charset));
        Predictor bPredictor = new Predictor(words, Utils.parseLayoutJson(getResourceAsStream(bLayoutPath), charset));

        Label aLabel = (Label) lookup("#a_layout_button");
        Label bLabel = (Label) lookup("#b_layout_button");

        aLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> runSeries(aPredictor));
        bLabel.addEventFilter(MouseEvent.MOUSE_CLICKED, e -> runSeries(bPredictor));
    }

    private void loadLayout(String layoutName) {
        try {
            Parent layout = (Parent) FXMLLoader.load(Layouts.get(layoutName));
            setRoot(layout);
        } catch (IOException e) {
            throw new RuntimeException("failed to load layout for " + this.getClass() + " due to io error", e);
        }
    }

    private List<String> getSentences() {
        return Utils.readLines(getResourceAsStream(sentencesPath), Charset.forName("UTF-8"));
    }

    private void runSeries(Predictor predictor) {
        List<String> randomSentences = Utils.subsample(sentences, PrototypeApp.sentencesInSeries);
        if (sentences == null) {
            return;
        }
        PrototypeApp.getApp().setScene(new SeriesScene(predictor, randomSentences));
    }

    private static InputStream getResourceAsStream(String path) {
        return MainScene.class.getClassLoader().getResourceAsStream(path);
    }
}