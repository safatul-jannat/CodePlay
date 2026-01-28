package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.VBox;

public class ToolPanel extends UIPanel {

    private MainWindowScreen mainWindow;
    private ToggleButton codeButton;
    private Button saveButton;
    private Button docButton;
    private Button helpButton;
    private Button backButton;

    public ToolPanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        codeButton = (ToggleButton) loader.getNamespace().get("codeButton");
        saveButton = (Button) loader.getNamespace().get("saveButton");
        docButton = (Button) loader.getNamespace().get("docButton");
        helpButton = (Button) loader.getNamespace().get("helpButton");
        backButton = (Button) loader.getNamespace().get("backButton");

        VBox leftPanel = (VBox) loader.getNamespace().get("leftPanel");
        this.root = leftPanel;
    }

    @Override
    public void initialize() {
        setupButtonStyles();
        setupButtonActions();
        selectCodeButton(); // Auto-select Code on startup
    }

    private void setupButtonStyles() {
        String selectedStyle = "-fx-background-color: #0e639c; -fx-text-fill: white;";
        String normalStyle = "-fx-background-color: #2d2d30; -fx-text-fill: white;";

        codeButton.setStyle(selectedStyle);
        saveButton.setStyle(normalStyle);
        docButton.setStyle(normalStyle);
        helpButton.setStyle(normalStyle);
        backButton.setStyle(normalStyle);
    }

    private void setupButtonActions() {
        codeButton.setOnAction(e -> {
            selectCodeButton();
            mainWindow.focusCodeEditor();
        });

        saveButton.setOnAction(e -> {
            resetButtonStyles();
            saveButton.setStyle("-fx-background-color: #2d2d30; -fx-text-fill: #0e639c;");
            mainWindow.showSaveDialog();
        });

        docButton.setOnAction(e -> {
            resetButtonStyles();
            docButton.setStyle("-fx-background-color: #2d2d30; -fx-text-fill: #0e639c;");
            mainWindow.showDocDialog();
        });

        helpButton.setOnAction(e -> {
            resetButtonStyles();
            helpButton.setStyle("-fx-background-color: #2d2d30; -fx-text-fill: #0e639c;");
            mainWindow.showHelpDialog();
        });

        backButton.setOnAction(e -> {
            mainWindow.navigateToWelcome();
        });
    }

    public void selectCodeButton() {
        resetButtonStyles();
        codeButton.setSelected(true);
        codeButton.setStyle("-fx-background-color: #0e639c; -fx-text-fill: white;");
    }

    public void returnToCodeView() {
        selectCodeButton();
    }

    private void resetButtonStyles() {
        String normalStyle = "-fx-background-color: #2d2d30; -fx-text-fill: white;";
        codeButton.setStyle(normalStyle);
        saveButton.setStyle(normalStyle);
        docButton.setStyle(normalStyle);
        helpButton.setStyle(normalStyle);

        codeButton.setSelected(false);
    }
}