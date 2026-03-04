package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.render.ShapeType;
import javafx.scene.paint.Color;

/**
 * GoalZone - Level exit/win condition.
 *
 * Properties:
 * - Trigger collider (no physics response)
 * - Triggers level completion on player touch
 * - Can be door, flag, portal, etc.
 *
 * Example usage:
 * GoalZone door = new GoalZone(10, 5, 2, 3);
 * door.setColor(Color.GREEN);
 * door.show();
 */
public class GoalZone extends Entity {

    private float width;
    private float height;
    private boolean isActivated;
    private String nextLevel; // Optional: specific next level to load

    /**
     * Create a goal zone at position with default size (1x1)
     */
    public GoalZone(float x, float y) {
        this(x, y, 1.0f, 1.0f);
    }

    /**
     * Create a goal zone with specific size
     */
    public GoalZone(float x, float y, float width, float height) {
        super(x, y);

        this.width = width;
        this.height = height;
        this.isActivated = false;
        this.nextLevel = null;

        // Add collider as TRIGGER
        addCollider(width, height);
        getCollider().setTrigger(true); // IMPORTANT: Trigger for goal

        // Set up rendering - default flag appearance
        getRenderComponent().setShapeType(ShapeType.RECTANGLE);
        getRenderComponent().setFillColor(Color.GREEN);
        getRenderComponent().setStrokeColor(Color.DARKGREEN);
        getRenderComponent().setStrokeWidth(2);
        getRenderComponent().setSize(width, height);

        this.name = "GoalZone_" + id;
    }

    /**
     * Create a door (rectangle)
     */
    public static GoalZone createDoor(float x, float y, float width, float height) {
        GoalZone door = new GoalZone(x, y, width, height);
        door.setColor(Color.SADDLEBROWN);
        door.getRenderComponent().setShapeType(ShapeType.RECTANGLE);
        return door;
    }

    /**
     * Create a flag (rectangle with triangle top - simplified as rounded rect)
     */
    public static GoalZone createFlag(float x, float y) {
        GoalZone flag = new GoalZone(x, y, 0.8f, 1.5f);
        flag.setColor(Color.GOLD);
        flag.getRenderComponent().setShapeType(ShapeType.ROUNDED_RECTANGLE);
        return flag;
    }

    /**
     * Create a portal (circle)
     */
    public static GoalZone createPortal(float x, float y) {
        GoalZone portal = new GoalZone(x, y, 1.2f, 1.2f);
        portal.setColor(Color.PURPLE);
        portal.getRenderComponent().setShapeType(ShapeType.CIRCLE);
        return portal;
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        // Goals don't need per-frame updates
        // They just wait for player collision

        super.update(deltaTime);
    }

    /**
     * Called when player touches this goal zone
     * This should be called by the physics system on collision
     */
    public void onTouch(BlockPlayer player) {
        if (!active || isActivated || player == null) return;

        Logger.info(name + " reached by player at (" + getX() + ", " + getY() + ")");

        // Mark as activated to prevent multiple triggers
        isActivated = true;

        // Trigger level completion
        onLevelComplete();
    }

    /**
     * Called when level is completed
     */
    private void onLevelComplete() {
        // Visual feedback
        getRenderComponent().setFillColor(Color.GOLD);

        // Log completion
        Logger.info("*** LEVEL COMPLETE! ***");

        // Here you would implement actual level transition
        // This depends on your game manager structure

        // Example: Call a static GameManager method
        // GameManager.completeLevel(nextLevel);

        // Example: Fire an event
        // EventBus.publish(new LevelCompleteEvent(this));

        // For now, just print
        System.out.println("🎉 CONGRATULATIONS! Level complete! 🎉");
    }

    /**
     * Reset the goal zone (for level restart)
     */
    public void reset() {
        isActivated = false;
        getRenderComponent().setFillColor(Color.GREEN);
    }

    // ========== CUSTOMIZATION ==========

    /**
     * Set goal zone color
     */
    public void setColor(Color color) {
        getRenderComponent().setFillColor(color);
    }

    /**
     * Set goal zone color using RGB
     */
    public void setColor(int r, int g, int b) {
        getRenderComponent().setFillColor(r, g, b);
    }

    /**
     * Set goal zone color using name or hex
     */
    public void setColor(String colorNameOrHex) {
        if (colorNameOrHex.startsWith("#")) {
            getRenderComponent().setFillColor(colorNameOrHex);
        } else {
            getRenderComponent().setFillColor(parseColorName(colorNameOrHex));
        }
    }

    /**
     * Set size (updates collider and renderer)
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
     * Set the next level to load when this goal is reached
     */
    public void setNextLevel(String levelName) {
        this.nextLevel = levelName;
    }

    /**
     * Get the next level name
     */
    public String getNextLevel() {
        return nextLevel;
    }

    /**
     * Check if this goal has been activated
     */
    public boolean isActivated() {
        return isActivated;
    }

    /**
     * Parse common color names
     */
    private Color parseColorName(String name) {
        switch (name.toUpperCase()) {
            case "DOOR":
            case "BROWN": return Color.SADDLEBROWN;
            case "FLAG":
            case "GOLD": return Color.GOLD;
            case "PORTAL":
            case "PURPLE": return Color.PURPLE;
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            default: return Color.web(name);
        }
    }

    @Override
    public String toString() {
        return String.format("GoalZone[%s, activated=%b, pos=(%.1f,%.1f)]",
                name, isActivated, getX(), getY());
    }
}