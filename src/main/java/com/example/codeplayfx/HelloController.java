package com.example.codeplayfx;

import com.example.codeplayfx.ui.CodePanel;
import com.example.codeplayfx.compiler.DummyRunner;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;

public class HelloController {

    @FXML
    private TextArea codeTextArea;

    @FXML
    private Button runButton;

    @FXML
    private Pane gamepane;

    @FXML
    private void initialize() {
        System.out.println("Controller loaded");
        new CodePanel(codeTextArea, runButton, new DummyRunner());
    }

    @FXML
    private void onRun() {
        System.out.println("Run button clicked");
    }
}

