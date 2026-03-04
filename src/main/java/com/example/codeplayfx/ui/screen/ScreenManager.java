package com.example.codeplayfx.ui.screen;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
    private static Stage stage;
    private static Scene currentScene;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        System.out.println("ScreenManager initialized");
    }

    public static void show(Screen screen) {
        if (stage == null) {
            throw new IllegalStateException("ScreenManager not initialized");
        }

        System.out.println("Showing screen: " + screen.getClass().getSimpleName());

        // Check if this screen already has its own scene (like MainWindowScreen)
        if (screen.getView().getScene() != null) {
            // Screen already has a scene, just set it on the stage
            stage.setScene(screen.getView().getScene());
        } else {
            // Create new scene for screens without one
            Scene scene = new Scene(screen.getView(), 1200, 800);
            stage.setScene(scene);
        }

        stage.show();
        stage.toFront();

        screen.onShow();
        System.out.println("Screen displayed");
    }

    public static Stage getStage() {
        return stage;
    }

    public static Scene getCurrentScene() {
        return currentScene;
    }
}