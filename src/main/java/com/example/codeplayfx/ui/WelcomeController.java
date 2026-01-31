package com.example.codeplayfx.ui;

import com.example.codeplayfx.ui.screen.ScreenManager;
import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class WelcomeController {

    public void openNew(ActionEvent event) {
        System.out.println("WelcomeController: New button clicked!");

        try {
            // Get the current stage
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            System.out.println("Current stage: " + stage);

            // Create MainWindowScreen
            System.out.println("Creating MainWindowScreen...");
            MainWindowScreen mainWindow = new MainWindowScreen(stage);

            // Use ScreenManager to show it
            System.out.println("Calling ScreenManager.show()...");
            ScreenManager.show(mainWindow);

            System.out.println("Navigation to MainWindow completed!");

        } catch (Exception e) {
            System.err.println("ERROR in openNew: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void openGallery(ActionEvent event) {
        System.out.println("Opening Gallery...");
        switchToFXML(event, "/com/example/codeplayfx/Gallery.fxml");
    }

    public void openLibrary(ActionEvent event) {
        System.out.println("Opening Library...");
        switchToFXML(event, "/com/example/codeplayfx/Library.fxml");
    }

    public void openAbout(ActionEvent event) {
        System.out.println("Opening About...");
        switchToFXML(event, "/com/example/codeplayfx/About.fxml");
    }

    private void switchToFXML(ActionEvent event, String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
            System.out.println("Switched to: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("Error switching to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}