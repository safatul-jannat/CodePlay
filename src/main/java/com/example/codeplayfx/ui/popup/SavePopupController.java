package com.example.codeplayfx.ui.popup;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.function.Consumer;

public class SavePopupController {

    @FXML private TextField projectNameField;
    @FXML private Label errorLabel;
    @FXML private Button cancelButton;
    @FXML private Button saveButton;

    private Popup popup;  // আগে Popup ছিল
    private Stage popupStage;  // এখন Stage ও রাখছি (দুটোই support)
    private String codeToSave;
    private Consumer<String> onSaveSuccess;

    // Popup version (পুরনো)
    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    // Stage version (নতুন)
    public void setPopupStage(Stage stage) {
        this.popupStage = stage;
    }

    public void setCodeToSave(String code) {
        this.codeToSave = code;
    }

    public void setOnSaveSuccess(Consumer<String> callback) {
        this.onSaveSuccess = callback;
    }

    @FXML
    public void initialize() {
        // Focus on text field
        if (projectNameField != null) {
            projectNameField.requestFocus();

            // Enter key to save
            projectNameField.setOnKeyPressed(e -> {
                if (e.getCode() == KeyCode.ENTER) {
                    handleSave();
                }
            });
        }

        // Button actions
        if (saveButton != null) {
            saveButton.setOnAction(e -> handleSave());
        }

        if (cancelButton != null) {
            cancelButton.setOnAction(e -> handleCancel());
        }
    }

    @FXML
    private void handleSave() {
        String projectName = projectNameField.getText().trim();

        if (projectName.isEmpty()) {
            errorLabel.setText("Project name cannot be empty!");
            return;
        }

        if (onSaveSuccess != null) {
            onSaveSuccess.accept(projectName);
        }

        closePopup();
    }

    @FXML
    private void handleCancel() {
        closePopup();
    }

    private void closePopup() {
        if (popup != null) {
            popup.hide();
        }
        if (popupStage != null) {
            popupStage.close();
        }
    }
}