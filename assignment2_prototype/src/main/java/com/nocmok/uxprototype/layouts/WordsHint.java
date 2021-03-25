package com.nocmok.uxprototype.layouts;

import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;

public class WordsHint extends FlowPane {

    private static final String lightStyleClass = "word-hint-tag-light";

    private static final String defaultStyleClass = "word-hint-tag-default";

    private int lightedItem;

    private boolean hasItems;

    private Node placeholder;

    public WordsHint() {
        this.lightedItem = -1;
        this.hasItems = false;
        this.placeholder = wrapWord("начните ввод");
        getChildren().add(placeholder);
    }

    private Node wrapWord(String word) {
        Label node = new Label();
        node.setText(word);
        node.getStyleClass().clear();
        node.getStyleClass().add(defaultStyleClass);
        node.setAlignment(Pos.CENTER);
        node.setPadding(new Insets(5.0, 5.0, 5.0, 5.0));
        return node;
    }

    public void setWords(List<?> words) {
        _clear();
        hasItems = !words.isEmpty();
        for (Object word : words) {
            Node node = wrapWord(word.toString());
            getChildren().add(node);
        }
    }

    public void lightItem(int i) {
        if (!hasItems) {
            return;
        }
        if (hasLightedItem()) {
            unlightItem(lightedItem);
        }
        Node node = getChildren().get(i);
        node.getStyleClass().clear();
        node.getStyleClass().add(lightStyleClass);
        lightedItem = i;
    }

    private boolean hasLightedItem() {
        return lightedItem >= 0;
    }

    private void unlightItem(int i) {
        Node node = getChildren().get(i);
        node.getStyleClass().clear();
        node.getStyleClass().add(defaultStyleClass);
    }

    public void clear() {
        _clear();
        getChildren().add(placeholder);
    }

    private void _clear() {
        lightedItem = -1;
        hasItems = false;
        getChildren().clear();
    }
}
