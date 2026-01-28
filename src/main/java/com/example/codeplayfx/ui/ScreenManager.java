package com.example.codeplayfx.ui;

import com.example.codeplayfx.ui.screen.Screen;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ScreenManager {
    private static Stage stage;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
    }

    public static void show(Screen screen) {
        if (stage == null) {
            throw new IllegalStateException("ScreenManager not initialized. Call init() first.");
        }

        Scene scene = new Scene(screen.getView());
        stage.setScene(scene);
        screen.onShow();
    }
}