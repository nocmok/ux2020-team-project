package com.nocmok.uxprototype.layouts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class LayoutHint extends GridPane {

    private static final int rows = 2;

    private static final int cols = 8;

    private static final char[] keys = new char[] { 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l' };

    private static final Map<Character, Integer> keyCodeMapping = new HashMap<>();

    private static final String defaultStyle = "-fx-border-radius: 5 5 5 5;-fx-background-color: white;-fx-background-radius: 5 5 5 5;-fx-border-color: black;";

    private static final String lightStyle = "-fx-background-color: gray;-fx-border-radius: 5 5 5 5;-fx-background-radius: 5 5 5 5;-fx-border-color: black;";

    static {
        keyCodeMapping.put('s', 0);
        keyCodeMapping.put('d', 1);
        keyCodeMapping.put('f', 2);
        keyCodeMapping.put('g', 3);
        keyCodeMapping.put('h', 4);
        keyCodeMapping.put('j', 5);
        keyCodeMapping.put('k', 6);
        keyCodeMapping.put('l', 7);
    }

    private Label[] labels = new Label[cols];

    public LayoutHint() {
        makeGrid();
    }

    private void makeGrid() {
        for (int i = 0; i < rows; ++i) {
            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100f / rows);
            getRowConstraints().add(row);
        }

        for (int i = 0; i < cols; ++i) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100f / cols);
            getColumnConstraints().add(col);
        }

        for (int i = 0; i < cols; ++i) {
            Label label = new Label();
            labels[i] = label;
            label.setText(String.valueOf(keys[i]).toUpperCase());
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);
            label.setStyle(defaultStyle);

            setHalignment(label, HPos.CENTER);
            setValignment(label, VPos.CENTER);
            setMargin(label, new Insets(5, 5, 5, 5));

            add(label, i, 1);
        }
    }

    public void setLayout(Map<String, List<String>> layout) {
        for (int i = 0; i < cols; ++i) {
            StringBuilder text = new StringBuilder();
            List<String> chars = layout.get(String.valueOf(keys[i]));
            for (String ch : chars) {
                text.append(ch);
            }
            Label label = new Label();
            label.setText(text.toString());
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setAlignment(Pos.BOTTOM_CENTER);

            setHalignment(label, HPos.CENTER);
            setValignment(label, VPos.CENTER);
            setMargin(label, new Insets(5, 5, 5, 5));

            add(label, i, 0);
        }
    }

    public void lightCell(char key) {
        labels[keyCodeMapping.get(Character.toLowerCase(key))].setStyle(lightStyle);
    }

    public void unlightCell(char key) {
        labels[keyCodeMapping.get(Character.toLowerCase(key))].setStyle(defaultStyle);
    }

    public void unlight() {
        for (Label label : labels) {
            label.setStyle(defaultStyle);
        }
    }
}
