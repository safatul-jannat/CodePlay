package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Renderer for KillZone entities (lava, spikes, pits).
 *
 * Features:
 * - Hazard-specific visual effects
 * - Can animate (flicker, pulse) for hazards
 * - Different appearances for different hazard types
 */
public class KillZoneRenderer extends Renderer {

    // Animation fields
    private double time = 0;
    private boolean animate = true;

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;

        // Update animation time
        time += 0.016; // Assuming ~60 FPS

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

        // Apply hazard-specific animations based on color
        Color baseColor = renderComp.getFillColor();
        Color animatedColor = animateHazardColor(baseColor);

        // Create temporary render component with animated color
        RenderComponent animatedComp = new RenderComponent();
        animatedComp.setFillColor(animatedColor);
        animatedComp.setStrokeColor(renderComp.getStrokeColor());
        animatedComp.setStrokeWidth(renderComp.getStrokeWidth());
        animatedComp.setShapeType(renderComp.getShapeType());
        animatedComp.setImage(renderComp.getImage());

        // Render based on shape type
        switch (renderComp.getShapeType()) {
            case RECTANGLE:
                renderRectangle(gc, screenPos, renderWidth, renderHeight, animatedComp);
                break;

            case ROUNDED_RECTANGLE:
                renderRoundedRectangle(gc, screenPos, renderWidth, renderHeight, animatedComp);
                break;

            case CIRCLE:
                renderCircle(gc, screenPos, renderWidth, renderHeight, animatedComp);
                break;

            case TRIANGLE:
                renderTriangle(gc, screenPos, renderWidth, renderHeight, animatedComp);
                break;

            case IMAGE:
                renderImage(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;

            default:
                renderRectangle(gc, screenPos, renderWidth, renderHeight, animatedComp);
        }

        // Add hazard-specific overlay effects
        renderHazardEffect(entity, gc, screenPos, renderWidth, renderHeight, renderComp);

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
        double arcSize = Math.min(width, height) * 0.2;

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
     * Add hazard-specific visual effects
     */
    private void renderHazardEffect(Entity entity, GraphicsContext gc, Vector2 pos,
                                    double width, double height, RenderComponent renderComp) {
        Color color = renderComp.getFillColor();

        // Lava effect (glowing orange/red with particles)
        if (isLavaColor(color)) {
            renderLavaEffect(gc, pos, width, height);
        }

        // Spike effect (darker lines)
        if (isSpikeColor(color)) {
            renderSpikeEffect(gc, pos, width, height);
        }

        // Pit effect (dark with gradient)
        if (isPitColor(color)) {
            renderPitEffect(gc, pos, width, height);
        }
    }

    private void renderLavaEffect(GraphicsContext gc, Vector2 pos, double width, double height) {
        // Flickering glow
        double flicker = 0.5 + 0.5 * Math.sin(time * 10);
        gc.setGlobalAlpha(flicker * 0.3);

        gc.setFill(Color.ORANGE);
        gc.fillRect(pos.x - 2, pos.y - 2, width + 4, height + 4);

        // Bubbles/particles
        gc.setFill(Color.YELLOW);
        gc.setGlobalAlpha(0.6);

        for (int i = 0; i < 3; i++) {
            double bubbleX = pos.x + (Math.sin(time * 5 + i) * 0.3 + 0.5) * width;
            double bubbleY = pos.y + height - 5 + Math.sin(time * 8 + i) * 3;
            gc.fillOval(bubbleX, bubbleY, 3, 3);
        }
    }

    private void renderSpikeEffect(GraphicsContext gc, Vector2 pos, double width, double height) {
        // Draw spike lines
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);

        int spikeCount = (int) (width / 10);
        for (int i = 0; i < spikeCount; i++) {
            double x = pos.x + (i + 0.5) * (width / spikeCount);
            gc.strokeLine(x, pos.y, x - 3, pos.y - 5);
            gc.strokeLine(x, pos.y, x + 3, pos.y - 5);
        }
    }

    private void renderPitEffect(GraphicsContext gc, Vector2 pos, double width, double height) {
        // Dark gradient effect
        gc.setGlobalAlpha(0.3);
        for (int i = 0; i < 3; i++) {
            gc.setFill(Color.rgb(0, 0, 0, 0.1));
            gc.fillRect(pos.x + i, pos.y + i, width - 2*i, height - 2*i);
        }
    }

    /**
     * Animate hazard colors (flickering for lava, etc.)
     */
    private Color animateHazardColor(Color base) {
        if (isLavaColor(base)) {
            // Flickering lava
            double flicker = 0.8 + 0.4 * Math.sin(time * 15);
            return new Color(
                    Math.min(1.0, base.getRed() * flicker),
                    base.getGreen() * 0.8,
                    base.getBlue() * 0.5,
                    base.getOpacity()
            );
        }
        return base;
    }

    private boolean isLavaColor(Color c) {
        return c.getRed() > 0.8 && c.getGreen() < 0.6 && c.getBlue() < 0.3;
    }

    private boolean isSpikeColor(Color c) {
        return c.getRed() > 0.7 && c.getGreen() > 0.7 && c.getBlue() > 0.7;
    }

    private boolean isPitColor(Color c) {
        return c.getRed() < 0.2 && c.getGreen() < 0.2 && c.getBlue() < 0.2;
    }

    /**
     * Default kill zone appearance: Semi-transparent red hazard
     */
    private void renderDefault(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        double width = gridScale;
        double height = gridScale;

        if (entity instanceof com.example.codeplayfx.world.entity.KillZone) {
            com.example.codeplayfx.world.entity.KillZone killZone = (com.example.codeplayfx.world.entity.KillZone) entity;
            // You'd need getWidth/getHeight methods in KillZone
        }

        // Hazard red with transparency
        gc.setFill(new Color(1.0, 0.2, 0.2, 0.5));
        gc.fillRect(screenPos.x, screenPos.y, width, height);

        // Warning pattern (diagonal lines)
        gc.setStroke(Color.RED);
        gc.setLineWidth(1);
        gc.strokeLine(screenPos.x, screenPos.y, screenPos.x + width, screenPos.y + height);
        gc.strokeLine(screenPos.x + width, screenPos.y, screenPos.x, screenPos.y + height);
    }

    /**
     * Enable/disable animation
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }
}