package com.example.codeplayfx.world.component;


import com.example.codeplayfx.world.physics.Vector2;

/**
 * Transform component handles position, rotation, and scale.
 * Every entity should have a transform.
 */
public class TransformComponent implements Component {

    private Vector2 position;      // World position (grid coordinates)
    private float rotation;        // Rotation in degrees
    private Vector2 scale;         // Scale factor (1.0 = normal size)
    private boolean active;

    /**
     * Create transform at origin
     */
    public TransformComponent() {
        this(Vector2.ZERO);
    }

    /**
     * Create transform at specific position
     */
    public TransformComponent(Vector2 position) {
        this.position = position;
        this.rotation = 0f;
        this.scale = Vector2.ONE;
        this.active = true;
    }

    /**
     * Create transform with position coordinates
     */
    public TransformComponent(float x, float y) {
        this(new Vector2(x, y));
    }

    @Override
    public void update(float deltaTime) {
        // Transform doesn't need per-frame updates
        // Position changes happen through setPosition()
    }

    // Position methods
    public Vector2 getPosition() {
        return position;
    }

    public void setPosition(Vector2 position) {
        this.position = position;
    }

    public void setPosition(float x, float y) {
        this.position = new Vector2(x, y);
    }

    public float getX() {
        return position.x;
    }

    public float getY() {
        return position.y;
    }

    /**
     * Move by an offset
     */
    public void translate(Vector2 offset) {
        this.position = position.add(offset);
    }

    public void translate(float dx, float dy) {
        translate(new Vector2(dx, dy));
    }

    // Rotation methods
    public float getRotation() {
        return rotation;
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
    }

    public void rotate(float degrees) {
        this.rotation += degrees;
    }

    // Scale methods
    public Vector2 getScale() {
        return scale;
    }

    public void setScale(Vector2 scale) {
        this.scale = scale;
    }

    public void setScale(float uniformScale) {
        this.scale = new Vector2(uniformScale, uniformScale);
    }

    public void setScale(float scaleX, float scaleY) {
        this.scale = new Vector2(scaleX, scaleY);
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
        return String.format("Transform[pos=%s, rot=%.1f°, scale=%s]",
                position, rotation, scale);
    }
}
