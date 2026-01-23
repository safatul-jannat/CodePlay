package com.example.codeplayfx.ui;

import com.example.codeplayfx.compiler.CodeRunner;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;

public class CodePanel {
    private final TextArea codeTextArea;
    private final Button runButton;
    private final CodeRunner runner;

    public CodePanel(TextArea codeTextArea, Button runButton, CodeRunner runner) {
        this.codeTextArea = codeTextArea;
        this.runButton = runButton;
        this.runner = runner;

        wire();
    }

    private void wire() {
        runButton.setOnAction(e -> {
            String code = codeTextArea.getText();
            runner.run(code); // ✅ UI -> backend
        });
    }
}
