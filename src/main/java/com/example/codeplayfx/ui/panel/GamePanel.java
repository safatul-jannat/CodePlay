package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import com.example.codeplayfx.engine.core.GameEngine;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class GamePanel extends UIPanel {

    private Canvas canvas;
    private MainWindowScreen mainWindow;

    public GamePanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        Pane container = (Pane) loader.getNamespace().get("canvasContainer");

        canvas = new Canvas(800, 600);
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());

        // Pass canvas to engine
        GameEngine engine = mainWindow.getGameEngine();
        if (engine != null) {
            engine.setCanvas(canvas);
        }

        // Setup mouse tracking
        setupMouseTracking();

        container.getChildren().add(canvas);
        this.root = container;
    }

    @Override
    public void initialize() {
        System.out.println("GamePanel initialized");
    }

    private void setupMouseTracking() {
        canvas.setOnMouseMoved(this::handleMouseMoved);
        canvas.setOnMouseDragged(this::handleMouseMoved);

        canvas.setOnMousePressed(e -> {
            GameEngine engine = mainWindow.getGameEngine();
            if (engine != null && engine.getGridRenderer() != null) {
                double worldX = engine.getGridRenderer().screenToWorldX(e.getX(), canvas.getWidth());
                double worldY = engine.getGridRenderer().screenToWorldY(e.getY(), canvas.getHeight());
                mainWindow.getConsolePanel().log(
                        String.format("Canvas clicked at (%.1f, %.1f)", worldX, worldY)
                );
            }
        });
    }

    private void handleMouseMoved(MouseEvent event) {
        GameEngine engine = mainWindow.getGameEngine();
        if (engine != null && engine.getGridRenderer() != null) {
            engine.getGridRenderer().updateMousePosition(
                    event.getX(), event.getY(), canvas.getWidth(), canvas.getHeight()
            );
        }
    }

    public Canvas getCanvas() {
        return canvas;
    }
}