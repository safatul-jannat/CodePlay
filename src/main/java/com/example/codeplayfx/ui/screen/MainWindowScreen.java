package com.example.codeplayfx.ui.screen;

import com.example.codeplayfx.ui.panel.*;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.ui.SynthwaveThemeApplier;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainWindowScreen extends Screen {

    private CodePanel codePanel;
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    private ToolPanel toolPanel;
    private TopNavigationPanel topNavPanel;
    private ConsolePanel consolePanel;
    private GameEngine gameEngine;
    private final Stage stage;

    public MainWindowScreen(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/MainWindow.fxml")
            );
            root = loader.load();

            codePanel = new CodePanel(loader, this);
            gamePanel = new GamePanel(loader, this);
            controlPanel = new ControlPanel(loader, this);
            toolPanel = new ToolPanel(loader, this);
            topNavPanel = new TopNavigationPanel(loader, this);
            consolePanel = new ConsolePanel(loader, this);

            gameEngine = gamePanel.getGameEngine();

            // Create scene with the root
            Scene scene = new Scene(root, 1400, 800);

            // Load CSS immediately
            loadCss(scene);

            // Set the scene on this screen's view
            ((Parent)root).getScene(); // This is just to ensure, but we'll store scene separately
            this.root = root; // Already set by Screen class

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadCss(Scene scene) {
        try {
            String cssPath = "/com/example/codeplayfx/css/synthwave-theme.css";
            String css = getClass().getResource(cssPath).toExternalForm();
            scene.getStylesheets().add(css);
            System.out.println("✅ CSS loaded: " + cssPath);
        } catch (Exception e) {
            System.err.println("❌ Failed to load CSS: " + e.getMessage());
        }
    }

    @Override
    public void onShow() {
        System.out.println("Main Window Loaded");

        codePanel.initialize();
        gamePanel.initialize();
        controlPanel.initialize();
        toolPanel.initialize();
        topNavPanel.initialize();
        consolePanel.initialize();

        // Apply Synthwave theme after scene is ready
        Platform.runLater(() -> {
            try {
                SynthwaveThemeApplier.apply(
                        getView().getScene(),  // Get scene from view
                        topNavPanel.getLogoLabel(),
                        codePanel.getCodeEditor(),
                        gamePanel.getCanvasContainer(),
                        controlPanel.getStatusLabel(),
                        controlPanel.getRunButton(),
                        controlPanel.getPauseButton(),
                        controlPanel.getStopButton(),
                        toolPanel.getSaveButton()
                );
                System.out.println("✅ Synthwave theme applied");
            } catch (Exception e) {
                System.err.println("❌ Failed to apply theme: " + e.getMessage());
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> {
            if (codePanel != null) {
                codePanel.setupKeyboardShortcuts();
            }
        });
    }

    // Getters
    public CodePanel getCodePanel() { return codePanel; }
    public GamePanel getGamePanel() { return gamePanel; }
    public ConsolePanel getConsolePanel() { return consolePanel; }
    public ControlPanel getControlPanel() { return controlPanel; }
    public ToolPanel getToolPanel() { return toolPanel; }
    public TopNavigationPanel getTopNavPanel() { return topNavPanel; }
    public GameEngine getGameEngine() { return gameEngine; }
    public Stage getStage() { return stage; }

    // Navigation methods
    public void navigateToWelcome() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/welcomescreen.fxml")
            );
            Parent root = loader.load();
            WelcomeScreen welcomeScreen = new WelcomeScreen(stage); // Adjust based on your WelcomeScreen constructor
            ScreenManager.show(welcomeScreen);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSaveDialog() { focusCodeEditor(); }
    public void showDocDialog() { focusCodeEditor(); }
    public void showHelpDialog() { focusCodeEditor(); }
    public void focusCodeEditor() { if (codePanel != null) codePanel.focusEditor(); }
    public void returnToCodeView() { focusCodeEditor(); }

    public void onCreateTabClicked() { focusCodeEditor(); }

    public void onGalleryTabClicked() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/Gallery.fxml")
            );
            javafx.scene.Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void onLibraryTabClicked() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/Library.fxml")
            );
            javafx.scene.Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onAboutTabClicked() {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/About.fxml")
            );
            javafx.scene.Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void onHomeTabClicked() { navigateToWelcome(); }
    public void setGameEngine(GameEngine gameEngine) { this.gameEngine = gameEngine; }
}