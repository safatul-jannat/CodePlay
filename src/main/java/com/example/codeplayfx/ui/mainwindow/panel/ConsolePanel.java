package com.example.codeplayfx.ui.mainwindow.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class ConsolePanel extends UIPanel {

    private TextArea console;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");
    private Label consoleLabel;
    public ConsolePanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        console = (TextArea) loader.getNamespace().get("console");
        consoleLabel = (Label) loader.getNamespace().get("consoleLabel");
        VBox consoleContainer = (VBox) loader.getNamespace().get("consoleContainer");
        this.root = consoleContainer;
    }

    @Override
    public void initialize() {
        console.setEditable(false);
        console.setWrapText(true);
        console.setStyle(
                "-fx-control-inner-background: #1e1e1e; " +
                        "-fx-text-fill: #d4d4d4; " +
                        "-fx-font-family: 'Consolas', 'Monaco', 'Courier New', monospace; " +
                        "-fx-font-size: 12px;"
        );
        log("Console initialized. Ready for output.");
    }

    /**
     * Log info message (normal)
     */
    public void log(String message) {
        appendMessage("INFO", message, false);
    }

    /**
     * Log error message (red)
     */
    public void error(String message) {
        appendMessage("ERROR", message, true);
    }

    /**
     * Log warning message
     */
    public void warning(String message) {
        appendMessage("WARN", message, false);
    }

    /**
     * Log success message
     */
    public void success(String message) {
        appendMessage("SUCCESS", message, false);
    }

    /**
     * Log debug message (gray)
     */
    public void debug(String message) {
        appendMessage("DEBUG", message, false);
    }

    /**
     * Append formatted message to console
     */
    private void appendMessage(String level, String message, boolean isError) {
        Platform.runLater(() -> {
            if (console != null) {
                String timestamp = LocalTime.now().format(TIME_FORMAT);
                String formattedMessage;

                if (isError) {
                    formattedMessage = String.format("[%s] ❌ %s: %s\n",
                            timestamp, level, message);
                } else if (level.equals("SUCCESS")) {
                    formattedMessage = String.format("[%s] ✓ %s\n",
                            timestamp, message);
                } else {
                    formattedMessage = String.format("[%s] [%s] %s\n",
                            timestamp, level, message);
                }

                console.appendText(formattedMessage);
                console.positionCaret(console.getText().length());
            }
        });
    }

    /**
     * Log compilation error with details
     */
    public void compilationError(String error) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("\n");
                console.appendText("═══════════════════════════════════════\n");
                console.appendText("  COMPILATION ERROR\n");
                console.appendText("═══════════════════════════════════════\n");
                console.appendText(error);
                console.appendText("\n═══════════════════════════════════════\n\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    /**
     * Log runtime error
     */
    public void runtimeError(String error) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("\n");
                console.appendText("═══════════════════════════════════════\n");
                console.appendText("  RUNTIME ERROR\n");
                console.appendText("═══════════════════════════════════════\n");
                console.appendText(error);
                console.appendText("\n═══════════════════════════════════════\n\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    /**
     * Log safety violation
     */
    public void securityError(String message) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("\n");
                console.appendText("═══════════════════════════════════════\n");
                console.appendText("  🔒 SECURITY VIOLATION\n");
                console.appendText("═══════════════════════════════════════\n");
                console.appendText(message);
                console.appendText("\n═══════════════════════════════════════\n\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    /**
     * Clear console
     */
    public void clear() {
        Platform.runLater(() -> {
            if (console != null) {
                console.clear();
            }
        });
    }

    /**
     * Print separator line
     */
    public void separator() {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("───────────────────────────────────────\n");
            }
        });
    }
    public void logInfo(String message) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("ℹ️ " + message + "\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    public void logSuccess(String message) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("✅ " + message + "\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    public void logWarning(String message) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("⚠️ " + message + "\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    public void logError(String message) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("❌ " + message + "\n");
                console.positionCaret(console.getText().length());
            }
        });
    }

    /**
     * Print user's System.out.println() output
     */
    public void userOutput(String output) {
        Platform.runLater(() -> {
            if (console != null) {
                console.appendText("▶ " + output + "\n");
                console.positionCaret(console.getText().length());
            }
        });
    }
    public Pane getRoot() {
        return (Pane) root;
    }
    public Label getConsoleLabel() { return consoleLabel; }
}