package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.KillZone;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

public class KillZoneRenderer extends Renderer {

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;

        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        double width = gridScale;
        double height = gridScale;

        if (entity instanceof KillZone) {
            KillZone killZone = (KillZone) entity;
            width = killZone.getBlockWidth() * gridScale;
            height = killZone.getBlockHeight() * gridScale;

            renderKillZoneByType(killZone, gc, screenPos, width, height);
        } else {
            // Fallback for non-KillZone entities
            RenderComponent renderComp = entity.getComponent(RenderComponent.class);
            Color fillColor = (renderComp != null) ? renderComp.getFillColor() : Color.RED;
            gc.setFill(fillColor);
            gc.fillRect(screenPos.x, screenPos.y, width, height);
            gc.setStroke(Color.DARKGRAY);
            gc.strokeRect(screenPos.x, screenPos.y, width, height);
        }

        renderDebugInfo(entity, gc, coordinateSystem);
    }

    private void renderKillZoneByType(KillZone killZone, GraphicsContext gc, Vector2 pos, double w, double h) {
        float x = (float) pos.x;
        float y = (float) pos.y;

        // Now these will work correctly
        boolean isInstantKill = killZone.isInstantKill();
        float dps = killZone.getDamagePerSecond();
        int damage = killZone.getDamageAmount();
        float pushForce = killZone.getPushForce();

        // Debug: print to console to verify
        System.out.println("KillZone: instant=" + isInstantKill + ", dps=" + dps + ", damage=" + damage + ", push=" + pushForce);

        if (pushForce > 0) {
            renderPushZone(killZone, gc, x, y, w, h);
        } else if (isInstantKill) {
            renderDeathZone(killZone, gc, x, y, w, h);
        } else if (dps > 0) {
            renderDamageOverTimeZone(killZone, gc, x, y, w, h);
        } else if (damage > 0) {
            renderDamageZone(killZone, gc, x, y, w, h);
        } else {
            renderSafeZone(killZone, gc, x, y, w, h);
        }
    }

    // 1. DEATH ZONE - Instant kill (Skull pattern)
    private void renderDeathZone(KillZone killZone, GraphicsContext gc, float x, float y, double w, double h) {
        // Dark red background
        gc.setFill(Color.web("#8B0000")); // DarkRed
        gc.fillRect(x, y, w, h);

        // Bloody edges
        gc.setStroke(Color.web("#FF0000"));
        gc.setLineWidth(2);
        gc.strokeRect(x + 2, y + 2, w - 4, h - 4);

        // Skull icon (simple)
        gc.setFill(Color.WHITE);
        float skullSize = (float) Math.min(w, h) * 0.4f;
        float skullX = x + (float) w / 2 - skullSize / 2;
        float skullY = y + (float) h / 2 - skullSize / 2;

        // Skull head
        gc.fillOval(skullX, skullY, skullSize, skullSize);

        // Eye sockets
        gc.setFill(Color.BLACK);
        float eyeSize = skullSize * 0.25f;
        gc.fillOval(skullX + skullSize * 0.2f, skullY + skullSize * 0.3f, eyeSize, eyeSize);
        gc.fillOval(skullX + skullSize * 0.55f, skullY + skullSize * 0.3f, eyeSize, eyeSize);

        // Crossbones
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(3);
        gc.strokeLine(x + (float) w * 0.25f, y + (float) h * 0.7f,
                x + (float) w * 0.75f, y + (float) h * 0.85f);
        gc.strokeLine(x + (float) w * 0.75f, y + (float) h * 0.7f,
                x + (float) w * 0.25f, y + (float) h * 0.85f);

        // Danger stripes border
        drawDangerStripes(gc, x, y, w, h, 5);
    }

    // 2. DAMAGE OVER TIME ZONE (Toxic/poison green)
    private void renderDamageOverTimeZone(KillZone killZone, GraphicsContext gc, float x, float y, double w, double h) {
        // Toxic green gradient effect
        gc.setFill(Color.web("#228B22")); // ForestGreen
        gc.fillRect(x, y, w, h);

        // Bubbling effect (circles)
        gc.setFill(Color.web("#32CD32", 0.6)); // LimeGreen with alpha
        for (int i = 0; i < 12; i++) {
            float bubbleX = x + (float) ((i * 17) % (w - 10)) + 5;
            float bubbleY = y + (float) ((i * 13) % (h - 10)) + 5;
            float bubbleSize = 3 + ((i * 7) % 8);
            gc.fillOval(bubbleX, bubbleY, bubbleSize, bubbleSize);
        }

        // Warning biohazard pattern
        gc.setStroke(Color.LIMEGREEN);
        gc.setLineWidth(2);
        for (int i = 0; i < 3; i++) {
            float offset = (float) (Math.sin(System.currentTimeMillis() * 0.003 + i) * 2);
            gc.strokeOval(x + w/4 + offset, y + h/4, w/2, h/2);
        }

        // Dotted border for DoT zones
        gc.setStroke(Color.YELLOWGREEN);
        gc.setLineWidth(2);
        gc.setLineDashes(5);
        gc.strokeRect(x + 1, y + 1, w - 2, h - 2);
        gc.setLineDashes(null);
    }

    // 3. DAMAGE ZONE (Simple hazard pattern)
    // 3. DAMAGE ZONE (Simple hazard pattern with clipping)
    private void renderDamageZone(KillZone killZone, GraphicsContext gc, float x, float y, double w, double h) {
        // Save current state
        gc.save();

        // Clip to killzone bounds
        gc.beginPath();
        gc.rect(x, y, w, h);
        gc.clip();

        // Solid dark red background
        gc.setFill(Color.web("#8B0000")); // DarkRed
        gc.fillRect(x, y, w, h);

        // Diagonal hazard stripes (yellow on red) - clipped to bounds
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        for (int i = -20; i < (int)(w + h + 20); i += 15) {
            gc.strokeLine(x + i, y - (float)h, x + i - (float)h, y + (float)h * 2);
        }

        // Restore state (removes clip)
        gc.restore();

        // Simple border
        gc.setStroke(Color.BLACK);
        gc.setLineWidth(1);
        gc.strokeRect(x, y, w, h);
    }
    // 4. PUSH ZONE (Wind/force)
    private void renderPushZone(KillZone killZone, GraphicsContext gc, float x, float y, double w, double h) {
        // Light blue windy color
        gc.setFill(Color.web("#87CEEB", 0.7)); // SkyBlue with alpha
        gc.fillRect(x, y, w, h);

        // Wind lines
        gc.setStroke(Color.CYAN);
        gc.setLineWidth(2);
        for (int i = 0; i < 8; i++) {
            float lineY = y + (i * (float) h / 8);
            gc.strokeLine(x, lineY, x + w, lineY);
        }

        // Arrow indicators (push direction)
        gc.setFill(Color.DARKBLUE);
        float arrowSize = (float) Math.min(w, h) * 0.3f;
        float arrowX = x + (float) w / 2 - arrowSize / 2;
        float arrowY = y + (float) h / 2 - arrowSize / 2;

        // Draw up arrow (default)
        gc.fillPolygon(
                new double[]{arrowX + arrowSize/2, arrowX, arrowX + arrowSize},
                new double[]{arrowY, arrowY + arrowSize, arrowY + arrowSize},
                3
        );

        // Ripple effect circles
        gc.setStroke(Color.LIGHTBLUE);
        gc.setLineWidth(1);
        for (int i = 1; i <= 3; i++) {
            gc.strokeOval(x + w/2 - i*5, y + h/2 - i*5, i*10, i*10);
        }
    }

    // 5. SAFE ZONE (Healing/shield)
    private void renderSafeZone(KillZone killZone, GraphicsContext gc, float x, float y, double w, double h) {
        // Light green healing zone
        gc.setFill(Color.web("#90EE90", 0.5)); // LightGreen with alpha
        gc.fillRect(x, y, w, h);

        // Cross/plus pattern
        gc.setStroke(Color.GREEN);
        gc.setLineWidth(3);
        float centerX = x + (float) w / 2;
        float centerY = y + (float) h / 2;
        float crossSize = (float) Math.min(w, h) * 0.3f;

        gc.strokeLine(centerX - crossSize/2, centerY, centerX + crossSize/2, centerY);
        gc.strokeLine(centerX, centerY - crossSize/2, centerX, centerY + crossSize/2);

        // Pulsing glow (simulated)
        gc.setFill(Color.WHITE);
        gc.fillOval(centerX - 3, centerY - 3, 6, 6);

        // Dashed border
        gc.setStroke(Color.LIGHTGREEN);
        gc.setLineWidth(2);
        gc.setLineDashes(5);
        gc.strokeRect(x + 2, y + 2, w - 4, h - 4);
        gc.setLineDashes(null);
    }

    // Helper: Hazard stripes (black/yellow)
    private void drawHazardStripes(GraphicsContext gc, float x, float y, double w, double h) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(3);
        for (int i = - (int) (w + h); i < (int) (w + h); i += 15) {
            gc.strokeLine(x + i, y, x + i - (float) h, y + (float) h);
        }
    }

    // Helper: Danger stripes border
    private void drawDangerStripes(GraphicsContext gc, float x, float y, double w, double h, int spacing) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        for (int i = 0; i < w / spacing; i++) {
            if (i % 2 == 0) {
                gc.strokeLine(x + i * spacing, y, x + i * spacing + spacing/2, y + 5);
            }
        }
        for (int i = 0; i < w / spacing; i++) {
            if (i % 2 == 0) {
                gc.strokeLine(x + i * spacing, y + h, x + i * spacing + spacing/2, y + h - 5);
            }
        }
    }

    // Helper methods to get KillZone properties (need these getters in KillZone class)
    private float getDamagePerSecond(KillZone killZone) {
        // You need to add a getter in KillZone class
        return 0; // Placeholder
    }

    private int getDamageAmount(KillZone killZone) {
        // You need to add a getter in KillZone class
        return 0; // Placeholder
    }

    private float getPushForce(KillZone killZone) {
        // You need to add a getter in KillZone class
        return 0; // Placeholder
    }
}