package com.example.codeplayfx.ui.screen;

import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        System.out.println("ScreenManager initialized");
    }

    public static void show(Screen screen) {
        if (stage == null) {
            throw new IllegalStateException("ScreenManager not initialized");
        }

        System.out.println("Showing screen: " + screen.getClass().getSimpleName());

        // Make sure stage is visible
        Scene scene = new Scene(screen.getView(), 1200, 800);
        stage.setScene(scene);
        stage.show(); // This is important!
        stage.toFront(); // Bring to front

        screen.onShow();

        System.out.println("Screen displayed");
    }

    public static Stage getStage() {
        return stage;
    }
}