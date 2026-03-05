package com.example.codeplayfx.world.render;

import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Collectible;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * CollectibleRenderer — draws coins, gems, and power-ups on the canvas.
 *
 * Reads the entity's RenderComponent (color, shape, size) and draws it.
 * Also adds a subtle glow effect and a small shine highlight to make
 * collectibles look distinct from regular entities.
 *
 * Follows the exact same pattern as BlockPlayerRenderer.
 */
public class CollectibleRenderer extends Renderer {

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coords) {
        if (!entity.isVisible()) return;

        RenderComponent render = entity.getRenderComponent();
        if (render == null || !render.isActive()) return;

        // Convert grid position → screen pixels
        Vector2 screenPos = coords.toScreen(entity.getPosition());
        double gridScale  = coords.getGridScale();

        double w = render.getWidth()  * gridScale;
        double h = render.getHeight() * gridScale;
        double x = screenPos.x;
        double y = screenPos.y;

        Color fill   = render.getFillColor()   != null ? render.getFillColor()   : Color.GOLD;
        Color stroke = render.getStrokeColor() != null ? render.getStrokeColor() : Color.DARKORANGE;
        double strokeWidth = render.getStrokeWidth();

        gc.save();

        ShapeType shape = render.getShapeType();

        // --- Draw glow behind the shape ---
        gc.setEffect(new javafx.scene.effect.Glow(0.6));

        if (shape == ShapeType.CIRCLE) {
            // Fill
            gc.setFill(fill);
            gc.fillOval(x, y, w, h);
            // Stroke
            if (strokeWidth > 0) {
                gc.setStroke(stroke);
                gc.setLineWidth(strokeWidth);
                gc.strokeOval(x, y, w, h);
            }
        } else if (shape == ShapeType.ROUNDED_RECTANGLE) {
            double arc = Math.min(w, h) * 0.4;
            gc.setFill(fill);
            gc.fillRoundRect(x, y, w, h, arc, arc);
            if (strokeWidth > 0) {
                gc.setStroke(stroke);
                gc.setLineWidth(strokeWidth);
                gc.strokeRoundRect(x, y, w, h, arc, arc);
            }
        } else {
            // RECTANGLE fallback
            gc.setFill(fill);
            gc.fillRect(x, y, w, h);
            if (strokeWidth > 0) {
                gc.setStroke(stroke);
                gc.setLineWidth(strokeWidth);
                gc.strokeRect(x, y, w, h);
            }
        }

        // --- Draw a small shine highlight in the top-left to make it look 3D ---
        gc.setEffect(null);
        gc.setFill(Color.rgb(255, 255, 255, 0.4));
        gc.fillOval(x + w * 0.2, y + h * 0.15, w * 0.25, h * 0.2);

        gc.restore();

        // Debug info (collider bounds, name) — only in debug mode
        renderDebugInfo(entity, gc, coords);
    }
}