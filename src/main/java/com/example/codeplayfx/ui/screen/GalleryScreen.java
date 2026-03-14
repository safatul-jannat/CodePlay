package com.example.codeplayfx.ui.screen;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class GalleryScreen extends Screen {

    public GalleryScreen(Stage stage) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/Gallery.fxml")
            );
            root = loader.load();
            scene = new Scene(root, 1100, 700);

            // Load CSS
            String css = getClass().getResource("/com/example/codeplayfx/css/screen-styles.css").toExternalForm();
            scene.getStylesheets().add(css);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShow() {
        System.out.println("Gallery Screen Loaded");
    }
}