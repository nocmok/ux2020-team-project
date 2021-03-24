package com.nocmok.uxprototype.scenes;

import java.io.IOException;

import com.nocmok.uxprototype.layouts.Layouts;
import com.nocmok.uxprototype.scenes.Session.SessionMetrics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class MetricsFragment extends Parent {

    private SessionMetrics metrics;

    public MetricsFragment(SessionMetrics metrics) {
        this.metrics = metrics;
        setFocusTraversable(true);

        loadLayout("metrics_layout.fxml");

        Label keysTypedLabel = (Label) lookup("#keys_typed");
        Label keysTranscribedLabel = (Label) lookup("#keys_transcribed");
        Label timeLabel = (Label) lookup("#time");
        Label matchedLabel = (Label) lookup("#matched");
        Label kspcLabel = (Label) lookup("#kspc");
        Label lpLabel = (Label) lookup("#lp");

        keysTypedLabel.setText(String.valueOf(metrics.keysTyped()));
        keysTranscribedLabel.setText(String.valueOf(metrics.keysTranscribed()));
        timeLabel.setText(String.valueOf(metrics.duration()));
        matchedLabel.setText(String.valueOf(metrics.matched()));
        kspcLabel.setText(String.format("%.6f", metrics.keyStrokesPerChar()));
        lpLabel.setText(String.format("%.0f", metrics.typingPerformance()));
    }

    private void loadLayout(String layoutName) {
        try {
            Node layout = (Node) FXMLLoader.load(Layouts.get(layoutName));
            getChildren().add(layout);
        } catch (IOException e) {
            throw new RuntimeException("failed to load layout for " + this.getClass() + " due to io error", e);
        }
    }
}
