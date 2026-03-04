package com.example.codeplayfx.ui.popup;

import com.example.codeplayfx.manager.GalleryManager;
import com.example.codeplayfx.model.Project;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Popup;

import java.util.function.Consumer;

public class SavePopupController {

    @FXML
    private TextField projectNameField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;

    @FXML
    private Label errorLabel;

    private Popup popup;
    private String codeToSave;
    private Consumer<String> onSaveSuccess;

    @FXML
    public void initialize() {
        errorLabel.setVisible(false);
        errorLabel.setStyle("-fx-text-fill: #ff6b6b;");

        // Enable/disable save button based on input
        projectNameField.textProperty().addListener((obs, old, newVal) -> {
            saveButton.setDisable(newVal.trim().isEmpty());
            errorLabel.setVisible(false);
        });

        // Enter key triggers save
        projectNameField.setOnAction(e -> handleSave());
    }

    @FXML
    private void handleSave() {
        String projectName = projectNameField.getText().trim();

        if (projectName.isEmpty()) {
            errorLabel.setText("Please enter a project name");
            errorLabel.setVisible(true);
            return;
        }

        // Check if project already exists
        GalleryManager gallery = GalleryManager.getInstance();
        Project existing = gallery.findProjectByName(projectName);

        if (existing != null) {
            // Simple confirmation - in real app you'd show a dialog
            errorLabel.setText("Project exists! Will overwrite.");
            errorLabel.setStyle("-fx-text-fill: #ffaa00;");
            errorLabel.setVisible(true);
        }

        // Save project
        gallery.saveProject(projectName, codeToSave);

        // Call success callback
        if (onSaveSuccess != null) {
            onSaveSuccess.accept(projectName);
        }

        // Close popup
        popup.hide();
    }

    @FXML
    private void handleCancel() {
        popup.hide();
    }

    public void setPopup(Popup popup) {
        this.popup = popup;
    }

    public void setCodeToSave(String code) {
        this.codeToSave = code;
    }

    public void setOnSaveSuccess(Consumer<String>  callback) {
        this.onSaveSuccess = callback;
    }
}