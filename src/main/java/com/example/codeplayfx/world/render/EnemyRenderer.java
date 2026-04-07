package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * EnemyRenderer — draws Enemy entities on the JavaFX canvas.
 *
 * Mirrors BlockPlayerRenderer exactly in structure so the rendering
 * pipeline treats them identically. The default look is a red circle
 * with a dark-red outline, but users can override colour/shape/sprite
 * through Enemy's appearance API and this renderer will honour it.
 *
 * Supported shape types (driven by RenderComponent):
 *   CIRCLE            → filled oval (default)
 *   RECTANGLE         → filled rectangle
 *   ROUNDED_RECTANGLE → filled rounded rectangle
 *   TRIANGLE          → filled upward-pointing triangle
 *   IMAGE             → sprite image
 */
public class EnemyRenderer extends Renderer {

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;

        // Get render component — every Enemy has one set up in the constructor
        RenderComponent renderComp = entity.getComponent(RenderComponent.class);
        if (renderComp == null || !renderComp.isActive()) {
            renderDefault(entity, gc, coordinateSystem);
            return;
        }

        // Convert world position → screen pixels
        Vector2 screenPos  = coordinateSystem.toScreen(entity.getPosition());
        double  gridScale  = coordinateSystem.getGridScale();

        // Size in pixels
        double renderWidth  = renderComp.getWidth()  * gridScale;
        double renderHeight = renderComp.getHeight() * gridScale;

        // Apply opacity
        gc.setGlobalAlpha(renderComp.getOpacity());

        // Dispatch to the correct shape renderer
        switch (renderComp.getShapeType()) {
            case RECTANGLE:
                renderRectangle(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;

            case ROUNDED_RECTANGLE:
                renderRoundedRectangle(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;

            case CIRCLE:
                renderCircle(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;

            case TRIANGLE:
                renderTriangle(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;

            case IMAGE:
                renderImage(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;

            default:
                // Unknown shape type — fall back to circle (sensible default for an enemy)
                renderCircle(gc, screenPos, renderWidth, renderHeight, renderComp);
        }

        // Restore full opacity for subsequent entities
        gc.setGlobalAlpha(1.0);

        // Debug overlay (collider box, name, position) — only active when debugMode = true
        renderDebugInfo(entity, gc, coordinateSystem);
    }

    // ========== SHAPE RENDERERS ==========

    private void renderCircle(GraphicsContext gc, Vector2 pos,
                              double width, double height,
                              RenderComponent rc) {
        gc.setFill(rc.getFillColor());
        gc.fillOval(pos.x, pos.y, width, height);

        if (rc.getStrokeWidth() > 0) {
            gc.setStroke(rc.getStrokeColor());
            gc.setLineWidth(rc.getStrokeWidth());
            gc.strokeOval(pos.x, pos.y, width, height);
        }
    }

    private void renderRectangle(GraphicsContext gc, Vector2 pos,
                                 double width, double height,
                                 RenderComponent rc) {
        gc.setFill(rc.getFillColor());
        gc.fillRect(pos.x, pos.y, width, height);

        if (rc.getStrokeWidth() > 0) {
            gc.setStroke(rc.getStrokeColor());
            gc.setLineWidth(rc.getStrokeWidth());
            gc.strokeRect(pos.x, pos.y, width, height);
        }
    }

    private void renderRoundedRectangle(GraphicsContext gc, Vector2 pos,
                                        double width, double height,
                                        RenderComponent rc) {
        double arcSize = Math.min(width, height) * 0.3;

        gc.setFill(rc.getFillColor());
        gc.fillRoundRect(pos.x, pos.y, width, height, arcSize, arcSize);

        if (rc.getStrokeWidth() > 0) {
            gc.setStroke(rc.getStrokeColor());
            gc.setLineWidth(rc.getStrokeWidth());
            gc.strokeRoundRect(pos.x, pos.y, width, height, arcSize, arcSize);
        }
    }

    private void renderTriangle(GraphicsContext gc, Vector2 pos,
                                double width, double height,
                                RenderComponent rc) {
        // Upward-pointing triangle — same convention as BlockPlayerRenderer
        double[] xPoints = {
                pos.x + width / 2,   // apex (top centre)
                pos.x,               // bottom-left
                pos.x + width        // bottom-right
        };
        double[] yPoints = {
                pos.y,               // apex
                pos.y + height,      // bottom-left
                pos.y + height       // bottom-right
        };

        gc.setFill(rc.getFillColor());
        gc.fillPolygon(xPoints, yPoints, 3);

        if (rc.getStrokeWidth() > 0) {
            gc.setStroke(rc.getStrokeColor());
            gc.setLineWidth(rc.getStrokeWidth());
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }

    private void renderImage(GraphicsContext gc, Vector2 pos,
                             double width, double height,
                             RenderComponent rc) {
        if (rc.getImage() != null) {
            gc.drawImage(rc.getImage(), pos.x, pos.y, width, height);
        } else {
            // No image loaded yet — fall back to the default red circle
            renderDefault(null, gc, null);
        }
    }

    // ========== FALLBACK ==========

    /**
     * Renders a plain red circle when no RenderComponent is available.
     * Should never be reached in practice because Enemy always sets one up.
     */
    private void renderDefault(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (entity == null || coordinateSystem == null) return;

        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double  gridScale = coordinateSystem.getGridScale();

        gc.setFill(Color.RED);
        gc.fillOval(screenPos.x, screenPos.y, gridScale, gridScale);

        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokeOval(screenPos.x, screenPos.y, gridScale, gridScale);
    }
}