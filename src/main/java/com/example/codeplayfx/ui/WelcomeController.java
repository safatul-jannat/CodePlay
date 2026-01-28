package com.example.codeplayfx.ui;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class WelcomeController {

    private void switchScene(ActionEvent event, String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(
                    getClass().getResource(fxmlPath)
            );

            Stage stage = (Stage) ((Node) event.getSource())
                    .getScene()
                    .getWindow();

            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openNew(ActionEvent event) {
        switchScene(event, "/com/example/codeplayfx/new.fxml");
    }

    public void openGallery(ActionEvent event) {
        switchScene(event, "/com/example/codeplayfx/Gallery.fxml");
    }

    public void openLibrary(ActionEvent event) {
        switchScene(event, "/com/example/codeplayfx/Library.fxml");
    }

    public void openAbout(ActionEvent event) {
        switchScene(event, "/com/example/codeplayfx/About.fxml");
    }
}

