package com.example.codeplayfx.ui.popup;

import com.example.codeplayfx.manager.GalleryManager;
import com.example.codeplayfx.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Popup;

public class DocPopupController {

    @FXML private Label projectNameLabel;
    @FXML private TextArea docTextArea;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Popup popup;
    private String projectName;
    private String currentCode;
    private Runnable onSaveSuccess;

    @FXML
    public void initialize() {
        System.out.println("DocPopupController initialized");
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    public void setProjectData(String projectName, String code, String existingDoc) {
        this.projectName = projectName;
        this.currentCode = code;

        if (projectNameLabel != null) {
            projectNameLabel.setText(projectName);
        }

        if (docTextArea != null) {
            if (existingDoc != null && !existingDoc.isEmpty()) {
                docTextArea.setText(existingDoc);
                System.out.println("Loaded existing documentation (" + existingDoc.length() + " chars)");
            } else {
                System.out.println("No existing documentation for: " + projectName);
            }
        }
    }

    public void setOnSaveSuccess(Runnable onSaveSuccess) {
        this.onSaveSuccess = onSaveSuccess;
    }

    @FXML
    private void handleCancel() {
        System.out.println("Documentation editing cancelled");
        if (popup != null) {
            popup.hide();
        }
    }

    @FXML
    private void handleSave() {
        String documentation = docTextArea.getText();

        System.out.println("Saving documentation for: " + projectName);
        System.out.println("Documentation length: " + documentation.length() + " chars");

        // Save both code and documentation together
        GalleryManager.getInstance().saveProject(projectName, currentCode, documentation);

        System.out.println(" Documentation saved successfully for project: " + projectName);

        if (onSaveSuccess != null) {
            onSaveSuccess.run();
        }

        if (popup != null) {
            popup.hide();
        }
    }
}