package com.example.codeplayfx;

import com.example.codeplayfx.ui.screen.ScreenManager;
import com.example.codeplayfx.ui.screen.WelcomeScreen;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        System.out.println("=== CODE PLAY IDE STARTING ===");

        try {
            // Set up stage properties
            primaryStage.setTitle("CodePlay Game IDE");
            primaryStage.setWidth(1200);
            primaryStage.setHeight(800);

            // Make sure window is visible
            primaryStage.setAlwaysOnTop(true); // Temporary to force visibility
            primaryStage.setOpacity(1.0);

            // Initialize ScreenManager
            ScreenManager.init(primaryStage);

            // Create and show WelcomeScreen
            WelcomeScreen welcomeScreen = new WelcomeScreen(primaryStage);
            ScreenManager.show(welcomeScreen);

            // Turn off always on top after showing
            primaryStage.setAlwaysOnTop(false);

            System.out.println("=== APPLICATION STARTED ===");

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();

            // Show error window
            javafx.scene.control.Label label = new javafx.scene.control.Label(
                    "CodePlay IDE\n\nCheck if window is minimized or behind other windows."
            );
            javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(label);
            javafx.scene.Scene scene = new javafx.scene.Scene(root, 400, 200);
            primaryStage.setScene(scene);
            primaryStage.setTitle("CodePlay");
            primaryStage.setAlwaysOnTop(true);
            primaryStage.show();
        }
    }

    @Override
    public void stop() {
        System.out.println("Application stopping...");
    }

    public static void main(String[] args) {
        System.out.println("Starting CodePlay IDE...");
        launch(args);
    }
}