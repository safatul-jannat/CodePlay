package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Renderer for SolidBlock entities.
 *
 * Features:
 * - Default gray rectangle
 * - Can have textures/images
 * - Supports all shape types
 */
public class SolidBlockRenderer extends Renderer {

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;

        // Get render component
        RenderComponent renderComp = entity.getComponent(RenderComponent.class);
        if (renderComp == null || !renderComp.isActive()) {
            renderDefault(entity, gc, coordinateSystem);
            return;
        }

        // Get screen position
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        // Calculate render size
        double renderWidth = renderComp.getWidth() * gridScale;
        double renderHeight = renderComp.getHeight() * gridScale;

        // Set opacity
        gc.setGlobalAlpha(renderComp.getOpacity());

        // Render based on shape type
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
                renderRectangle(gc, screenPos, renderWidth, renderHeight, renderComp);
        }

        // Reset opacity
        gc.setGlobalAlpha(1.0);

        // Render debug info
        renderDebugInfo(entity, gc, coordinateSystem);
    }

    private void renderRectangle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        gc.setFill(renderComp.getFillColor());
        gc.fillRect(pos.x, pos.y, width, height);

        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokeRect(pos.x, pos.y, width, height);
        }
    }

    private void renderRoundedRectangle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        double arcSize = Math.min(width, height) * 0.1; // Smaller arcs for blocks

        gc.setFill(renderComp.getFillColor());
        gc.fillRoundRect(pos.x, pos.y, width, height, arcSize, arcSize);

        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokeRoundRect(pos.x, pos.y, width, height, arcSize, arcSize);
        }
    }

    private void renderCircle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        gc.setFill(renderComp.getFillColor());
        gc.fillOval(pos.x, pos.y, width, height);

        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokeOval(pos.x, pos.y, width, height);
        }
    }

    private void renderTriangle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        double[] xPoints = {
                pos.x + width / 2,
                pos.x,
                pos.x + width
        };
        double[] yPoints = {
                pos.y,
                pos.y + height,
                pos.y + height
        };

        gc.setFill(renderComp.getFillColor());
        gc.fillPolygon(xPoints, yPoints, 3);

        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }

    private void renderImage(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        if (renderComp.getImage() != null) {
            gc.drawImage(renderComp.getImage(), pos.x, pos.y, width, height);
        } else {
            renderRectangle(gc, pos, width, height, renderComp);
        }
    }

    /**
     * Default solid block appearance: Gray rectangle
     */
    private void renderDefault(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        // Get dimensions from entity if available
        double width = gridScale;
        double height = gridScale;

        if (entity instanceof com.example.codeplayfx.world.entity.SolidBlock) {
            com.example.codeplayfx.world.entity.SolidBlock block = (com.example.codeplayfx.world.entity.SolidBlock) entity;
            width = block.getBlockWidth() * gridScale;
            height = block.getBlockHeight() * gridScale;
        }

        // Default gray block
        gc.setFill(Color.GRAY);
        gc.fillRect(screenPos.x, screenPos.y, width, height);

        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);
        gc.strokeRect(screenPos.x, screenPos.y, width, height);
    }
}