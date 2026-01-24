package com.example.codeplayfx.ui;

import com.example.codeplayfx.core.GameWorld;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GamePanel {
    private final Pane container;
    private final Canvas canvas;
    private final GameWorld gameWorld;

    public GamePanel(Pane existingPane) {
        this.container = existingPane;
        this.gameWorld = new GameWorld();
        this.canvas = new Canvas();

        setupCanvas();
        refresh();
    }

    private void setupCanvas() {
        canvas.widthProperty().bind(container.widthProperty());
        canvas.heightProperty().bind(container.heightProperty());

        container.getChildren().add(canvas);

        container.setStyle(
                "-fx-border-color: #2c3e50; " +
                        "-fx-border-width: 2px; " +
                        "-fx-background-color: #ecf0f1;"
        );

        canvas.widthProperty().addListener((obs, oldVal, newVal) -> refresh());
        canvas.heightProperty().addListener((obs, oldVal, newVal) -> refresh());
    }

    public void refresh() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        double width = canvas.getWidth();
        double height = canvas.getHeight();

        gc.clearRect(0, 0, width, height);

        drawBackground(gc, width, height);
        drawGrid(gc, width, height);
        gameWorld.renderAll(gc);
        drawStats(gc, width, height);
    }

    private void drawBackground(GraphicsContext gc, double width, double height) {
        gc.setFill(Color.web("#ecf0f1"));
        gc.fillRect(0, 0, width, height);
    }

    private void drawGrid(GraphicsContext gc, double width, double height) {
        gc.setStroke(Color.web("#bdc3c7"));
        gc.setLineWidth(0.5);

        double gridSize = 50;

        for (double x = 0; x < width; x += gridSize) {
            gc.strokeLine(x, 0, x, height);
        }

        for (double y = 0; y < height; y += gridSize) {
            gc.strokeLine(0, y, width, y);
        }
    }

    private void drawStats(GraphicsContext gc, double width, double height) {
        int count = gameWorld.getObjectCount();

        gc.setFill(Color.web("#2c3e50"));
        gc.setFont(javafx.scene.text.Font.font("Arial", 12));
        gc.fillText("Objects: " + count, 10, 20);

        if (count > 0) {
            gc.fillText("Drag coordinates to reposition", 10, 40);
        }
    }

    public void reset() {
        gameWorld.clearAll();
        refresh();
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }
}