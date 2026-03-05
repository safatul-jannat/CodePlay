package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.Ground;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Renderer for Ground entities.
 *
 * Features:
 * - Solid color fill with border
 * - Optional grid lines for large ground areas
 * - Debug rendering for collider bounds
 * - Visual feedback for triggers vs solid blocks
 */
public class GroundRenderer extends Renderer {

    private boolean showGridLines = true;
    private Color gridLineColor = Color.rgb(255, 255, 255, 0.3);
    private boolean showBorder = true;
    private Color borderColor = Color.BLACK;

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible() || !(entity instanceof Ground)) return;

        Ground ground = (Ground) entity;

        // Get screen position
        Vector2 screenPos = coordinateSystem.toScreen(ground.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        // Calculate screen dimensions
        double screenWidth = ground.getWidth() * gridScale;
        double screenHeight = ground.getHeight() * gridScale;

        // Debug output
        System.out.println("Rendering Ground: " + ground.getWidth() + "x" + ground.getHeight() +
                " cells → " + screenWidth + "x" + screenHeight + " pixels at (" +
                screenPos.x + ", " + screenPos.y + ")");

        // Check if ground is a trigger (semi-transparent if trigger)
        boolean isTrigger = ground.hasCollider() && ground.getCollider().isTrigger();

        // Save original opacity
        double originalAlpha = gc.getGlobalAlpha();

        // If trigger, make semi-transparent
        if (isTrigger) {
            gc.setGlobalAlpha(0.5);
        }

        // Draw the ground
        if (ground.hasRenderComponent()) {
            // Use component-based rendering
            renderWithComponent(ground, gc, screenPos, screenWidth, screenHeight);
        } else {
            // Default rendering
            renderDefault(ground, gc, screenPos, screenWidth, screenHeight);
        }

        // Draw grid lines (optional)
        if (showGridLines && (ground.getWidth() > 1 || ground.getHeight() > 1)) {
            renderGridLines(gc, screenPos, screenWidth, screenHeight, gridScale, ground);
        }

        // Draw border (if enabled)
        if (showBorder) {
            gc.setStroke(borderColor);
            gc.setLineWidth(1);
            gc.strokeRect(screenPos.x, screenPos.y, screenWidth, screenHeight);
        }

        // Draw trigger indicator
        if (isTrigger && debugMode) {
            renderTriggerIndicator(gc, screenPos, screenWidth, screenHeight);
        }

        // Restore opacity
        gc.setGlobalAlpha(originalAlpha);

        // Render debug info
        renderDebugInfo(entity, gc, coordinateSystem);
    }

    /**
     * Render using entity's RenderComponent
     */
    private void renderWithComponent(Ground ground, GraphicsContext gc,
                                     Vector2 pos, double width, double height) {
        // Let the component handle fill
        gc.setFill(ground.getRenderComponent().getFillColor());
        gc.fillRect(pos.x, pos.y, width, height);
    }

    /**
     * Default rendering without RenderComponent
     */
    private void renderDefault(Ground ground, GraphicsContext gc,
                               Vector2 pos, double width, double height) {
        // Fill
        gc.setFill(ground.getColor());
        gc.fillRect(pos.x, pos.y, width, height);
    }

    /**
     * Render grid lines for large ground areas
     */
    private void renderGridLines(GraphicsContext gc, Vector2 pos,
                                 double width, double height,
                                 double gridScale, Ground ground) {
        gc.setStroke(gridLineColor);
        gc.setLineWidth(0.5);

        // Draw vertical grid lines
        for (int i = 1; i < ground.getWidth(); i++) {
            double x = pos.x + i * gridScale;
            gc.strokeLine(x, pos.y, x, pos.y + height);
        }

        // Draw horizontal grid lines
        for (int i = 1; i < ground.getHeight(); i++) {
            double y = pos.y + i * gridScale;
            gc.strokeLine(pos.x, y, pos.x + width, y);
        }
    }

    /**
     * Visual indicator for trigger zones
     */
    private void renderTriggerIndicator(GraphicsContext gc, Vector2 pos,
                                        double width, double height) {
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.setLineDashes(5);
        gc.strokeRect(pos.x, pos.y, width, height);
        gc.setLineDashes(null);
    }

    @Override
    protected void renderDebugInfo(Entity entity, GraphicsContext gc,
                                   CoordinateSystem coordinateSystem) {
        if (!debugMode) return;

        Ground ground = (Ground) entity;
        Vector2 screenPos = coordinateSystem.toScreen(ground.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        // Draw collider bounds with different color for triggers
        if (ground.hasCollider()) {
            if (ground.getCollider().isTrigger()) {
                gc.setStroke(Color.YELLOW);
            } else {
                gc.setStroke(Color.RED);
            }
            gc.setLineWidth(1);

            double width = ground.getWidth() * gridScale;
            double height = ground.getHeight() * gridScale;

            gc.strokeRect(screenPos.x, screenPos.y, width, height);
        }

        // Draw ground info
        gc.setFill(Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText(String.format("Ground[%.1fx%.1f]", ground.getWidth(), ground.getHeight()),
                screenPos.x + 2, screenPos.y + 12);

        // Show if trigger
        if (ground.hasCollider() && ground.getCollider().isTrigger()) {
            gc.setFill(Color.YELLOW);
            gc.fillText("TRIGGER", screenPos.x + 2, screenPos.y + 24);
        }
    }

    // ========== CONFIGURATION METHODS ==========

    /**
     * Enable/disable grid lines on ground
     */
    public void setShowGridLines(boolean show) {
        this.showGridLines = show;
    }

    /**
     * Set grid line color
     */
    public void setGridLineColor(Color color) {
        this.gridLineColor = color;
    }

    /**
     * Enable/disable border
     */
    public void setShowBorder(boolean show) {
        this.showBorder = show;
    }

    /**
     * Set border color
     */
    public void setBorderColor(Color color) {
        this.borderColor = color;
    }
}