package com.example.codeplayfx.engine.render;

import com.example.codeplayfx.world.base.GameObject;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.text.Font;

public abstract class BaseRenderer {
    protected GraphicsContext gc;

    public BaseRenderer(GraphicsContext gc) {
        this.gc = gc;
    }

    /**
     * Check if this renderer can render the given object
     */
    public abstract boolean canRender(GameObject obj);

    /**
     * Render the object
     */
    public abstract void render(GameObject obj);

    // ===== COMMON RENDERING UTILITIES =====

    protected void drawHealthBar(double x, double y, double width, double height,
                                 int currentHealth, int maxHealth) {
        if (maxHealth <= 0) return;

        double healthPercent = (double) currentHealth / maxHealth;

        // Health bar dimensions
        double barWidth = width;
        double barHeight = 5;
        double barY = y - 8; // Above the object

        // Background (empty health - red)
        gc.setFill(javafx.scene.paint.Color.RED);
        gc.fillRect(x, barY, barWidth, barHeight);

        // Foreground (current health - green)
        gc.setFill(javafx.scene.paint.Color.GREEN);
        gc.fillRect(x, barY, barWidth * healthPercent, barHeight);

        // Border
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(0.5);
        gc.strokeRect(x, barY, barWidth, barHeight);

        // Health text
        drawText(x + 2, barY + 4, currentHealth + "/" + maxHealth,
                javafx.scene.paint.Color.WHITE, 8);
    }

    protected void drawBorder(double x, double y, double width, double height,
                              javafx.scene.paint.Color color) {
        gc.setStroke(color);
        gc.setLineWidth(1.5);
        gc.strokeRect(x, y, width, height);
    }

    protected void drawText(double x, double y, String text,
                            javafx.scene.paint.Color color, int fontSize) {
        gc.setFill(color);
        gc.setFont(Font.font("Arial", fontSize));
        gc.fillText(text, x, y);
    }

    protected void drawCenteredText(double x, double y, double width, double height,
                                    String text, javafx.scene.paint.Color color, int fontSize) {
        // Simple centered text (approx center)
        gc.setFill(color);
        gc.setFont(Font.font("Arial", fontSize));

        // Approximate center (good enough for demo)
        double textX = x + width/2 - (text.length() * fontSize * 0.3);
        double textY = y + height/2 + fontSize/3;

        gc.fillText(text, textX, textY);
    }

    protected void drawProgressBar(double x, double y, double width, double height,
                                   double progress, javafx.scene.paint.Color fillColor,
                                   javafx.scene.paint.Color bgColor) {
        // Draw background
        gc.setFill(bgColor);
        gc.fillRect(x, y, width, height);

        // Draw progress
        gc.setFill(fillColor);
        gc.fillRect(x, y, width * progress, height);

        // Draw border
        gc.setStroke(javafx.scene.paint.Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, width, height);
    }
}