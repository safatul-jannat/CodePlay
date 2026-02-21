package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Renderer for BlockPlayer entities.
 * 
 * Features:
 * - Default rectangle shape
 * - Customizable color
 * - Customizable size
 * - Can switch to other shapes (circle, rounded rect, etc.)
 * - Can use images/sprites
 */
public class BlockPlayerRenderer extends Renderer {
    
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
    
    /**
     * Render as rectangle (default)
     */
    private void renderRectangle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        // Fill
        gc.setFill(renderComp.getFillColor());
        gc.fillRect(pos.x, pos.y, width, height);
        
        // Stroke (outline)
        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokeRect(pos.x, pos.y, width, height);
        }
    }
    
    /**
     * Render as rounded rectangle
     */
    private void renderRoundedRectangle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        double arcSize = Math.min(width, height) * 0.3;  // 30% of smallest dimension
        
        // Fill
        gc.setFill(renderComp.getFillColor());
        gc.fillRoundRect(pos.x, pos.y, width, height, arcSize, arcSize);
        
        // Stroke
        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokeRoundRect(pos.x, pos.y, width, height, arcSize, arcSize);
        }
    }
    
    /**
     * Render as circle/oval
     */
    private void renderCircle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        // Fill
        gc.setFill(renderComp.getFillColor());
        gc.fillOval(pos.x, pos.y, width, height);
        
        // Stroke
        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokeOval(pos.x, pos.y, width, height);
        }
    }
    
    /**
     * Render as triangle
     */
    private void renderTriangle(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        // Triangle points (pointing up)
        double[] xPoints = {
            pos.x + width / 2,           // Top center
            pos.x,                       // Bottom left
            pos.x + width                // Bottom right
        };
        double[] yPoints = {
            pos.y,                       // Top
            pos.y + height,              // Bottom left
            pos.y + height               // Bottom right
        };
        
        // Fill
        gc.setFill(renderComp.getFillColor());
        gc.fillPolygon(xPoints, yPoints, 3);
        
        // Stroke
        if (renderComp.getStrokeWidth() > 0) {
            gc.setStroke(renderComp.getStrokeColor());
            gc.setLineWidth(renderComp.getStrokeWidth());
            gc.strokePolygon(xPoints, yPoints, 3);
        }
    }
    
    /**
     * Render as image/sprite
     */
    private void renderImage(GraphicsContext gc, Vector2 pos, double width, double height, RenderComponent renderComp) {
        if (renderComp.getImage() != null) {
            gc.drawImage(renderComp.getImage(), pos.x, pos.y, width, height);
        } else {
            // Fallback to rectangle if no image
            renderRectangle(gc, pos, width, height, renderComp);
        }
    }
    
    /**
     * Render with default appearance (no RenderComponent)
     */
    private void renderDefault(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        
        // Default blue rectangle
        gc.setFill(Color.LIGHTBLUE);
        gc.fillRect(screenPos.x, screenPos.y, gridScale, gridScale);
        
        gc.setStroke(Color.DARKBLUE);
        gc.setLineWidth(2);
        gc.strokeRect(screenPos.x, screenPos.y, gridScale, gridScale);
    }
}