package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.VBox;
import java.util.Stack;

public class CodePanel extends UIPanel {

    private TextArea codeEditor;
    private Button undoButton;
    private Button redoButton;
    private Button clearButton;
    private MainWindowScreen mainWindow;

    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private String currentText = "";

    public CodePanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        codeEditor = (TextArea) loader.getNamespace().get("codeEditor");
        undoButton = (Button) loader.getNamespace().get("undoButton");
        redoButton = (Button) loader.getNamespace().get("redoButton");
        clearButton = (Button) loader.getNamespace().get("clearButton");

        VBox editorContainer = (VBox) loader.getNamespace().get("editorContainer");
        this.root = editorContainer;
    }

    @Override
    public void initialize() {
        setupCodeEditor();
        setupButtonActions();
        setupKeyboardShortcuts();
        setDefaultCode();
        updateButtonStates();
    }

    private void setupCodeEditor() {
        codeEditor.setStyle("-fx-control-inner-background: #1e1e1e; -fx-text-fill: #d4d4d4; -fx-font-family: 'Monospace';");
        codeEditor.setWrapText(true);

        codeEditor.textProperty().addListener((obs, oldText, newText) -> {
            if (!oldText.equals(currentText)) {
                undoStack.push(oldText);
                redoStack.clear();
                currentText = newText;
                updateButtonStates();

                // Notify control panel
                mainWindow.getControlPanel().setCodeModified(true);
                mainWindow.getConsolePanel().log("Code modified");
            }
        });
    }

    private void setupButtonActions() {
        undoButton.setOnAction(e -> undo());
        redoButton.setOnAction(e -> redo());
        clearButton.setOnAction(e -> clear());
    }

    private void setupKeyboardShortcuts() {
        if (codeEditor.getScene() != null) {
            codeEditor.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN),
                    this::undo
            );

            codeEditor.getScene().getAccelerators().put(
                    new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN),
                    this::redo
            );
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(currentText);
            String previousText = undoStack.pop();
            codeEditor.setText(previousText);
            currentText = previousText;
            updateButtonStates();
            mainWindow.getConsolePanel().log("Undo performed");
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(currentText);
            String nextText = redoStack.pop();
            codeEditor.setText(nextText);
            currentText = nextText;
            updateButtonStates();
            mainWindow.getConsolePanel().log("Redo performed");
        }
    }

    public void clear() {
        if (!codeEditor.getText().trim().isEmpty()) {
            undoStack.push(currentText);
            codeEditor.clear();
            currentText = "";
            redoStack.clear();
            updateButtonStates();
            mainWindow.getConsolePanel().log("Code cleared");
        }
    }

    private void updateButtonStates() {
        undoButton.setDisable(undoStack.isEmpty());
        redoButton.setDisable(redoStack.isEmpty());
        clearButton.setDisable(codeEditor.getText().trim().isEmpty());
    }

    public void setDefaultCode() {
        String defaultCode = """
            // Welcome to CodePlay IDE!
            // Start coding your game here
            
            function init() {
                console.log("Game initialized!");
            }
            
            function update() {
                // Game logic here
            }
            
            function draw() {
                // Drawing here
            }
            """;

        codeEditor.setText(defaultCode);
        currentText = defaultCode;
        undoStack.clear();
        redoStack.clear();
    }

    public String getCode() {
        return codeEditor.getText();
    }

    public void setCode(String code) {
        undoStack.push(currentText);
        codeEditor.setText(code);
        currentText = code;
        updateButtonStates();
    }

    public void focusEditor() {
        codeEditor.requestFocus();
    }
}