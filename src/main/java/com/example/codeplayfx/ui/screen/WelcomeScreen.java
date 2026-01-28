package com.example.codeplayfx.ui.screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class WelcomeScreen extends Screen {

    public WelcomeScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/welcomescreen.fxml")
            );
            root = loader.load();
        } catch (Exception e) {
            e.printStackTrace();
            // Fallback
            root = new javafx.scene.layout.StackPane(
                    new javafx.scene.control.Label("Welcome Screen")
            );
        }
    }

    @Override
    public void onShow() {
        System.out.println("Welcome screen shown");
    }
}