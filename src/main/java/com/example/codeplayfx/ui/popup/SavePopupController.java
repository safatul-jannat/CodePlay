package com.example.codeplayfx.ui.popup;

import com.example.codeplayfx.manager.GalleryManager;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.stage.Popup;
import java.util.function.Consumer;

public class SavePopupController {

    @FXML private TextField projectNameField;
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Popup popup;
    private String codeToSave;
    private String documentationToSave;
    private String currentNameForSaveAs;
    private Consumer<String> onSaveSuccess;

    @FXML
    public void initialize() {
        System.out.println("SavePopupController initialized");

        if (projectNameField != null) {
            projectNameField.requestFocus();

            projectNameField.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.ENTER) {
                    handleSave();
                }
            });
        }

        if (saveButton != null) {
            saveButton.setOnAction(event -> handleSave());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(event -> handleCancel());
        }
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    public void setCodeToSave(String code) {
        this.codeToSave = code;
    }

    public void setDocumentationToSave(String documentation) {
        this.documentationToSave = documentation;
    }

    public void setCurrentName(String name) {
        this.currentNameForSaveAs = name;
        if (name != null && projectNameField != null) {
            projectNameField.setText(name + "_copy");
        }
    }

    public void setOnSaveSuccess(Consumer<String> callback) {
        this.onSaveSuccess = callback;
    }

    @FXML
    private void handleSave() {
        String projectName = projectNameField.getText().trim();

        if (projectName.isEmpty()) {
            if (errorLabel != null) {
                errorLabel.setText("Project name cannot be empty!");
            }
            return;
        }

        // Call the callback to perform the actual save
        if (onSaveSuccess != null) {
            onSaveSuccess.accept(projectName);
        }

        // Close the popup
        if (popup != null) {
            popup.hide();
        }
    }

    @FXML
    private void handleCancel() {
        if (popup != null) {
            popup.hide();
        }
    }
}