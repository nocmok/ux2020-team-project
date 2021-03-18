package com.nocmok.uxprototype;

import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;

import com.nocmok.uxprototype.scenes.SessionScene;

import javafx.application.Application;
import javafx.stage.Stage;

public class PrototypeApp extends Application {

    @Override
        public void start(Stage primaryStage) throws Exception {

        primaryStage.setScene(new SessionScene());
        
        primaryStage.show();
    }

    public static void main(String[] args){
        Application.launch(args);
    }
}