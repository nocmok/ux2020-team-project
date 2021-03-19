package com.nocmok.uxprototype.scenes;

import java.io.File;
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
import com.nocmok.uxprototype.Word;
import com.nocmok.uxprototype.layouts.Layouts;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;

public class MainScene extends Scene {

    private final static Parent placeholder = new Label("failed to load layout");

    private final static File layoutsDir = new File(
            MainScene.class.getClassLoader().getResource("keyboard_layouts").getPath());

    private final static File dictFile = new File(
            MainScene.class.getClassLoader().getResource("corpus/dictionary.csv").getPath());

    private VBox layoutListView;

    public MainScene() throws Exception {
        super(placeholder);

        Charset charset = Charset.forName("UTF-8");

        List<File> layoutFiles = layoutFiles();
        List<Word> words = Utils.readDictionaryCSV(dictFile, charset);
        Map<String, Predictor> predictors = new HashMap<>();

        for (File layoutFile : layoutFiles) {
            Map<String, List<String>> layout = Utils.parseLayoutJson(layoutFile, charset);
            Predictor predictor = new Predictor(words, layout);
            predictors.put(layoutFile.getName(), predictor);
        }

        setRoot(FXMLLoader.load(Layouts.get("main_layout.fxml")));

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
            label.addEventHandler(MouseEvent.MOUSE_CLICKED, (event) -> startSession(predictor));
            layoutListView.getChildren().add(label);
        }
    }

    private List<String> getSentences() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Файл с предложениями");
        File selectedFile = fileChooser.showOpenDialog(PrototypeApp.getApp().stage());
        if (selectedFile == null) {
            return null;
        }
        List<String> sentences = Utils.readLines(selectedFile, Charset.forName("UTF-8"));
        List<String> formatted = new ArrayList<>(sentences.size());
        for (String sentence : sentences) {
            formatted.add(sentence.trim().toLowerCase());
        }
        return formatted;
    }

    private void startSession(Predictor predictor) {
        List<String> sentences = getSentences();
        if (sentences == null) {
            return;
        }
        PrototypeApp.getApp().setScene(new SessionScene(predictor, sentences));
    }

    private List<File> layoutFiles() {
        List<File> files = Arrays.asList(layoutsDir.listFiles());
        return files;
    }
}