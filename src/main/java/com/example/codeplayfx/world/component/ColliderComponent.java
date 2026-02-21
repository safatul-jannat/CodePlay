package com.example.codeplayfx.world.component;

import javafx.geometry.Rectangle2D;
import com.example.codeplayfx.world.physics.Vector2;

/**
 * Collider component for collision detection.
 * Uses JavaFX Rectangle2D for AABB (Axis-Aligned Bounding Box) collision.
 * 
 * Every entity that can collide should have this component.
 */
public class ColliderComponent implements Component {
    
    private float width;      // Collider width (in grid units)
    private float height;     // Collider height (in grid units)
    private Vector2 offset;   // Offset from entity position
    private boolean active;
    private boolean isTrigger; // If true, collision detected but no physics response
    
    /**
     * Create collider with size 1x1 (one grid cell)
     */
    public ColliderComponent() {
        this(1.0f, 1.0f);
    }
    
    /**
     * Create collider with specific size
     * @param width collider width in grid units
     * @param height collider height in grid units
     */
    public ColliderComponent(float width, float height) {
        this.width = width;
        this.height = height;
        this.offset = Vector2.ZERO;
        this.active = true;
        this.isTrigger = false;
    }
    
    /**
     * Create collider with size and offset
     */
    public ColliderComponent(float width, float height, Vector2 offset) {
        this(width, height);
        this.offset = offset;
    }
    
    /**
     * Get the collision bounds as Rectangle2D
     * @param entityPosition the entity's world position
     * @return Rectangle2D representing the collision box
     */
    public Rectangle2D getBounds(Vector2 entityPosition) {
        float x = entityPosition.x + offset.x;
        float y = entityPosition.y + offset.y;
        return new Rectangle2D(x, y, width, height);
    }
    
    /**
     * Check if this collider intersects with another collider
     */
    public boolean intersects(ColliderComponent other, Vector2 thisPos, Vector2 otherPos) {
        if (!this.active || !other.active) return false;
        
        Rectangle2D thisBounds = this.getBounds(thisPos);
        Rectangle2D otherBounds = other.getBounds(otherPos);
        
        return thisBounds.intersects(otherBounds);
    }
    
    /**
     * Check if a point is inside this collider
     */
    public boolean containsPoint(Vector2 point, Vector2 entityPosition) {
        if (!active) return false;
        
        Rectangle2D bounds = getBounds(entityPosition);
        return bounds.contains(point.x, point.y);
    }
    
    /**
     * Get the center of the collider
     */
    public Vector2 getCenter(Vector2 entityPosition) {
        Rectangle2D bounds = getBounds(entityPosition);
        return new Vector2(
            (float) (bounds.getMinX() + bounds.getWidth() / 2),
            (float) (bounds.getMinY() + bounds.getHeight() / 2)
        );
    }
    
    @Override
    public void update(float deltaTime) {
        // Collider doesn't need per-frame updates
    }
    
    // ========== GETTERS/SETTERS ==========
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = Math.max(0, width);
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = Math.max(0, height);
    }
    
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }
    
    public Vector2 getOffset() {
        return offset;
    }
    
    public void setOffset(Vector2 offset) {
        this.offset = offset;
    }
    
    public void setOffset(float x, float y) {
        this.offset = new Vector2(x, y);
    }
    
    public boolean isTrigger() {
        return isTrigger;
    }
    
    public void setTrigger(boolean trigger) {
        this.isTrigger = trigger;
    }
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return String.format("Collider[%.1fx%.1f, offset=%s, trigger=%b]", 
            width, height, offset, isTrigger);
    }
}