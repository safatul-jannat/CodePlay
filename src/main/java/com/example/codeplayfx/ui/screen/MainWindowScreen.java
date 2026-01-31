package com.example.codeplayfx.ui.screen;

import com.example.codeplayfx.ui.panel.*;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.engine.core.GridConfig;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.stage.Stage;

public class MainWindowScreen extends Screen {

    // Panels
    private CodePanel codePanel;
    private GamePanel gamePanel;
    private ControlPanel controlPanel;
    private ToolPanel toolPanel;
    private TopNavigationPanel topNavPanel;
    private ConsolePanel consolePanel;

    // Engine
    private GameEngine gameEngine;
    private GridConfig gridConfig;

    // Stage reference
    private Stage stage;

    public MainWindowScreen(Stage stage) {
        this.stage = stage;
        this.gridConfig = new GridConfig();

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/MainWindow.fxml")
            );
            root = loader.load();

            // Initialize GameEngine
            gameEngine = new GameEngine(gridConfig);

            // Initialize ALL panels with 'this' reference
            codePanel = new CodePanel(loader, this);
            gamePanel = new GamePanel(loader, this);
            controlPanel = new ControlPanel(loader, this);
            toolPanel = new ToolPanel(loader, this);
            topNavPanel = new TopNavigationPanel(loader, this);
            consolePanel = new ConsolePanel(loader, this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onShow() {
        System.out.println("Main Window Loaded");

        // Initialize all panels
        codePanel.initialize();
        gamePanel.initialize();
        controlPanel.initialize();
        toolPanel.initialize();
        topNavPanel.initialize();
        consolePanel.initialize();
    }

    // ===== GETTERS for panels to access =====
    public Stage getStage() { return stage; }
    public GameEngine getGameEngine() { return gameEngine; }
    public CodePanel getCodePanel() { return codePanel; }
    public GamePanel getGamePanel() { return gamePanel; }
    public ConsolePanel getConsolePanel() { return consolePanel; }
    public ControlPanel getControlPanel() { return controlPanel; }
    public ToolPanel getToolPanel() { return toolPanel; }
    public TopNavigationPanel getTopNavPanel() { return topNavPanel; }

    // ===== NAVIGATION METHODS (called by panels) =====

    // For backButton in FXML
    public void navigateToWelcome() {
        System.out.println("Back button clicked - navigating to Welcome");
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/welcomescreen.fxml")
            );
            javafx.scene.Parent root = loader.load();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // For saveButton in FXML
    public void showSaveDialog() {
        System.out.println("Save button clicked");
        focusCodeEditor();
    }

    // For docButton in FXML
    public void showDocDialog() {
        System.out.println("Documentation button clicked");
        focusCodeEditor();
    }

    // For helpButton in FXML
    public void showHelpDialog() {
        System.out.println("Help button clicked");
        focusCodeEditor();
    }

    // For codeButton in FXML (to focus editor)
    public void focusCodeEditor() {
        System.out.println("Focusing code editor");
        if (codePanel != null) {
            codePanel.focusEditor();
        }
    }

    // For ToolPanel class methods
    public void returnToCodeView() {
        System.out.println("Returning to code view");
        focusCodeEditor();
    }

    // ===== TOP NAV METHODS =====
    public void onCreateTabClicked() {
        System.out.println("Create tab clicked - already here");
    }

    public void onGalleryTabClicked() {
        System.out.println("Gallery tab clicked");
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
        System.out.println("Library tab clicked");
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
        System.out.println("About tab clicked");
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

    public void onHomeTabClicked() {
        System.out.println("Home tab clicked");
        navigateToWelcome();
    }
}