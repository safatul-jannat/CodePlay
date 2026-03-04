package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.compiler.GameCompiler;
import com.example.codeplayfx.compiler.CompilationException;
import com.example.codeplayfx.scripting.runtime.UserGame;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class ControlPanel extends UIPanel {

    private MainWindowScreen mainWindow;

    private Button runButton;
    private Button pauseButton;
    private Button stopButton;
    private Label statusLabel;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private boolean codeModified = false;

    public ControlPanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        runButton = (Button) loader.getNamespace().get("runButton");
        pauseButton = (Button) loader.getNamespace().get("pauseButton");
        stopButton = (Button) loader.getNamespace().get("stopButton");
        statusLabel = (Label) loader.getNamespace().get("statusLabel");

        HBox controlBar = (HBox) loader.getNamespace().get("controlToolbar");
        this.root = controlBar;
    }

    @Override
    public void initialize() {
        setupButtonActions();
        updateButtonStates();
        updateStatus("Ready - Click Run to start");
    }

    private void setupButtonActions() {
        runButton.setOnAction(e -> onRunClicked());
        pauseButton.setOnAction(e -> onPauseClicked());
        stopButton.setOnAction(e -> onStopClicked());
    }

    private void onRunClicked() {
        GameEngine engine = mainWindow.getGamePanel().getGameEngine();
        String code = mainWindow.getCodePanel().getCode();

        if (engine == null) {
            updateStatus("Error: Game engine not initialized");
            mainWindow.getConsolePanel().error("Game engine not initialized");
            return;
        }

        if (!isRunning) {
            // First time running - compile and start
            startGame(engine, code);

        } else if (isPaused) {
            // Resuming from pause
            engine.resume();
            isPaused = false;
            updateButtonStates();
            updateStatus("Game running...");
            mainWindow.getConsolePanel().log("Game resumed");

        } else if (codeModified) {
            // Code changed while running - recompile and restart
            engine.getGameWorld().clear();
            engine.stop();
            mainWindow.getGamePanel().clearGameObjects();
            startGame(engine, code);
        }
    }

    /**
     * Compile user code and start the game
     */
    private void startGame(GameEngine engine, String code) {
        try {
            engine.getGameWorld().clear();
            engine.stop();
            // Clear console before compilation
            mainWindow.getConsolePanel().clear();
            mainWindow.getConsolePanel().log("Compiling user code...");

            // Compile user code
            UserGame userGame = GameCompiler.compile(code);

            mainWindow.getConsolePanel().log("✓ Compilation successful");
            mainWindow.getConsolePanel().log("Starting game...");

            // Load user game into engine
            engine.loadUserGame(userGame);

            // Start engine
            engine.start();

            isRunning = true;
            isPaused = false;
            codeModified = false;

            updateButtonStates();
            updateStatus("Game running...");
            mainWindow.getConsolePanel().log("✓ Game started successfully");

        } catch (CompilationException e) {
            // Compilation failed - show error in console
            updateStatus("Compilation failed");
            mainWindow.getConsolePanel().error("Compilation failed:");
            mainWindow.getConsolePanel().error(e.getMessage());

            isRunning = false;
            isPaused = false;
            updateButtonStates();

        } catch (Exception e) {
            // Unexpected error
            updateStatus("Error: " + e.getMessage());
            mainWindow.getConsolePanel().error("Unexpected error:");
            mainWindow.getConsolePanel().error(e.getMessage());
            e.printStackTrace();

            isRunning = false;
            isPaused = false;
            updateButtonStates();
        }
    }

    private void onPauseClicked() {
        GameEngine engine = mainWindow.getGamePanel().getGameEngine();

        if (isRunning && !isPaused) {
            engine.pause();
            isPaused = true;
            updateButtonStates();
            updateStatus("Game paused");
            mainWindow.getConsolePanel().log("Game paused");

        } else if (isPaused) {
            engine.resume();
            isPaused = false;
            updateButtonStates();
            updateStatus("Game running...");
            mainWindow.getConsolePanel().log("Game resumed");
        }
    }

    private void onStopClicked() {
        GameEngine engine = mainWindow.getGamePanel().getGameEngine();

        if (isRunning) {
            engine.stop();
            mainWindow.getGamePanel().clearGameObjects();

            isRunning = false;
            isPaused = false;
            updateButtonStates();
            updateStatus("Game stopped");
            mainWindow.getConsolePanel().log("Game stopped");
        }
    }

    private void updateButtonStates() {
        runButton.setDisable(false);
        pauseButton.setDisable(!isRunning);
        stopButton.setDisable(!isRunning);

        if (!isRunning) {
            runButton.setText("▶ Run");
            pauseButton.setText("⏸ Pause");
        } else if (isPaused) {
            runButton.setText("▶ Resume");
            pauseButton.setText("⏸ Paused");
        } else {
            runButton.setText("↻ Restart");
            pauseButton.setText("⏸ Pause");
        }
    }

    private void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText("Status: " + message);
        }
    }

    public void setCodeModified(boolean modified) {
        codeModified = modified;
        if (modified && isRunning && !isPaused) {
            updateStatus("Code modified - click Run to apply changes");
        }
    }

    public boolean isCodeModified() {
        return codeModified;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public boolean isPaused() {
        return isPaused;
    }
    public Button getRunButton() { return runButton; }
    public Button getPauseButton() { return pauseButton; }
    public Button getStopButton() { return stopButton; }
    public Label getStatusLabel() { return statusLabel; }
}