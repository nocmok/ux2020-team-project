package com.nocmok.uxprototype.layouts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class LayoutHint extends GridPane {

    private static final int rows = 2;

    private static final int cols = 8;

    private static final char[] keys = new char[] { 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l' };

    private static final Map<Character, Integer> keyCodeMapping = new HashMap<>();

    private final static Set<KeyCode> acceptableKeys = new HashSet<>();

    private static final String defaultStyleClass = "layout-hint-key-default";

    private static final String lightStyleClass = "layout-hint-key-light";

    static {
        keyCodeMapping.put('s', 0);
        keyCodeMapping.put('d', 1);
        keyCodeMapping.put('f', 2);
        keyCodeMapping.put('g', 3);
        keyCodeMapping.put('h', 4);
        keyCodeMapping.put('j', 5);
        keyCodeMapping.put('k', 6);
        keyCodeMapping.put('l', 7);

        acceptableKeys.add(KeyCode.S);
        acceptableKeys.add(KeyCode.D);
        acceptableKeys.add(KeyCode.F);
        acceptableKeys.add(KeyCode.G);
        acceptableKeys.add(KeyCode.H);
        acceptableKeys.add(KeyCode.J);
        acceptableKeys.add(KeyCode.K);
        acceptableKeys.add(KeyCode.L);
    }

    private Label[] labels = new Label[cols];

    public LayoutHint() {
        makeGrid();

        setFocusTraversable(true);
        addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
        addEventHandler(KeyEvent.KEY_RELEASED, this::onKeyReleased);
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

            label.getStyleClass().clear();
            label.getStyleClass().add(defaultStyleClass);
            label.setText(String.valueOf(keys[i]).toUpperCase());
            label.setMaxWidth(Double.MAX_VALUE);
            label.setMaxHeight(Double.MAX_VALUE);
            label.setAlignment(Pos.CENTER);

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
            label.wrapTextProperty().set(true);

            setHalignment(label, HPos.CENTER);
            setValignment(label, VPos.CENTER);
            setMargin(label, new Insets(5, 5, 5, 5));

            add(label, i, 0);
        }
    }

    private boolean ignoreKey(KeyCode key) {
        return !acceptableKeys.contains(key);
    }

    private void onKeyPressed(KeyEvent event) {
        if (ignoreKey(event.getCode())) {
            return;
        }
        char key = Character.toLowerCase(event.getCode().getChar().charAt(0));
        lightCell(key);
    }

    private void onKeyReleased(KeyEvent event) {
        if (ignoreKey(event.getCode())) {
            return;
        }
        char key = Character.toLowerCase(event.getCode().getChar().charAt(0));
        unlightCell(key);
    }

    public void lightCell(char key) {
        Label label = labels[keyCodeMapping.get(Character.toLowerCase(key))];
        label.getStyleClass().clear();
        label.getStyleClass().add(lightStyleClass);
    }

    public void unlightCell(char key) {
        Label label = labels[keyCodeMapping.get(Character.toLowerCase(key))];
        label.getStyleClass().clear();
        label.getStyleClass().add(defaultStyleClass);
    }
}
