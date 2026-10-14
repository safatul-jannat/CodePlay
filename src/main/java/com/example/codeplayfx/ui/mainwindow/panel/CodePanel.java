package com.example.codeplayfx.ui.mainwindow.panel;

import com.example.codeplayfx.ui.mainwindow.highlighting.SyntaxHighlighter;
import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.*;
import org.fxmisc.richtext.CodeArea;
import org.fxmisc.richtext.LineNumberFactory;
import java.util.Stack;

public class CodePanel extends UIPanel {

    private CodeArea codeEditor;
    private Button undoButton, redoButton, clearButton, fontUp, fontDown;
    private MainWindowScreen mainWindow;
    private VBox editorContainer;
    public StackPane editorStack;

    private Stack<String> undoStack = new Stack<>();
    private Stack<String> redoStack = new Stack<>();
    private String currentText = "";

    // Flag to track first show
    private boolean isFirstShow = true;

    public CodePanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        undoButton = (Button) loader.getNamespace().get("undoButton");
        redoButton = (Button) loader.getNamespace().get("redoButton");
        clearButton = (Button) loader.getNamespace().get("clearButton");
        fontUp = (Button) loader.getNamespace().get("fontUp");
        fontDown = (Button) loader.getNamespace().get("fontDown");

        editorContainer = (VBox) loader.getNamespace().get("editorContainer");
        editorContainer.getChildren().removeIf(node -> node instanceof TextArea);

        codeEditor = new CodeArea();
        editorStack = new StackPane();

        // IMPORTANT: Make StackPane resizable
        editorStack.setPrefSize(Region.USE_COMPUTED_SIZE, Region.USE_COMPUTED_SIZE);
        VBox.setVgrow(editorStack, Priority.ALWAYS);

        // Make CodeArea fill the StackPane
        codeEditor.prefWidthProperty().bind(editorStack.widthProperty());
        codeEditor.prefHeightProperty().bind(editorStack.heightProperty());

        editorStack.getChildren().add(codeEditor);
        //addGifBackground();
        editorContainer.getChildren().add(editorStack);
        this.root = editorContainer;
    }

    private void setupCodeEditor() {
        codeEditor.setParagraphGraphicFactory(LineNumberFactory.get(codeEditor));
        codeEditor.getStyleClass().add("code-area");

        codeEditor.setWrapText(true);
        // Load CSS
        try {
            String cssPath = getClass().getResource("/com/example/codeplayfx/css/synthwave-theme.css").toExternalForm();
            codeEditor.getStylesheets().add(cssPath);
        } catch (Exception e) {
            System.err.println("Failed to load CSS: " + e.getMessage());
        }

        codeEditor.richChanges()
                .filter(ch -> !ch.getInserted().equals(ch.getRemoved()))
                .subscribe(change -> {
                    Platform.runLater(() -> {
                        codeEditor.setStyleSpans(0,
                                SyntaxHighlighter.computeHighlighting(codeEditor.getText()));
                    });
                });

        codeEditor.textProperty().addListener((obs, oldText, newText) -> {
            if (!oldText.equals(currentText)) {
                undoStack.push(oldText);
                redoStack.clear();
                currentText = newText;
                updateButtonStates();
                if (mainWindow.getControlPanel() != null) {
                    mainWindow.getControlPanel().setCodeModified(true);
                }
            }
        });

        codeEditor.setStyle("-fx-font-size: 14px;");
        codeEditor.setBackground(Background.EMPTY);
    }

    private void setupButtonActions() {
        undoButton.setFocusTraversable(false);
        redoButton.setFocusTraversable(false);
        clearButton.setFocusTraversable(false);
        fontUp.setFocusTraversable(false);
        fontDown.setFocusTraversable(false);

        undoButton.setOnAction(e -> undo());
        redoButton.setOnAction(e -> redo());
        clearButton.setOnAction(e -> clear());

        fontUp.setOnAction(e -> {
            String style = codeEditor.getStyle();
            int size = 14;
            if (style.contains("-fx-font-size:")) {
                size = Integer.parseInt(style.split("-fx-font-size:")[1].split("px")[0].trim());
            }
            codeEditor.setStyle("-fx-font-size: " + (size + 1) + "px;");
        });

        fontDown.setOnAction(e -> {
            String style = codeEditor.getStyle();
            int size = 14;
            if (style.contains("-fx-font-size:")) {
                size = Integer.parseInt(style.split("-fx-font-size:")[1].split("px")[0].trim());
            }
            if (size > 8) {
                codeEditor.setStyle("-fx-font-size: " + (size - 1) + "px;");
            }
        });
    }

    public void setupKeyboardShortcuts() {
        if (codeEditor.getScene() == null) return;

        codeEditor.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Z, KeyCombination.CONTROL_DOWN), this::undo
        );
        codeEditor.getScene().getAccelerators().put(
                new KeyCodeCombination(KeyCode.Y, KeyCombination.CONTROL_DOWN), this::redo
        );
    }

    // SINGLE initialize method with parameter
    public void initialize(boolean isFirstShow) {
        this.isFirstShow = isFirstShow;

        setupCodeEditor();
        setupButtonActions();

        if (isFirstShow) {
            // Only set default content on first show
            if (codeEditor != null && codeEditor.getText().isEmpty()) {
                setDefaultCode();
            }
        }

        updateButtonStates();
    }

    // Overload for backward compatibility (calls with true)
    public void initialize() {
        initialize(true);
    }

    public String getCodeContent() {
        return codeEditor != null ? codeEditor.getText() : "";
    }

    public void setCodeContent(String content) {
        if (codeEditor != null && content != null) {
            codeEditor.replaceText(content);
            currentText = content;
            // Update syntax highlighting
            Platform.runLater(() ->
                    codeEditor.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(content))
            );
        }
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(currentText);
            String prev = undoStack.pop();
            codeEditor.replaceText(prev);
            currentText = prev;
            updateButtonStates();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(currentText);
            String next = redoStack.pop();
            codeEditor.replaceText(next);
            currentText = next;
            updateButtonStates();
        }
    }

    public void clear() {
        if (!codeEditor.getText().trim().isEmpty()) {
            undoStack.push(currentText);
            codeEditor.clear();
            currentText = "";
            redoStack.clear();
            updateButtonStates();
        }
    }

    private void updateButtonStates() {
        undoButton.setDisable(undoStack.isEmpty());
        redoButton.setDisable(redoStack.isEmpty());
        clearButton.setDisable(codeEditor.getText().trim().isEmpty());
    }

    public void setDefaultCode() {
        String code = SyntaxHighlighter.getDefaultCode();
        codeEditor.replaceText(code);
        currentText = code;
        undoStack.clear();
        redoStack.clear();

        Platform.runLater(() ->
                codeEditor.setStyleSpans(0, SyntaxHighlighter.computeHighlighting(code))
        );
    }
    public void setEditorFocusable(boolean focusable) {
        if (codeEditor != null) {
            codeEditor.setFocusTraversable(focusable);
            if (!focusable) {
                // Remove focus from editor if it has it
                Platform.runLater(() -> {
                    if (codeEditor.isFocused()) {
                        codeEditor.getParent().requestFocus();
                    }
                });
            }
        }
    }

    public boolean isEditorFocused() {
        return codeEditor != null && codeEditor.isFocused();
    }

    public String getCode() { return codeEditor.getText(); }
    public void focusEditor() {
        if (codeEditor != null) {
            codeEditor.requestFocus();
        }
    }    public CodeArea getCodeEditor() { return codeEditor; }
}