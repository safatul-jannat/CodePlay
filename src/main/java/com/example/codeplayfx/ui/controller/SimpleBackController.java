package com.example.codeplayfx.ui.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SimpleBackController {

    public void goBack(ActionEvent event) {
        // This matches your FXML: onAction="#goBack"
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource("/com/example/codeplayfx/welcomescreen.fxml")
            );
            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();
            stage.setScene(new Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}