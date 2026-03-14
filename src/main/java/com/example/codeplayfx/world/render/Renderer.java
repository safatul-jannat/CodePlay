package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Abstract base class for all entity renderers.
 * Each entity type can have its own custom renderer.
 * 
 * Renderers handle visual appearance:
 * - Shapes (rectangle, circle, etc.)
 * - Colors
 * - Images/sprites
 * - Animations
 */
public abstract class Renderer {
    
    protected boolean debugMode = false;
    
    /**
     * Render an entity to the screen
     * @param entity the entity to render
     * @param gc JavaFX graphics context
     * @param coordinateSystem for converting world to screen coordinates
     */
    public abstract void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem);
    
    /**
     * Render debug information (collider bounds, position, etc.)
     */
    protected void renderDebugInfo(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!debugMode) return;
        
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        
        // Draw collider bounds if entity has collider
        if (entity.hasCollider()) {
            gc.setStroke(javafx.scene.paint.Color.RED);
            gc.setLineWidth(1);
            
            float width = entity.getCollider().getWidth();
            float height = entity.getCollider().getHeight();
            
            gc.strokeRect(
                screenPos.x,
                screenPos.y,
                width * gridScale,
                height * gridScale
            );
        }
        
        // Draw entity name
        gc.setFill(javafx.scene.paint.Color.WHITE);
        gc.setFont(javafx.scene.text.Font.font(10));
        gc.fillText(entity.getName(), screenPos.x + 2, screenPos.y + 12);
        
        // Draw position
        gc.fillText(
            String.format("(%.1f, %.1f)", entity.getX(), entity.getY()),
            screenPos.x + 2,
            screenPos.y + 24
        );
    }
    
    /**
     * Enable/disable debug rendering
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public boolean isDebugMode() {
        return debugMode;
    }
}