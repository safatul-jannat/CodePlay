package com.example.codeplayfx.world.render;

import com.example.codeplayfx.world.entity.BlockPlayer;
import com.example.codeplayfx.world.GameWorld;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class HUDRenderer {

    private boolean enabled = true;

    public void render(GameWorld world, GraphicsContext gc) {
        if (!enabled) return;

        List<BlockPlayer> players = world.findEntitiesByType(BlockPlayer.class);
        if (players.isEmpty()) return;

        double x          = 15;
        double y          = 15;
        double barWidth   = 180;
        double barHeight  = 14;
        double rowHeight  = 52;
        double panelWidth = 220;
        double panelHeight = 20 + players.size() * rowHeight + 10;

        gc.save();

        // Panel background
        gc.setFill(Color.color(0, 0, 0, 0.55));
        gc.fillRoundRect(x, y, panelWidth, panelHeight, 14, 14);
        gc.setStroke(Color.color(1, 1, 1, 0.15));
        gc.setLineWidth(1);
        gc.strokeRoundRect(x, y, panelWidth, panelHeight, 14, 14);

        // Title
        gc.setFont(Font.font("Arial", FontWeight.BOLD, 11));
        gc.setFill(Color.color(1, 1, 1, 0.7));
        gc.fillText("HEALTH", x + 10, y + 15);

        double rowY = y + 24;

        for (int i = 0; i < players.size(); i++) {
            BlockPlayer p = players.get(i);
            String label = players.size() == 1 ? "Player" : "Player " + (i + 1);

            float hp    = p.getHealth();
            float maxHp = p.getMaxHealth();
            float pct   = maxHp > 0 ? Math.max(0, Math.min(1, hp / maxHp)) : 0;
            boolean alive = p.isAlive();

            // Name
            gc.setFont(Font.font("Arial", FontWeight.BOLD, 12));
            gc.setFill(alive ? Color.WHITE : Color.color(0.6, 0.6, 0.6));
            gc.fillText(label, x + 10, rowY + 13);

            // HP numbers
            String hpText = alive ? (int) hp + " / " + (int) maxHp : "DEAD";
            gc.setFont(Font.font("Arial", FontWeight.NORMAL, 11));
            gc.setFill(alive ? Color.color(0.85, 0.85, 0.85) : Color.color(0.8, 0.2, 0.2));
            gc.fillText(hpText, x + 10 + barWidth - 55, rowY + 13);

            // Bar background
            gc.setFill(Color.color(0.1, 0.4, 0.6));
            gc.fillRoundRect(x + 10, rowY + 18, barWidth, barHeight, 6, 6);

            // Bar fill — shifts color based on health
            Color fillColor;
            if (!alive || pct < 0.25f) {
                fillColor = Color.color(1.0, 0.15, 0.15);  // critical red
            } else if (pct < 0.5f) {
                fillColor = Color.color(1.0, 0.6, 0.1);    // orange warning
            } else {
                fillColor = Color.color(0.3, 0.8, 1.0);    // healthy cyan-blue
            }

            gc.setFill(fillColor);
            gc.fillRoundRect(x + 10, rowY + 18, barWidth * pct, barHeight, 6, 6);

            // Bar border
            gc.setStroke(Color.color(1, 1, 1, 0.2));
            gc.setLineWidth(1);
            gc.strokeRoundRect(x + 10, rowY + 18, barWidth, barHeight, 6, 6);

            // Dead overlay
            if (!alive) {
                gc.setFill(Color.color(0, 0, 0, 0.45));
                gc.fillRoundRect(x + 10, rowY + 18, barWidth, barHeight, 6, 6);
            }

            rowY += rowHeight;
        }

        gc.restore();
    }

    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public boolean isEnabled() { return enabled; }
}