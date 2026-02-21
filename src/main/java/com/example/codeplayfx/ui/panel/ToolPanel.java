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

        VBox toolContainer = (VBox) loader.getNamespace().get("toolContainer");
        this.root = toolContainer;
    }

    @Override
    public void initialize() {
        setupButtonActions();
    }

    private void setupButtonActions() {
        // Code button - focus editor
        codeButton.setOnAction(e -> mainWindow.focusCodeEditor());

        // Save button
        saveButton.setOnAction(e -> mainWindow.showSaveDialog());

        // Documentation button
        docButton.setOnAction(e -> mainWindow.showDocDialog());

        // Help button
        helpButton.setOnAction(e -> mainWindow.showHelpDialog());

        // Back button - go to welcome
        backButton.setOnAction(e -> mainWindow.navigateToWelcome());
    }

    public void returnToCodeView() {
        mainWindow.focusCodeEditor();
    }
    public Button getSaveButton() { return saveButton; }
}