package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Renderer for GoalZone entities (doors, flags, portals).
 *
 * Features:
 * - Distinctive visual appearance
 * - Can animate (pulse, rotate) for goals
 * - Different styles for different goal types
 */
public class GoalZoneRenderer extends Renderer {

    private double time = 0;
    private boolean animate = true;

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;

        // Update animation time
        time += 0.016;

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

        // Apply goal-specific animations
        Color animatedColor = animateGoalColor(renderComp.getFillColor());

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

        // Add goal-specific overlay effects
        renderGoalEffect(entity, gc, screenPos, renderWidth, renderHeight, renderComp);

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
     * Add goal-specific visual effects
     */
    private void renderGoalEffect(Entity entity, GraphicsContext gc, Vector2 pos,
                                  double width, double height, RenderComponent renderComp) {
        Color color = renderComp.getFillColor();

        // Door effect (handle or markings)
        if (isDoorColor(color)) {
            renderDoorEffect(gc, pos, width, height);
        }

        // Flag effect (pole and waving)
        if (isFlagColor(color)) {
            renderFlagEffect(gc, pos, width, height);
        }

        // Portal effect (sparkling/rotating)
        if (isPortalColor(color)) {
            renderPortalEffect(gc, pos, width, height);
        }

        // Common goal effect (pulsing glow)
        renderPulsingGlow(gc, pos, width, height, color);
    }

    private void renderDoorEffect(GraphicsContext gc, Vector2 pos, double width, double height) {
        // Door handle
        gc.setFill(Color.GOLD);
        gc.fillOval(pos.x + width - 10, pos.y + height / 2 - 3, 6, 6);

        // Door panels
        gc.setStroke(Color.BROWN);
        gc.setLineWidth(1);
        gc.strokeLine(pos.x + width / 2, pos.y, pos.x + width / 2, pos.y + height);
    }

    private void renderFlagEffect(GraphicsContext gc, Vector2 pos, double width, double height) {
        // Flag pole
        gc.setStroke(Color.BROWN);
        gc.setLineWidth(3);
        gc.strokeLine(pos.x + 5, pos.y, pos.x + 5, pos.y + height);

        // Waving flag (triangle)
        double wave = Math.sin(time * 5) * 3;
        gc.setFill(Color.RED);
        double[] xPoints = {pos.x + 8, pos.x + width - 5 + wave, pos.x + 8};
        double[] yPoints = {pos.y + 5, pos.y + height / 3, pos.y + height / 1.5};
        gc.fillPolygon(xPoints, yPoints, 3);
    }

    private void renderPortalEffect(GraphicsContext gc, Vector2 pos, double width, double height) {
        // Spinning rings
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        gc.setGlobalAlpha(0.5 + 0.3 * Math.sin(time * 3));

        double centerX = pos.x + width / 2;
        double centerY = pos.y + height / 2;
        double radius = Math.min(width, height) / 2;

        gc.strokeOval(centerX - radius, centerY - radius, radius * 2, radius * 2);
        gc.strokeOval(centerX - radius * 0.7, centerY - radius * 0.7, radius * 1.4, radius * 1.4);
    }

    private void renderPulsingGlow(GraphicsContext gc, Vector2 pos, double width, double height, Color color) {
        // Pulsing glow around goal
        double pulse = 0.3 + 0.2 * Math.sin(time * 4);
        gc.setGlobalAlpha(pulse);

        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeRect(pos.x - 2, pos.y - 2, width + 4, height + 4);
    }

    /**
     * Animate goal colors (pulsing)
     */
    private Color animateGoalColor(Color base) {
        if (!animate) return base;

        // Pulsing brightness
        double pulse = 0.8 + 0.3 * Math.sin(time * 3);
        return new Color(
                Math.min(1.0, base.getRed() * pulse),
                Math.min(1.0, base.getGreen() * pulse),
                Math.min(1.0, base.getBlue() * pulse),
                base.getOpacity()
        );
    }

    private boolean isDoorColor(Color c) {
        return c.getRed() > 0.5 && c.getGreen() < 0.4 && c.getBlue() < 0.3;
    }

    private boolean isFlagColor(Color c) {
        return c.getRed() > 0.8 && c.getGreen() > 0.7 && c.getBlue() < 0.2;
    }

    private boolean isPortalColor(Color c) {
        return c.getRed() > 0.6 && c.getGreen() < 0.4 && c.getBlue() > 0.6;
    }

    /**
     * Default goal zone appearance: Shining gold with star
     */
    private void renderDefault(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        double width = gridScale;
        double height = gridScale;

        // Golden goal
        gc.setFill(Color.GOLD);
        gc.fillRect(screenPos.x, screenPos.y, width, height);

        // Star symbol
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeLine(screenPos.x + width/2, screenPos.y + 5,
                screenPos.x + width/2, screenPos.y + height - 5);
        gc.strokeLine(screenPos.x + 5, screenPos.y + height/2,
                screenPos.x + width - 5, screenPos.y + height/2);
    }

    /**
     * Enable/disable animation
     */
    public void setAnimate(boolean animate) {
        this.animate = animate;
    }
}