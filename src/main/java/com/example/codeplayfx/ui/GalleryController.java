package com.example.codeplayfx.ui;

import com.example.codeplayfx.ui.screen.ScreenManager;
import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class GalleryController {

    public void goBack(ActionEvent event) {
        // This matches your FXML: onAction="#goBack"
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

        // You need to add "New Project" button to your Gallery.fxml
        // For now, just go back to welcome
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/welcomescreen.fxml")
            );
            javafx.scene.Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}