package com.nocmok.uxprototype.scenes;

import javafx.scene.Scene;
import javafx.scene.Parent;
import javafx.scene.control.Label;

import com.nocmok.uxprototype.layouts.Layouts;

import javafx.fxml.FXMLLoader;

public class SessionScene extends Scene {
 
    private final static Parent placeholder = new Label("failed to load layout");

    public SessionScene() throws Exception {
        super(placeholder);
        setRoot(FXMLLoader.load(Layouts.get("session_layout.fxml")));
    }
}