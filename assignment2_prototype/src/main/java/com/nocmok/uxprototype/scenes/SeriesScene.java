package com.nocmok.uxprototype.scenes;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.PrototypeApp;
import com.nocmok.uxprototype.layouts.Layouts;

import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Pane;

public class SeriesScene extends Scene {

    private final static Parent placeholder = new Label("failed to load layout");

    private Label tracker;

    private Pane frame;

    private Predictor predictor;

    private List<String> sentences;

    private Iterator<String> sentenceIt;

    private int nSession;

    private Session session;

    public SeriesScene(Predictor predictor, List<String> sentences) {
        super(placeholder);

        this.predictor = predictor;
        this.sentences = sentences;
        this.sentenceIt = sentences.iterator();
        this.nSession = 0;

        loadLayout("series_layout.fxml");

        this.tracker = (Label) lookup("#tracker");
        this.frame = (Pane) lookup("#frame");

        nextSession();
    }

    private void loadLayout(String layoutName) {
        try {
            Parent layout = (Parent) FXMLLoader.load(Layouts.get(layoutName));
            setRoot(layout);
        } catch (IOException e) {
            throw new RuntimeException("failed to load layout for " + this.getClass() + " due to io error", e);
        }
    }

    private void runSession(Session session) {
        frame.getChildren().clear();
        SessionFragment fragment = new SessionFragment(session);
        frame.getChildren().add(fragment);
    }

    private void endSession(Session session) {
        session.detach();
        showMetrics();
    }

    private void showMetrics() {
        frame.getChildren().clear();
        MetricsFragment fragment = new MetricsFragment(session.statistic());
        frame.getChildren().add(fragment);

        // leave metrics frame by pressing enter
        fragment.addEventHandler(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode().equals(KeyCode.ENTER)) {
                    fragment.removeEventHandler(KeyEvent.KEY_PRESSED, this);
                    SeriesScene.this.next();
                }
            }
        });
    }

    /**
     * Either next session, or series completion
     */
    private void next() {
        if (sentenceIt.hasNext()) {
            nextSession();
        } else {
            completeSeries();
        }
    }

    private void nextSession() {
        nSession += 1;
        tracker.setText(nSession + "/" + sentences.size());

        session = new Session(predictor, sentenceIt.next());
        session.setOnSessionCompleteHandler(this::endSession);

        runSession(session);
    }

    private void completeSeries() {
        PrototypeApp.getApp().home();
    }
}
