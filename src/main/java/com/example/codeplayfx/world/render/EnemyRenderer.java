package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Renderer for Enemy entities.
 * Default appearance: Red circle (different from BlockPlayer)
 */
public class EnemyRenderer extends Renderer {
    
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
        
        // Render based on shape type (same logic as BlockPlayerRenderer)
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
                
            case IMAGE:
                renderImage(gc, screenPos, renderWidth, renderHeight, renderComp);
                break;
                
            default:
                renderCircle(gc, screenPos, renderWidth, renderHeight, renderComp);  // Default to circle for enemies
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
        double arcSize = Math.min(width, height) * 0.3;
        
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
    
    private void renderImage(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        if (renderComp.getImage() != null) {
            gc.drawImage(renderComp.getImage(), pos.x, pos.y, width, height);
        } else {
            renderCircle(gc, pos, width, height, renderComp);
        }
    }
    
    /**
     * Default enemy appearance: Red circle
     */
    private void renderDefault(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        
        // Red circle (different from player's blue rectangle)
        gc.setFill(Color.RED);
        gc.fillOval(screenPos.x, screenPos.y, gridScale, gridScale);
        
        gc.setStroke(Color.DARKRED);
        gc.setLineWidth(2);
        gc.strokeOval(screenPos.x, screenPos.y, gridScale, gridScale);
    }
}