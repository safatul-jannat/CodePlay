package com.example.codeplayfx.ui;

import com.example.codeplayfx.compiler.SimpleExecutor;
import com.example.codeplayfx.core.GameWorld;
import javafx.scene.control.TextArea;

public class CodePanel {
    private final TextArea codeTextArea;
    private final SimpleExecutor executor;

    public CodePanel(TextArea codeTextArea) {
        this.codeTextArea = codeTextArea;
        this.executor = new SimpleExecutor();

        setupCodeEditor();
    }

    private void setupCodeEditor() {
        codeTextArea.setStyle(
                "-fx-font-family: 'Consolas', monospace; " +
                        "-fx-font-size: 14px; " +
                        "-fx-control-inner-background: #2b2b2b; " +
                        "-fx-text-fill: #a9b7c6;"
        );
    }

    // Now accepts GameWorld as parameter
    public String executeCode(GameWorld gameWorld) {
        String code = codeTextArea.getText().trim();
        if (code.isEmpty()) {
            return "⚠ No code to execute";
        }

        return executor.execute(code, gameWorld);
    }

    public void clearCode() {
        codeTextArea.clear();
    }

    public void loadExampleCode() {
        codeTextArea.setText(
                "// Create Player objects:\n" +
                        "Player player1 = new Player(100, 200, \"hero\");\n" +
                        "Player player2 = new Player(300, 100, \"sidekick\");\n\n" +
                        "// Create Enemy objects:\n" +
                        "Enemy enemy1 = new Enemy(400, 300, \"boss\");\n" +
                        "Enemy enemy2 = new Enemy(500, 200, \"minion\");\n" +
                        "Enemy enemy3 = new Enemy(200, 400, \"guard\");"
        );
    }

    public TextArea getCodeTextArea() {
        return codeTextArea;
    }
}