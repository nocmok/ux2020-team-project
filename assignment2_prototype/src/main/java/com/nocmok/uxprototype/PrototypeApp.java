package com.nocmok.uxprototype;

import com.nocmok.uxprototype.scenes.MainScene;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class PrototypeApp extends Application {

    private Scene mainScene;

    private Stage primaryStage;

    private static PrototypeApp app;

    @Override
    public void start(Stage primaryStage) throws Exception {
        app = this;
        this.primaryStage = primaryStage;

        primaryStage.maxWidthProperty().set(800);
        primaryStage.minWidthProperty().set(800);
        primaryStage.maxHeightProperty().set(600);
        primaryStage.minHeightProperty().set(600);

        this.mainScene = new MainScene();
        primaryStage.setScene(mainScene);
        primaryStage.show();
    }

    public void home(){
        primaryStage.setScene(mainScene);
    }

    public void setScene(Scene scene) {
        primaryStage.setScene(scene);
    }

    public Stage stage() {
        return primaryStage;
    }

    public static PrototypeApp getApp() {
        return app;
    }

    public static void main(String[] args) {
        Application.launch(args);
    }
}