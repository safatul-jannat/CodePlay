package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.world.physics.Vector2;
import com.example.codeplayfx.world.render.EnemyRenderer;
import com.example.codeplayfx.world.render.ShapeType;
import javafx.scene.paint.Color;

/**
 * Enemy - A basic enemy entity that users can interact with.
 *
 * Example usage:
 * Enemy enemy = new Enemy(10, 5);
 * enemy.show();
 *
 * if (player.isColliding(enemy)) {
 *     player.takeDamage(10);
 * }
 */
public class Enemy extends LivingEntity {

    private float moveSpeed = 0.1f;
    private Vector2 moveDirection;
    private boolean isPatrolling = false;

    /**
     * Create enemy at origin
     */
    public Enemy() {
        this(0, 0);
    }

    /**
     * Create enemy at specific position
     */
    public Enemy(float x, float y) {
        super(x, y, 50); // 50 health by default

        this.moveDirection = Vector2.RIGHT; // Start moving right
        this.friction = 0.95f;
        this.mass = 1.0f;
        this.useGravity = true;

        // Add collider (1x1 grid cell)
        addCollider(1.0f, 1.0f);

        // Set up rendering
        this.renderer = new EnemyRenderer();
        this.renderComponent = getRenderComponent();

        // Default appearance: Red circle, 1x1 size (different from player)
        renderComponent.setShapeType(ShapeType.CIRCLE);
        renderComponent.setFillColor(Color.RED);
        renderComponent.setStrokeColor(Color.DARKRED);
        renderComponent.setStrokeWidth(2);
        renderComponent.setSize(1.0f, 1.0f);

        this.name = "Enemy_" + id;
    }

    /**
     * Create enemy with position and color (convenience constructor)
     */
    public Enemy(float x, float y, String colorName) {
        this(x, y);
        setColor(colorName);
    }

    // ========== MOVEMENT ==========

    /**
     * Make enemy patrol back and forth
     */
    public void startPatrol() {
        isPatrolling = true;
    }

    /**
     * Stop patrolling
     */
    public void stopPatrol() {
        isPatrolling = false;
        stop();
    }

    /**
     * Set movement direction
     */
    public void setMoveDirection(Vector2 direction) {
        this.moveDirection = direction.normalized();
    }

    /**
     * Move in current direction
     */
    public void moveForward() {
        addVelocity(moveDirection.multiply(moveSpeed));
    }

    /**
     * Reverse direction
     */
    public void reverseDirection() {
        moveDirection = moveDirection.multiply(-1);
    }

    // ========== AI UPDATE ==========

    @Override
    public void update(float deltaTime) {
        // Simple patrol AI
        if (isPatrolling && alive) {
            moveForward();
        }

        // Update physics
        super.update(deltaTime);
    }

    // ========== CUSTOMIZATION ==========

    public void setMoveSpeed(float speed) {
        this.moveSpeed = Math.max(0, speed);
    }

    public float getMoveSpeed() {
        return moveSpeed;
    }

    public boolean isPatrolling() {
        return isPatrolling;
    }

    // ========== APPEARANCE CUSTOMIZATION ==========

    /**
     * Set enemy color
     */
    public void setColor(Color color) {
        getRenderComponent().setFillColor(color);
    }

    /**
     * Set enemy color using RGB
     */
    public void setColor(int r, int g, int b) {
        getRenderComponent().setFillColor(r, g, b);
    }

    /**
     * Set enemy color using color name or hex
     * Supports: "RED", "BLUE", "GREEN", etc. or "#FF5733"
     */
    public void setColor(String colorNameOrHex) {
        if (colorNameOrHex.startsWith("#")) {
            getRenderComponent().setFillColor(colorNameOrHex);
        } else {
            Color color = parseColorName(colorNameOrHex);
            getRenderComponent().setFillColor(color);
        }
    }

    /**
     * Parse common color names
     */
    private Color parseColorName(String name) {
        switch (name.toUpperCase()) {
            case "RED": return Color.RED;
            case "BLUE": return Color.BLUE;
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "ORANGE": return Color.ORANGE;
            case "PURPLE": return Color.PURPLE;
            case "PINK": return Color.PINK;
            case "CYAN": return Color.CYAN;
            case "MAGENTA": return Color.MAGENTA;
            case "LIME": return Color.LIME;
            case "BROWN": return Color.BROWN;
            case "GRAY": return Color.GRAY;
            case "BLACK": return Color.BLACK;
            case "WHITE": return Color.WHITE;
            case "GOLD": return Color.GOLD;
            case "SILVER": return Color.SILVER;
            default:
                return Color.web(name);
        }
    }

    /**
     * Set enemy size
     */
    public void setSize(float width, float height) {
        getRenderComponent().setSize(width, height);
        if (hasCollider()) {
            getCollider().setSize(width, height);
        }
    }

    /**
     * Set enemy shape
     */
    public void setShape(ShapeType shape) {
        getRenderComponent().setShapeType(shape);
    }

    /**
     * Load sprite for enemy
     */
    public void loadSprite(String imagePath) {
        getRenderComponent().loadImage(imagePath);
    }

    @Override
    protected void onDeath() {
        super.onDeath();
        stopPatrol();
    }
}