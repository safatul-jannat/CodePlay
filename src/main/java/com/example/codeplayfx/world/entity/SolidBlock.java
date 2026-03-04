package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.world.render.ShapeType;
import javafx.scene.paint.Color;

/**
 * SolidBlock - Immovable obstacle that entities collide with.
 *
 * Properties:
 * - Non-trigger collider (physical collision)
 * - Fixed position
 * - Can be colored/textured
 *
 * Example usage:
 * SolidBlock wall = new SolidBlock(5, 3, 2, 1);
 * wall.setColor(Color.GRAY);
 * wall.show();
 */
public class SolidBlock extends Entity {

    private float width;
    private float height;

    /**
     * Create a solid block at position with default size (1x1)
     */
    public SolidBlock(float x, float y) {
        this(x, y, 1.0f, 1.0f);
    }

    /**
     * Create a solid block with specific size
     * @param width block width in grid units
     * @param height block height in grid units
     */
    public SolidBlock(float x, float y, float width, float height) {
        super(x, y);

        this.width = width;
        this.height = height;

        // Add collider (NOT a trigger - physical collision)
        addCollider(width, height);
        getCollider().setTrigger(false); // Explicitly set as non-trigger

        // Set up rendering
        getRenderComponent().setShapeType(ShapeType.RECTANGLE);
        getRenderComponent().setFillColor(Color.GRAY);
        getRenderComponent().setStrokeColor(Color.DARKGRAY);
        getRenderComponent().setStrokeWidth(1);
        getRenderComponent().setSize(width, height);

        this.name = "SolidBlock_" + id;
    }

    /**
     * Create a solid block with position, size and color
     */
    public SolidBlock(float x, float y, float width, float height, String colorName) {
        this(x, y, width, height);
        setColor(colorName);
    }

    /**
     * Create a solid block with position and size (width/height as separate params for clarity)
     */
    public static SolidBlock createWall(float x, float y, float width, float height) {
        return new SolidBlock(x, y, width, height);
    }

    /**
     * Create a ground/platform (horizontal block)
     */
    public static SolidBlock createGround(float x, float y, float length) {
        return new SolidBlock(x, y, length, 1.0f);
    }

    /**
     * Create a wall (vertical block)
     */
    public static SolidBlock createWall(float x, float y, float height) {
        return new SolidBlock(x, y, 1.0f, height);
    }

    @Override
    public void update(float deltaTime) {
        // Solid blocks don't move or update
        // They just exist for collision
        super.update(deltaTime);
    }

    // ========== CUSTOMIZATION ==========

    /**
     * Set block color
     */
    public void setColor(Color color) {
        getRenderComponent().setFillColor(color);
    }

    /**
     * Set block color using RGB
     */
    public void setColor(int r, int g, int b) {
        getRenderComponent().setFillColor(r, g, b);
    }

    /**
     * Set block color using name or hex
     */
    public void setColor(String colorNameOrHex) {
        if (colorNameOrHex.startsWith("#")) {
            getRenderComponent().setFillColor(colorNameOrHex);
        } else {
            getRenderComponent().setFillColor(parseColorName(colorNameOrHex));
        }
    }

    /**
     * Set texture/image for this block
     */
    public void setTexture(String imagePath) {
        getRenderComponent().loadImage(imagePath);
    }

    /**
     * Set block size (updates collider and renderer)
     */
    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        getRenderComponent().setSize(width, height);
        if (hasCollider()) {
            getCollider().setSize(width, height);
        }
    }

    /**
     * Get block width
     */
    public float getBlockWidth() {
        return width;
    }

    /**
     * Get block height
     */
    public float getBlockHeight() {
        return height;
    }

    /**
     * Parse common color names
     */
    private Color parseColorName(String name) {
        switch (name.toUpperCase()) {
            case "GRAY": return Color.GRAY;
            case "DARKGRAY": return Color.DARKGRAY;
            case "LIGHTGRAY": return Color.LIGHTGRAY;
            case "BROWN": return Color.BROWN;
            case "SADDLEBROWN": return Color.SADDLEBROWN;
            case "STONE": return Color.rgb(136, 140, 141);
            case "BRICK": return Color.rgb(178, 84, 54);
            case "WOOD": return Color.rgb(156, 102, 68);
            case "DIRT": return Color.rgb(124, 92, 70);
            case "GRASS": return Color.rgb(124, 252, 0);
            default: return Color.web(name);
        }
    }

    @Override
    public String toString() {
        return String.format("SolidBlock[%s, size=%.1fx%.1f, pos=(%.1f,%.1f)]",
                name, width, height, getX(), getY());
    }
}