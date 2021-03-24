package com.nocmok.uxprototype.scenes;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.PrototypeApp;
import com.nocmok.uxprototype.Utils;
import com.nocmok.uxprototype.Predictor.Word;
import com.nocmok.uxprototype.layouts.Layouts;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class MainScene extends Scene {

    private final static Parent placeholder = new Label("failed to load layout");

    private final static File layoutsDir = new File(
            MainScene.class.getClassLoader().getResource("keyboard_layouts").getPath());

    private final static File dictFile = new File(
            MainScene.class.getClassLoader().getResource("corpus/dictionary.csv").getPath());

    private final static File sentencesFile = new File(
            MainScene.class.getClassLoader().getResource("corpus/sentences.txt").getPath());

    private VBox layoutListView;

    private List<String> sentences;

    public MainScene() {
        super(placeholder);
        loadLayout("main_layout.fxml");

        Charset charset = Charset.forName("UTF-8");
        List<File> layoutFiles = layoutFiles();
        List<Word> words = Utils.readDictionaryCSV(dictFile, charset);
        Map<String, Predictor> predictors = new HashMap<>();

        for (File layoutFile : layoutFiles) {
            Map<String, List<String>> layout = Utils.parseLayoutJson(layoutFile, charset);
            Predictor predictor = new Predictor(words, layout);
            predictors.put(layoutFile.getName(), predictor);
        }

        this.layoutListView = (VBox) lookup("#layout_list");

        for (var entry : predictors.entrySet()) {
            String layoutName = entry.getKey();
            Predictor predictor = entry.getValue();

            Label label = new Label(layoutName);
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMinHeight(50.0);
            label.setStyle("-fx-border-color:black");
            label.setAlignment(Pos.CENTER);
            VBox.setMargin(label, new Insets(5, 5, 5, 5));
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> runSeries(predictor));
            layoutListView.getChildren().add(label);
        }

        this.sentences = getSentences();
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

    private List<File> layoutFiles() {
        List<File> files = Arrays.asList(layoutsDir.listFiles());
        return files;
    }
}