package com.example.codeplayfx.engine.render;

import com.example.codeplayfx.world.base.GameObject;
import com.example.codeplayfx.world.entities.Bot;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class BotRenderer extends BaseRenderer {

    public BotRenderer(GraphicsContext gc) {
        super(gc);
    }

    @Override
    public boolean canRender(GameObject obj) {
        return obj instanceof Bot;
    }

    @Override
    public void render(GameObject obj) {
        if (!canRender(obj)) return;

        Bot bot = (Bot) obj;
        double x = bot.getX();
        double y = bot.getY();
        double width = 40;
        double height = 60;

        // ===== BOT BODY =====
        // Solid color instead of gradient (simpler)
        gc.setFill(Color.DODGERBLUE);
        gc.fillRect(x, y, width, height);

        // ===== BOT HEAD =====
        double headX = x + 10;
        double headY = y - 15;
        double headWidth = 20;
        double headHeight = 20;

        gc.setFill(Color.LIGHTSKYBLUE);
        gc.fillOval(headX, headY, headWidth, headHeight);

        // ===== BOT EYES =====
        gc.setFill(Color.WHITE);
        gc.fillOval(headX + 4, headY + 5, 4, 4);  // Left eye
        gc.fillOval(headX + 12, headY + 5, 4, 4); // Right eye

        gc.setFill(Color.BLUE);
        gc.fillOval(headX + 5, headY + 6, 2, 2);  // Left pupil
        gc.fillOval(headX + 13, headY + 6, 2, 2); // Right pupil

        // ===== BOT NAME =====
        String name = bot.getPlayerName();
        if (name == null || name.isEmpty()) {
            name = "Bot";
        }
        drawText(x, y - 20, name, Color.WHITE, 12);

        // ===== HEALTH BAR =====
        if (bot.isAlive()) {
            drawHealthBar(x, y, width, height, bot.getHealth(), 100);
        } else {
            drawCenteredText(x, y, width, height, "DEAD", Color.RED, 10);
        }

        // ===== BORDER =====
        drawBorder(x, y, width, height, Color.BLACK);

        // ===== MOVEMENT INDICATOR =====
        drawMovementIndicator(x, y, width, height, bot);
    }

    private void drawMovementIndicator(double x, double y, double width, double height, Bot bot) {
        // Simple movement indicator
        gc.setFill(Color.YELLOW);

        // Draw direction based on movement
        double indicatorX = x + width/2;
        double indicatorY = y + height + 10;

        // Simple arrow pointing based on last movement
        gc.fillPolygon(new double[]{indicatorX - 5, indicatorX + 5, indicatorX},
                new double[]{indicatorY - 5, indicatorY - 5, indicatorY + 5}, 3);
    }
}