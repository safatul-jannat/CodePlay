package com.example.codeplayfx.ui.mainwindow.panel;

import com.example.codeplayfx.ui.mainwindow.PopupManager;
import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;

public class ToolPanel extends UIPanel {

    private Button saveButton;
    private Button saveAsButton;
    private Button docButton;
    private Button helpButton;
    private MainWindowScreen mainWindow;

    public ToolPanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        saveButton = (Button) loader.getNamespace().get("saveButton");
        saveAsButton = (Button) loader.getNamespace().get("saveAsButton");
        docButton = (Button) loader.getNamespace().get("docButton");
        helpButton = (Button) loader.getNamespace().get("helpButton");
    }

    @Override
    public void initialize() {
        setupButtonActions();
    }

    private void setupButtonActions() {
        if (saveButton != null) {
            saveButton.setOnAction(e -> {
                // Use quickSave logic directly
                mainWindow.quickSave();
            });
        }

        if (saveAsButton != null) {
            saveAsButton.setOnAction(e -> {
                mainWindow.saveAs();
            });
        }

        if (docButton != null) {
            docButton.setOnAction(e -> {
                System.out.println("Doc button clicked - you can add doc popup here");
            });
        }

        if (helpButton != null) {
            helpButton.setOnAction(e -> {
                System.out.println("Help button clicked - you can add help popup here");
            });
        }
    }

    public Button getSaveButton() { return saveButton; }
    public Button getSaveAsButton() {
        return saveAsButton;
    }
    public Button getDocButton() { return docButton; }
    public Button getHelpButton() { return helpButton; }
}