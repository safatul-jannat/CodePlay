package com.example.codeplayfx.engine.render;

import com.example.codeplayfx.world.base.GameObject;
import com.example.codeplayfx.world.entities.Zombie;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class ZombieRenderer extends BaseRenderer {

    public ZombieRenderer(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public boolean canRender(GameObject obj) {
        return obj instanceof Zombie;
    }

    @Override
    public void render(GameObject obj) {
        if (!canRender(obj)) return;

        Zombie zombie = (Zombie) obj;
        double x = zombie.getX();
        double y = zombie.getY();
        double width = 35;
        double height = 50;

        // ===== ZOMBIE BODY =====
        gc.setFill(Color.DARKGREEN);
        gc.fillRect(x, y, width, height);

        // Add some lighter green patches
        gc.setFill(Color.GREEN.brighter());
        gc.fillRect(x + 5, y + 5, 10, 15);
        gc.fillRect(x + 20, y + 20, 10, 10);

        // ===== ZOMBIE HEAD =====
        double headX = x + 5;
        double headY = y - 10;
        double headWidth = 25;
        double headHeight = 20;

        gc.setFill(Color.OLIVE);
        gc.fillOval(headX, headY, headWidth, headHeight);

        // ===== ZOMBIE EYES =====
        gc.setFill(Color.RED);
        gc.fillOval(headX + 6, headY + 5, 6, 6);   // Left eye
        gc.fillOval(headX + 14, headY + 5, 6, 6);  // Right eye

        // ===== ZOMBIE MOUTH =====
        gc.setFill(Color.DARKRED);
        gc.fillRect(headX + 8, headY + 12, 10, 3);

        // ===== ZOMBIE NAME =====
        drawText(x, y - 20, "ZOMBIE", Color.WHITE, 11);

        // ===== HEALTH BAR =====
        if (zombie.isAlive()) {
            drawHealthBar(x, y, width, height, zombie.getHealth(), 100);
        } else {
            drawCenteredText(x, y, width, height, "DEAD", Color.RED, 10);
        }

        // ===== BORDER =====
        drawBorder(x, y, width, height, Color.DARKGREEN.darker());

        // ===== MOVEMENT TRAIL =====
        drawMovementTrail(x, y, width, height);
    }

    private void drawMovementTrail(double x, double y, double width, double height) {
        gc.setStroke(Color.GREEN.brighter());
        gc.setLineWidth(2);

        // Simple trail lines
        gc.strokeLine(x - 5, y + 20, x, y + 25);
        gc.strokeLine(x - 8, y + 35, x, y + 40);
    }
}