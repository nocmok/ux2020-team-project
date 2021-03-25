package com.nocmok.uxprototype.scenes;

import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.nocmok.uxprototype.Predictor;
import com.nocmok.uxprototype.T9;
import com.nocmok.uxprototype.layouts.LayoutHint;
import com.nocmok.uxprototype.layouts.Layouts;
import com.nocmok.uxprototype.layouts.WordsHint;

import javafx.fxml.FXMLLoader;

public class SessionFragment extends Parent {

    private final static Set<KeyCode> keyFilter = new HashSet<>();

    static {
        keyFilter.add(KeyCode.S);
        keyFilter.add(KeyCode.D);
        keyFilter.add(KeyCode.F);
        keyFilter.add(KeyCode.G);
        keyFilter.add(KeyCode.H);
        keyFilter.add(KeyCode.J);
        keyFilter.add(KeyCode.K);
        keyFilter.add(KeyCode.L);
        keyFilter.add(KeyCode.SPACE);
        keyFilter.add(KeyCode.B);
        keyFilter.add(KeyCode.N);
    }

    private Predictor predictor;

    private TextArea textBox;

    private WordsHint wordsHint;

    private T9 t9;

    private String targetSentence;

    private Sentence visibleText;

    private Session session;

    public SessionFragment(Session session) {
        this.session = session;
        this.predictor = session.predictor();
        this.targetSentence = session.targetSentence();
        this.t9 = new T9(predictor);
        this.visibleText = new Sentence();
        this.session.attach(this);

        // allows this fragment to grab focus 
        setFocusTraversable(true);

        loadLayout("session_layout.fxml");

        this.textBox = (TextArea) lookup("#text");

        var sentenceBox = (TextArea) lookup("#sentence");
        sentenceBox.setText(targetSentence);

        LayoutHint hint = (LayoutHint) lookup("#hint");
        hint.setLayout(predictor.getLayout());

        this.wordsHint = (WordsHint) lookup("#words_hint");

        this.addEventHandler(KeyEvent.KEY_PRESSED, this::onKeyPressed);
    }

    private void loadLayout(String layoutName) {
        try {
            Node layout = (Node) FXMLLoader.load(Layouts.get(layoutName));
            getChildren().add(layout);
        } catch (IOException e) {
            throw new RuntimeException("failed to load layout for " + this.getClass() + " due to io error", e);
        }
    }

    private boolean ignoreKey(KeyCode code) {
        return !keyFilter.contains(code);
    }

    private void onKeyPressed(KeyEvent event) {
        if (ignoreKey(event.getCode())) {
            return;
        }
        if (event.getCode().equals(KeyCode.SPACE)) {
            onSpaceTyped();
        } else if (event.getCode().equals(KeyCode.N)) {
            onNextWord();
        } else if (event.getCode().equals(KeyCode.B)) {
            onPrevWord();
        } else {
            char ch = Character.toLowerCase(event.getCode().getChar().charAt(0));
            onCharTyped(ch);
        }
    }

    private void onSpaceTyped() {
        visibleText.addWord(t9.word());
        visibleText.addWord(" ");
        textBox.setText(visibleText.toString() + "|");
        t9.clear();
        wordsHint.clear();
    }

    private void onNextWord() {
        t9.nextWord();
        visibleText.addWord(t9.word());
        textBox.setText(visibleText.toString() + "|");
        visibleText.dropLast();
        showWordHint();
    }

    private void onPrevWord() {
        t9.prevWord();
        visibleText.addWord(t9.word());
        textBox.setText(visibleText.toString() + "|");
        visibleText.dropLast();
        showWordHint();
    }

    private void onCharTyped(char ch) {
        t9.add(ch);
        visibleText.addWord(t9.word());
        textBox.setText(visibleText.toString() + "|");
        visibleText.dropLast();
        showPredictions();
        showWordHint();
    }

    private void showPredictions(){
        wordsHint.setWords(t9.words());
    }

    private void showWordHint(){
        wordsHint.lightItem(t9.wordPosition());
    }
}