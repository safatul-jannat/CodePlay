package com.example.codeplayfx;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class test extends  Application {

    @Override
    public void start(Stage stage) {
        System.out.println("Testing FXML paths...");

        // Test 1: MainWindow.fxml
        System.out.println("\n1. Testing /fxml/MainWindow.fxml:");
        try {
            FXMLLoader loader = new FXMLLoader(
                    test.class.getResource("/fxml/MainWindow.fxml")
            );
            System.out.println("   URL: " + loader.getLocation());
            Object root = loader.load();
            System.out.println("   SUCCESS: Loaded " + root.getClass().getName());
        } catch (Exception e) {
            System.err.println("   ERROR: " + e.getMessage());
        }

        // Test 2: WelcomeScreen.fxml
        System.out.println("\n2. Testing /com/example/codeplayfx/welcomescreen.fxml:");
        try {
            FXMLLoader loader = new FXMLLoader(
                    test.class.getResource("/com/example/codeplayfx/welcomescreen.fxml")
            );
            System.out.println("   URL: " + loader.getLocation());
            Object root = loader.load();
            System.out.println("   SUCCESS: Loaded " + root.getClass().getName());
        } catch (Exception e) {
            System.err.println("   ERROR: " + e.getMessage());
        }

        // Show test window
        javafx.scene.control.Label label = new javafx.scene.control.Label("Check console for FXML test results");
        javafx.scene.layout.StackPane root = new javafx.scene.layout.StackPane(label);
        Scene scene = new Scene(root, 400, 200);
        stage.setScene(scene);
        stage.setTitle("FXML Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}