package com.example.codeplayfx.world.entity;

import javafx.scene.paint.Color;
import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.component.Component;
import com.example.codeplayfx.world.render.GroundRenderer;

/**
 * Ground - Static immovable platform that entities collide with.
 *
 * Features:
 * - No movement/physics (static)
 * - Solid collision detection
 * - Customizable size and color
 * - Automatically appears when created with 'new'
 *
 * Users create ground like:
 * Ground floor = new Ground(0, 0, 20, 1);  // Automatically appears!
 * floor.setColor(Color.DARKGRAY);
 */
public class Ground extends Entity {

    protected float width;
    protected float height;
    protected Color color;

    /**
     * Create ground at position (1x1 cell)
     * Automatically adds to world and shows
     */
    public Ground(float x, float y) {
        this(x, y, 1.0f, 1.0f);
    }

    /**
     * Create ground with custom size
     * Automatically adds to world and shows
     * @param x grid X coordinate
     * @param y grid Y coordinate
     * @param width width in grid cells
     * @param height height in grid cells
     */
    public Ground(float x, float y, float width, float height) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.color = Color.DARKGRAY;

        System.out.println("Ground constructor: x=" + x + ", y=" + y +
                ", width=" + width + ", height=" + height);

        initializeGround();
        show();
    }

    /**
     * Create ground with custom color
     * Automatically adds to world and shows
     */
    public Ground(float x, float y, float width, float height, Color color) {
        super(x, y);
        this.width = width;
        this.height = height;
        this.color = color;

        System.out.println("Ground constructor: x=" + x + ", y=" + y +
                ", width=" + width + ", height=" + height +
                ", color=" + color);

        initializeGround();
        show();
    }

    /**
     * Initialize ground properties
     */
    private void initializeGround() {
        // Add collider for solid collision
        addCollider(width, height);
        if (collider != null) {
            collider.setTrigger(false);
            collider.setOffset(0, 0);
            System.out.println("GROUND COLLIDER: width=" + collider.getWidth() +
                    ", height=" + collider.getHeight() +
                    ", position=(" + getX() + "," + getY() + ")");
        }

        // Setup render component
        getRenderComponent().setWidth(width);
        getRenderComponent().setHeight(height);
        getRenderComponent().setFillColor(color);
        getRenderComponent().setStrokeColor(color.darker());
        getRenderComponent().setStrokeWidth(1.0F);

        setRenderer(new GroundRenderer());
    }

    /**
     * Override show to add to world automatically
     */
    @Override
    public void show() {
        super.show();
        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame != null) {
            currentGame.addEntity(this);
            System.out.println("Ground added to world");
        }
    }

    /**
     * Ground doesn't need updates (static)
     */
    @Override
    public void update(float deltaTime) {
        // Ground is static - no movement or physics updates
        // Only update components if needed (like animations)
        if (active) {
            for (Component component : components.values()) {
                if (component.isActive()) {
                    component.update(deltaTime);
                }
            }
        }
    }

    // ========== GROUND-SPECIFIC METHODS ==========

    /**
     * Change ground color
     */
    public void setColor(Color color) {
        this.color = color;
        if (renderComponent != null) {
            renderComponent.setFillColor(color);
            renderComponent.setStrokeColor(color.darker());
        }
    }

    /**
     * Get current color
     */
    public Color getColor() {
        return color;
    }

    /**
     * Get ground width in grid cells
     */
    @Override
    public float getWidth() {
        return width;
    }

    /**
     * Get ground height in grid cells
     */
    @Override
    public float getHeight() {
        return height;
    }

    /**
     * Resize ground
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;

        // Update collider
        if (collider != null) {
            collider.setWidth(width);
            collider.setHeight(height);
        }

        // Update render component
        if (renderComponent != null) {
            renderComponent.setWidth(width);
            renderComponent.setHeight(height);
        }
    }

    /**
     * Check if this ground is solid
     */
    public boolean isSolid() {
        return collider != null && !collider.isTrigger();
    }

    /**
     * Make this ground a trigger (entities pass through)
     * Useful for things like pressure plates
     */
    public void setAsTrigger() {
        if (collider != null) {
            collider.setTrigger(true);
        }
    }

    /**
     * Make this ground solid again
     */
    public void setAsSolid() {
        if (collider != null) {
            collider.setTrigger(false);
        }
    }

    /**
     * Check if a point is inside this ground
     */
    public boolean containsPoint(float x, float y) {
        return x >= getX() && x <= getX() + width &&
                y >= getY() && y <= getY() + height;
    }

    /**
     * Check if another entity is colliding with this ground
     */
    public boolean isCollidingWith(Entity other) {
        return isColliding(other);
    }

    @Override
    public String toString() {
        return String.format("Ground[%s, size=%.1fx%.1f, color=%s]",
                super.toString(), width, height, color);
    }

    @Override
    public float getX() {
        return transform.getX();
    }

    @Override
    public float getY() {
        return transform.getY();
    }
}