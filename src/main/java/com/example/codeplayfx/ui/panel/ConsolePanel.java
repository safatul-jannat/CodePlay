package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;

public class ConsolePanel extends UIPanel {

    private TextArea console;

    public ConsolePanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        console = (TextArea) loader.getNamespace().get("console");

        VBox consoleContainer = (VBox) loader.getNamespace().get("consoleContainer");
        this.root = consoleContainer;
    }

    @Override
    public void initialize() {
        console.setEditable(false);
        console.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #d4d4d4; -fx-font-family: 'Monospace';");
        log("Console initialized. Ready for output.");
    }

    public void log(String message) {
        if (console != null) {
            console.appendText("[INFO] " + message + "\n");
            console.positionCaret(console.getText().length());
        }
    }

    public void error(String message) {
        if (console != null) {
            console.appendText("[ERROR] " + message + "\n");
            console.positionCaret(console.getText().length());
        }
    }

    public void clear() {
        if (console != null) {
            console.clear();
        }
    }
}