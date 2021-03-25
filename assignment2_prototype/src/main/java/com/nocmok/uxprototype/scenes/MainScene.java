package com.nocmok.uxprototype.scenes;

import java.io.File;
import java.io.IOException;
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

    private final static File layoutsDir = new File(
            MainScene.class.getClassLoader().getResource("keyboard_layouts").getPath());

    private final static File dictFile = new File(
            MainScene.class.getClassLoader().getResource("corpus/dictionary.csv").getPath());

    private final static File sentencesFile = new File(
            MainScene.class.getClassLoader().getResource("corpus/sentences.txt").getPath());

    private List<String> sentences;

    public MainScene() {
        super(placeholder);
        loadLayout("main_layout.fxml");

        Charset charset = Charset.forName("UTF-8");

        File aLayoutFile = new File(layoutsDir, "control_layout.json");
        File bLayoutFile = new File(layoutsDir, "test_layout.json");

        List<Word> words = Utils.readDictionaryCSV(dictFile, charset);
        this.sentences = getSentences();

        Predictor aPredictor = new Predictor(words, Utils.parseLayoutJson(aLayoutFile, charset));
        Predictor bPredictor = new Predictor(words, Utils.parseLayoutJson(bLayoutFile, charset));

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
        return Utils.readLines(sentencesFile, Charset.forName("UTF-8"));
    }

    private void runSeries(Predictor predictor) {
        List<String> randomSentences = Utils.subsample(sentences, PrototypeApp.sentencesInSeries);
        if (sentences == null) {
            return;
        }
        PrototypeApp.getApp().setScene(new SeriesScene(predictor, randomSentences));
    }
}