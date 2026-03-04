package com.example.codeplayfx.ui.screen;

import com.example.codeplayfx.ui.controller.AboutController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class AboutScreen extends Screen {

    private AboutController controller;
    private final Stage stage;

    public AboutScreen(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/About.fxml")
            );
            root = loader.load();
            controller = loader.getController();

            System.out.println("✅ AboutScreen created");
        } catch (Exception e) {
            System.err.println("❌ Failed to load AboutScreen: " + e.getMessage());
            e.printStackTrace();
            // Fallback
            root = new javafx.scene.layout.StackPane(
                    new javafx.scene.control.Label("About Screen - Failed to Load")
            );
        }
    }

    public AboutController getController() {
        return controller;
    }

    @Override
    public void onShow() {
        System.out.println("About screen shown");
        // Any refresh logic here if needed
    }
}