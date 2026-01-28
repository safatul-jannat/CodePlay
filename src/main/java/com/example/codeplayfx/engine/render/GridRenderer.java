package com.example.codeplayfx.engine.render;

import com.example.codeplayfx.engine.core.GridConfig;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GridRenderer {

    private GridConfig config;
    private String mousePosition = "(0, 0)";

    public GridRenderer(GridConfig config) {
        this.config = config;
    }

    public void render(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        if (!config.isEnabled()) return;

        int gridSize = config.getSize();

        // Draw grid lines
        gc.setStroke(Color.rgb(60, 60, 60));
        gc.setLineWidth(1.0);

        // Vertical lines
        for (double x = 0; x <= canvasWidth; x += gridSize) {
            gc.strokeLine(x, 0, x, canvasHeight);
        }

        // Horizontal lines
        for (double y = 0; y <= canvasHeight; y += gridSize) {
            gc.strokeLine(0, y, canvasWidth, y);
        }

        // Center lines
        gc.setStroke(Color.rgb(80, 80, 80));
        double centerX = Math.floor(canvasWidth / 2 / gridSize) * gridSize;
        double centerY = Math.floor(canvasHeight / 2 / gridSize) * gridSize;

        gc.strokeLine(centerX, 0, centerX, canvasHeight);
        gc.strokeLine(0, centerY, canvasWidth, centerY);

        // Coordinates
        if (config.isShowCoordinates()) {
            drawCoordinates(gc, canvasWidth, canvasHeight, centerX, centerY, gridSize);
        }

        // Mouse position
        drawMousePosition(gc, canvasWidth, canvasHeight);

        // Border
        gc.setStroke(Color.rgb(40, 40, 40));
        gc.setLineWidth(2.0);
        gc.strokeRect(0, 0, canvasWidth, canvasHeight);
    }

    private void drawCoordinates(GraphicsContext gc, double width, double height,
                                 double centerX, double centerY, int gridSize) {
        gc.setFill(Color.rgb(150, 150, 150));
        gc.setFont(javafx.scene.text.Font.font(10));

        // X-axis
        for (int i = 0; i <= width; i += gridSize * 2) {
            int value = (int) ((i - centerX) / gridSize);
            gc.fillText(String.valueOf(value), i + 2, centerY - 2);
        }

        // Y-axis
        for (int i = 0; i <= height; i += gridSize * 2) {
            int value = (int) ((centerY - i) / gridSize);
            if (value != 0) {
                gc.fillText(String.valueOf(value), centerX + 2, i - 2);
            }
        }

        // Center marker
        gc.setFill(Color.rgb(100, 200, 100));
        gc.fillText("(0,0)", centerX + 5, centerY - 5);
    }

    private void drawMousePosition(GraphicsContext gc, double canvasWidth, double canvasHeight) {
        gc.setFill(Color.rgb(255, 255, 255));
        gc.setFont(javafx.scene.text.Font.font(12));

        gc.fillText("Mouse: " + mousePosition, canvasWidth - 120, 20);
        gc.fillText(String.format("Canvas: %.0f x %.0f", canvasWidth, canvasHeight), 10, 20);
    }

    public void updateMousePosition(double screenX, double screenY, double canvasWidth, double canvasHeight) {
        int gridSize = config.getSize();
        double centerX = canvasWidth / 2;
        double centerY = canvasHeight / 2;

        double worldX = (screenX - centerX) / gridSize;
        double worldY = (centerY - screenY) / gridSize;

        mousePosition = String.format("(%.1f, %.1f) [%.0f, %.0f]",
                worldX, worldY, screenX, screenY);
    }

    public double screenToWorldX(double screenX, double canvasWidth) {
        return (screenX - canvasWidth / 2) / config.getSize();
    }

    public double screenToWorldY(double screenY, double canvasHeight) {
        return (canvasHeight / 2 - screenY) / config.getSize();
    }
}