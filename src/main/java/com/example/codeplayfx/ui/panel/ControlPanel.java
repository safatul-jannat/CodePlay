package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import com.example.codeplayfx.engine.core.GameEngine;
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
        GameEngine engine = mainWindow.getGameEngine();
        String code = mainWindow.getCodePanel().getCode();

        if (!isRunning) {
            // First time running
            engine.start(code);
            isRunning = true;
            isPaused = false;
            codeModified = false;

            updateButtonStates();
            updateStatus("Game running...");
            mainWindow.getConsolePanel().log("Game started");

        } else if (isPaused) {
            // Resuming from pause
            engine.resume();
            isPaused = false;
            updateButtonStates();
            updateStatus("Game running...");
            mainWindow.getConsolePanel().log("Game resumed");

        } else if (codeModified) {
            // Code changed while running - restart
            engine.stop();
            engine.start(code);
            codeModified = false;
            updateStatus("Game restarted with new code");
            mainWindow.getConsolePanel().log("Game restarted");
        }
    }

    private void onPauseClicked() {
        GameEngine engine = mainWindow.getGameEngine();

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
        GameEngine engine = mainWindow.getGameEngine();

        if (isRunning) {
            engine.stop();
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
}