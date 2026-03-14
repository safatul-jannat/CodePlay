package com.example.codeplayfx.world.render;

import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.StaticEntity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * StaticEntityRenderer - Universal renderer for ALL StaticEntity subclasses.
 *
 * Replaces GroundRenderer. Works for Ground, Wall, Platform, or any
 * future StaticEntity subclass automatically.
 *
 * Features:
 * - Solid color fill from RenderComponent
 * - Optional border
 * - Optional grid lines for large entities
 * - Semi-transparent rendering for trigger zones
 * - Debug overlay showing type, size, trigger state
 */
public class StaticEntityRenderer extends Renderer {

    private boolean showGridLines = true;
    private Color   gridLineColor = Color.rgb(255, 255, 255, 0.3);
    private boolean showBorder    = true;
    private Color   borderColor   = Color.BLACK;

    // ========== MAIN RENDER ==========

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;
        if (!(entity instanceof StaticEntity)) return;

        StaticEntity se = (StaticEntity) entity;

        Vector2 screenPos   = coordinateSystem.toScreen(se.getPosition());
        double  gridScale   = coordinateSystem.getGridScale();
        double  screenW     = se.getWidth()  * gridScale;
        double  screenH     = se.getHeight() * gridScale;

        boolean isTrigger   = se.hasCollider() && se.getCollider().isTrigger();

        // Semi-transparent for trigger zones
        double savedAlpha = gc.getGlobalAlpha();
        if (isTrigger) gc.setGlobalAlpha(0.5);

        // Fill
        drawFill(se, gc, screenPos, screenW, screenH);

        // Grid lines on large tiles
        if (showGridLines && (se.getWidth() > 1 || se.getHeight() > 1)) {
            drawGridLines(gc, screenPos, screenW, screenH, gridScale, se);
        }

        // Border
        if (showBorder) {
            gc.setStroke(borderColor);
            gc.setLineWidth(1);
            gc.strokeRect(screenPos.x, screenPos.y, screenW, screenH);
        }

        // Dashed outline for triggers
        if (isTrigger) {
            drawTriggerOutline(gc, screenPos, screenW, screenH);
        }

        gc.setGlobalAlpha(savedAlpha);

        // Debug overlay
        renderDebugInfo(entity, gc, coordinateSystem);
    }

    // ========== PRIVATE HELPERS ==========

    private void drawFill(StaticEntity se, GraphicsContext gc,
                          Vector2 pos, double w, double h) {
        if (se.hasRenderComponent()) {
            gc.setFill(se.getRenderComponent().getFillColor());
        } else {
            gc.setFill(Color.LIGHTGRAY); // fallback
        }
        gc.fillRect(pos.x, pos.y, w, h);
    }

    private void drawGridLines(GraphicsContext gc, Vector2 pos,
                               double w, double h,
                               double gridScale, StaticEntity se) {
        gc.setStroke(gridLineColor);
        gc.setLineWidth(0.5);

        for (int i = 1; i < (int) se.getWidth(); i++) {
            double x = pos.x + i * gridScale;
            gc.strokeLine(x, pos.y, x, pos.y + h);
        }
        for (int i = 1; i < (int) se.getHeight(); i++) {
            double y = pos.y + i * gridScale;
            gc.strokeLine(pos.x, y, pos.x + w, y);
        }
    }

    private void drawTriggerOutline(GraphicsContext gc, Vector2 pos,
                                    double w, double h) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.setLineDashes(5);
        gc.strokeRect(pos.x, pos.y, w, h);
        gc.setLineDashes(null);
    }

    // ========== DEBUG OVERLAY ==========

    @Override
    protected void renderDebugInfo(Entity entity, GraphicsContext gc,
                                   CoordinateSystem coordinateSystem) {
        if (!debugMode) return;

        StaticEntity se       = (StaticEntity) entity;
        Vector2      screenPos = coordinateSystem.toScreen(se.getPosition());
        double       gridScale = coordinateSystem.getGridScale();
        double       w         = se.getWidth()  * gridScale;
        double       h         = se.getHeight() * gridScale;

        // Collider outline: red = solid, yellow = trigger
        if (se.hasCollider()) {
            gc.setStroke(se.getCollider().isTrigger() ? Color.YELLOW : Color.RED);
            gc.setLineWidth(1);
            gc.strokeRect(screenPos.x, screenPos.y, w, h);
        }

        // Label: "Ground[10.0x1.0]" etc.
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText(
            String.format("%s[%.1fx%.1f]",
                se.getClass().getSimpleName(), se.getWidth(), se.getHeight()),
            screenPos.x + 2, screenPos.y + 12
        );

        if (se.hasCollider() && se.getCollider().isTrigger()) {
            gc.setFill(Color.YELLOW);
            gc.fillText("TRIGGER", screenPos.x + 2, screenPos.y + 24);
        }
    }

    // ========== CONFIG ==========

    public void setShowGridLines(boolean show) { this.showGridLines = show; }
    public void setGridLineColor(Color color)  { this.gridLineColor = color; }
    public void setShowBorder(boolean show)    { this.showBorder = show; }
    public void setBorderColor(Color color)    { this.borderColor = color; }
}