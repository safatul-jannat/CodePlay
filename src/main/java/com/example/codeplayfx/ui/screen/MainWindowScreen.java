package com.example.codeplayfx.ui.screen;

import com.example.codeplayfx.manager.GalleryManager;
import com.example.codeplayfx.model.Project;
import com.example.codeplayfx.ui.mainwindow.PopupManager;
import com.example.codeplayfx.ui.mainwindow.panel.*;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.ui.mainwindow.SynthwaveThemeApplier;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
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


    private String savedCodeContent = "";
    private boolean isFirstShow = true;
    private Label projectTitleLabel;
    private String currentProjectName = "untitled";
    private boolean isNewProject = true;

    public MainWindowScreen(Stage stage) {
        this.stage = stage;

        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/codeplayfx/MainWindow.fxml")
            );
            root = loader.load();

            projectTitleLabel = (Label) loader.getNamespace().get("projectTitleLabel");
            codePanel = new CodePanel(loader, this);
            gamePanel = new GamePanel(loader, this);
            controlPanel = new ControlPanel(loader, this);
            toolPanel = new ToolPanel(loader, this);
            topNavPanel = new TopNavigationPanel(loader, this);
            consolePanel = new ConsolePanel(loader, this);

            gameEngine = gamePanel.getGameEngine();

            // Create scene with the root
             scene = new Scene(root, 1400, 800);

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
            System.out.println(" CSS loaded: " + cssPath);
        } catch (Exception e) {
            System.err.println(" Failed to load CSS: " + e.getMessage());
        }
    }

    @Override
    public void onShow() {
        System.out.println("Main Window Loaded");

        updateProjectTitle();
        setupKeyboardShortcuts();
        codePanel.initialize(isFirstShow);

        codePanel.initialize();
        gamePanel.initialize();
        controlPanel.initialize();
        toolPanel.initialize();
        topNavPanel.initialize();
        consolePanel.initialize();
        codePanel.focusEditor();


        if (!isFirstShow && !savedCodeContent.isEmpty()) {
            codePanel.setCodeContent(savedCodeContent);
            System.out.println(" Restored saved code content (" + savedCodeContent.length() + " chars)");
        }

        isFirstShow = false;

        // Apply Synthwave theme after scene is ready
        Platform.runLater(() -> {
            try {
                SynthwaveThemeApplier.apply(
                        scene,  // Get scene from view
                        topNavPanel.getLogoLabel(),
                        codePanel.getCodeEditor(),
                        gamePanel.getCanvasContainer(),
                        controlPanel.getStatusLabel(),
                        controlPanel.getRunButton(),
                        controlPanel.getPauseButton(),
                        controlPanel.getStopButton(),
                        toolPanel.getSaveButton()
                );
                System.out.println("Synthwave theme applied");
            } catch (Exception e) {
                System.err.println(" Failed to apply theme: " + e.getMessage());
                e.printStackTrace();
            }
        });

        Platform.runLater(() -> {
            if (codePanel != null) {
                codePanel.setupKeyboardShortcuts();
            }

            if (gamePanel != null && gamePanel.getGameEngine() != null) {
                gamePanel.getGameEngine().attachInputToScene(getScene());
            }
        });
    }

    public void onHide() {
        if (codePanel != null) {
            savedCodeContent = codePanel.getCodeContent();
            System.out.println(" Saved code content before leaving (" + savedCodeContent.length() + " chars)");
        }
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

//    // Navigation methods
//    public void navigateToWelcome() {
//        try {
//            ScreenManager.pushCurrentToHistory(this);
//            ScreenManager.goToWelcome();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    public void showSaveDialog() { focusCodeEditor(); }
    public void showDocDialog() {  System.out.println("Showing documentation popup");

        String currentCode = codePanel.getCode();
        String projectName = currentProjectName;

        // Get existing documentation if any
        String existingDoc = "";
        Project existingProject = GalleryManager.getInstance().findProjectByName(projectName);
        if (existingProject != null) {
            existingDoc = existingProject.getDocumentation();
        }

        // Use the doc button as anchor
        Button docButton = toolPanel.getDocButton();

        PopupManager.showDocPopup(
                docButton,
                stage,
                projectName,
                currentCode,
                existingDoc,
                () -> {
                    // Success callback - maybe show a temporary message
                    System.out.println("Documentation saved successfully");
                    showTemporaryMessage("Documentation saved");
                }
        );

        focusCodeEditor();  }
    public void showHelpDialog() { System.out.println("Showing help popup");

        Button helpButton = toolPanel.getHelpButton();

        PopupManager.showHelpPopup(
                helpButton,
                stage
        );

        focusCodeEditor();  }
    public void focusCodeEditor() { if (codePanel != null) codePanel.focusEditor(); }
    public void returnToCodeView() { focusCodeEditor(); }

    public void onCreateTabClicked() {
        // If game is running, don't switch focus to editor
        if (controlPanel != null && controlPanel.isRunning()) {
            focusGame();
        } else {
            focusCodeEditor();
        }
    }

    private void focusGame() {
        if (gamePanel != null) {
            gamePanel.getGameCanvas().requestFocus();
        }
    }
    public void onGalleryTabClicked() {
        System.out.println("MainWindow: Gallery button clicked");
        onHide();

        ScreenManager.goToGallery();
    }

    public void onLibraryTabClicked() {
        System.out.println("MainWindow: Library button clicked");
        onHide();

        ScreenManager.goToLibrary(); // Smart method handles caching
    }

    public void onAboutTabClicked() {
        System.out.println("MainWindow: About button clicked");
        onHide();
        ScreenManager.goToAbout();
    }


    public void onHomeTabClicked() {
        System.out.println("MainWindow: Home button clicked");
        onHide();
        ScreenManager.goToWelcome();
    }
    public void setGameEngine(GameEngine gameEngine) { this.gameEngine = gameEngine; }

    // Add this method to load a project from gallery
    public void loadProject(Project project) {
        if (codePanel != null) {
            codePanel.setCodeContent(project.getCode());
            this.currentProjectName = project.getName();
            this.isNewProject = false;
            updateProjectTitle();
            System.out.println("Loaded project: " + project.getName());
        }
    }

    public void setCurrentProjectName(String name) {
        this.currentProjectName = name;
        updateProjectTitle();
    }
    private void updateProjectTitle() {
        // Update window title
        if (stage != null) {
            stage.setTitle("CodePlay - " + currentProjectName);
        }

        // If you have a label in your UI showing project name, update it
        if (projectTitleLabel != null) {
            projectTitleLabel.setText(currentProjectName);
        }

        System.out.println("Project title updated to: " + currentProjectName);
    }
    public String getCurrentProjectName() {
        return currentProjectName;
    }
    public void setProjectTitleLabel(Label label) {
        this.projectTitleLabel = label;
        updateProjectTitle();
    }

    private void setupKeyboardShortcuts() {
        Scene scene = getScene();
        if (scene != null) {
            scene.getAccelerators().put(
                    new KeyCodeCombination(KeyCode.S, KeyCombination.CONTROL_DOWN),
                    this::quickSave
            );

            // DON'T add accelerator for SPACE - let game handle it
            // Remove any existing space accelerators
            scene.getAccelerators().remove(new KeyCodeCombination(KeyCode.SPACE));
        }
    }

    /**
     * Create a new project - clears editor and resets to untitled
     */
    public void newProject() {
        // Clear code editor with default template
        String defaultCode = "// Welcome to CodePlayFX!\n// Start coding here...\n\npublic class Main {\n    public static void main(String[] args) {\n        System.out.println(\"Hello World!\");\n    }\n}";
        codePanel.setCodeContent(defaultCode);

        // Clear documentation with default text
        String defaultDoc = "// Project Documentation\n// Describe your project here...\n\n## Overview\n## Features\n## Usage";

        // Update project name to untitled
        currentProjectName = "untitled";
        isNewProject = true;
        updateProjectTitle();

        showTemporaryMessage("New project created - 'untitled'");
        System.out.println("📄 New project created, editor cleared, name reset to 'untitled'");
    }
    /**
     * Quick save - if project has a name, save directly without popup
     * If it's untitled, show save popup
     */
    public void quickSave() {
        System.out.println("Quick save triggered: " + currentProjectName);

        if (!isNewProject && !"untitled".equals(currentProjectName) &&
                !"untitled.game".equals(currentProjectName)) {
            // Already has a name - save directly
            String code = codePanel.getCode();
            GalleryManager.getInstance().saveProject(currentProjectName, code);
            System.out.println(" Quick saved: " + currentProjectName);
            showTemporaryMessage("Saved: " + currentProjectName);
        } else {
            // New/untitled project - show save popup
            showSavePopup(false); // false = normal save (not Save As)
        }
    }
    public void showSavePopup(boolean isSaveAs) {
        String currentCode = codePanel.getCode();
        String currentDoc = ""; // Get documentation if you have it
        Button anchorButton = isSaveAs ? toolPanel.getSaveAsButton() : toolPanel.getSaveButton();

        PopupManager.showSavePopup(
                anchorButton,
                stage,
                currentCode,
                currentDoc,
                isSaveAs ? currentProjectName : null,  // Pass current name for Save As
                (savedProjectName) -> {
                    // ACTUALLY SAVE the project here
                    GalleryManager.getInstance().saveProject(savedProjectName, currentCode, currentDoc);

                    this.currentProjectName = savedProjectName;
                    this.isNewProject = false;
                    updateProjectTitle();
                    showTemporaryMessage((isSaveAs ? "Saved as: " : "Saved: ") + savedProjectName);
                }
        );
    }
    /**
     * Save As - always show popup to choose new name
     */
    public void saveAs() {
        showSavePopup(true); // true = Save As mode
    }

    private void showTemporaryMessage(String message) {
        // You could show a toast or status message
        System.out.println("ℹ" + message);
        // TODO: Add a status bar or temporary popup
    }


}