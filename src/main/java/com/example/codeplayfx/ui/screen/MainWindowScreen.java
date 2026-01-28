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
                    getClass().getResource("/fxml/MainWindow.fxml")
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

    // ===== NAVIGATION METHODS (called by panels) =====
    public void navigateToWelcome() {
        ScreenManager.show(new WelcomeScreen(stage));
    }

    public void showSaveDialog() {
        System.out.println("Save dialog (to implement)");
        toolPanel.returnToCodeView(); // Return focus to code
    }

    public void showDocDialog() {
        System.out.println("Documentation dialog (to implement)");
        toolPanel.returnToCodeView();
    }

    public void showHelpDialog() {
        System.out.println("Help dialog (to implement)");
        toolPanel.returnToCodeView();
    }

    public void focusCodeEditor() {
        codePanel.focusEditor();
    }

    // ===== TOP NAV METHODS =====
    public void onCreateTabClicked() {
        System.out.println("Create tab - already here");
    }

    public void onGalleryTabClicked() {
        System.out.println("Gallery tab (to implement)");
    }

    public void onLibraryTabClicked() {
        System.out.println("Library tab (to implement)");
    }

    public void onAboutTabClicked() {
        System.out.println("About tab (to implement)");
    }

    public void onHomeTabClicked() {
        navigateToWelcome();
    }
}