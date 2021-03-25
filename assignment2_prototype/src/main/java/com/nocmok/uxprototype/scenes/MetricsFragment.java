package com.nocmok.uxprototype.scenes;

import java.io.IOException;

import com.nocmok.uxprototype.layouts.Layouts;
import com.nocmok.uxprototype.scenes.Session.SessionMetrics;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;

public class MetricsFragment extends Parent {

    public MetricsFragment(SessionMetrics metrics) {
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
        timeLabel.setText(String.format("%.3f",(double)metrics.duration() / 1000));
        matchedLabel.setText(String.valueOf(metrics.matched()));
        kspcLabel.setText(String.format("%.6f", metrics.keyStrokesPerChar()));
        lpLabel.setText(String.format("%.3f", metrics.typingPerformance() / 1000));
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
