package com.example.codeplayfx.ui;

import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;

public class ControlPanel {
    private final VBox container;
    private final Button runButton;
    private final Button resetButton;
    private final Button clearButton;
    private final Button exampleButton;
    private final Label statusLabel;

    private final CodePanel codePanel;
    private final GamePanel gamePanel;

    public ControlPanel(CodePanel codePanel, GamePanel gamePanel) {
        this.codePanel = codePanel;
        this.gamePanel = gamePanel;

        // Create UI components
        this.runButton = createStyledButton("▶ Run Code", "#27ae60");
        this.resetButton = createStyledButton("↻ Reset Game", "#c0392b");
        this.clearButton = createStyledButton("✕ Clear Code", "#7f8c8d");
        this.exampleButton = createStyledButton("📋 Example", "#2980b9");
        this.statusLabel = createStatusLabel();
        this.container = createContainer();

        // Wire button events (ControlPanel handles its own logic!)
        wireEvents();

        System.out.println("✓ ControlPanel initialized");
    }

    private Button createStyledButton(String text, String color) {
        Button button = new Button(text);
        button.setStyle(
                "-fx-background-color: " + color + "; " +
                        "-fx-text-fill: white; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 8px 15px; " +
                        "-fx-background-radius: 5px; " +
                        "-fx-cursor: hand; " +
                        "-fx-font-size: 14px;"
        );
        button.setPrefWidth(130);
        return button;
    }

    private Label createStatusLabel() {
        Label label = new Label("Ready to execute code");
        label.setStyle(
                "-fx-font-size: 13px; " +
                        "-fx-text-fill: #2c3e50; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 5px;"
        );
        return label;
    }

    private VBox createContainer() {
        HBox buttonRow1 = new HBox(10, runButton, resetButton);
        HBox buttonRow2 = new HBox(10, clearButton, exampleButton);

        Separator separator = new Separator();
        separator.setStyle("-fx-padding: 5px 0;");

        VBox panel = new VBox(8, buttonRow1, buttonRow2, separator, statusLabel);
        panel.setStyle(
                "-fx-padding: 15px; " +
                        "-fx-background-color: #f8f9fa; " +
                        "-fx-border-color: #dee2e6; " +
                        "-fx-border-width: 1px; " +
                        "-fx-border-radius: 5px;"
        );
        return panel;
    }

    private void wireEvents() {
        // Run button - executes code and updates game
        runButton.setOnAction(e -> {
            System.out.println("▶ Run button clicked");
            String result = codePanel.executeCode(gamePanel.getGameWorld());
            updateStatus(result);
            gamePanel.refresh();
        });

        // Reset button - clears game objects
        resetButton.setOnAction(e -> {
            System.out.println("↻ Reset button clicked");
            gamePanel.reset();
            updateStatus("Game reset");
        });

        // Clear button - clears code editor
        clearButton.setOnAction(e -> {
            System.out.println("✕ Clear button clicked");
            codePanel.clearCode();
            updateStatus("Code cleared");
        });

        // Example button - loads example code
        exampleButton.setOnAction(e -> {
            System.out.println("📋 Example button clicked");
            codePanel.loadExampleCode();
            updateStatus("Example code loaded");
        });
    }

    public void updateStatus(String message) {
        statusLabel.setText(message);
    }

    public VBox getView() {
        return container;
    }
}