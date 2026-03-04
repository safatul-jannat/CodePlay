package com.example.codeplayfx.ui.screen;

import com.example.codeplayfx.ui.controller.LibraryController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class LibraryScreen extends Screen {
    
    private LibraryController controller;
    private final Stage stage;

    public LibraryScreen(Stage stage) {
        this.stage = stage;
        
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/Library.fxml")
            );
            root = loader.load();
            controller = loader.getController();
            
            System.out.println("✅ LibraryScreen created");
        } catch (Exception e) {
            System.err.println("❌ Failed to load LibraryScreen: " + e.getMessage());
            e.printStackTrace();
            // Fallback
            root = new javafx.scene.layout.StackPane(
                    new javafx.scene.control.Label("Library Screen - Failed to Load")
            );
        }
    }

    public LibraryController getController() {
        return controller;
    }

    @Override
    public void onShow() {
        System.out.println("Library screen shown");
        // Any refresh logic here if needed
        if (controller != null) {
            // For example: refresh content, restart animations, etc.
        }
    }
}