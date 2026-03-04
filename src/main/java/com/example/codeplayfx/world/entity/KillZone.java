package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.render.ShapeType;
import javafx.scene.paint.Color;

/**
 * KillZone - Hazardous area that damages/kills the player on touch.
 *
 * Properties:
 * - Trigger collider (no physics response)
 * - Damages player on contact
 * - Can be lava, spikes, bottomless pits, etc.
 *
 * Example usage:
 * KillZone lava = new KillZone(5, 2, 3, 1, 10);
 * lava.setColor(Color.ORANGE);
 * lava.show();
 */
public class KillZone extends Entity {

    private float width;
    private float height;
    private int damageAmount;
    private boolean destroyPlayerOnTouch; // If true, instant kill

    /**
     * Create a kill zone at position with default size (1x1) and damage 1
     */
    public KillZone(float x, float y) {
        this(x, y, 1.0f, 1.0f, 1);
    }

    /**
     * Create a kill zone with specific size and damage
     * @param damageAmount damage dealt to player (0 = instant kill)
     */
    public KillZone(float x, float y, float width, float height, int damageAmount) {
        super(x, y);

        this.width = width;
        this.height = height;
        this.damageAmount = damageAmount;
        this.destroyPlayerOnTouch = (damageAmount <= 0);

        // Add collider as TRIGGER
        addCollider(width, height);
        getCollider().setTrigger(true); // IMPORTANT: Trigger for hazards

        // Set up rendering - default lava appearance
        getRenderComponent().setShapeType(ShapeType.RECTANGLE);
        getRenderComponent().setFillColor(Color.ORANGERED);
        getRenderComponent().setStrokeColor(Color.DARKRED);
        getRenderComponent().setStrokeWidth(1);
        getRenderComponent().setSize(width, height);

        this.name = "KillZone_" + id;
    }

    /**
     * Create a lava pool
     */
    public static KillZone createLava(float x, float y, float width, float height) {
        KillZone lava = new KillZone(x, y, width, height, 5);
        lava.setColor(Color.ORANGERED);
        lava.getRenderComponent().setShapeType(ShapeType.RECTANGLE);
        return lava;
    }

    /**
     * Create a spike pit
     */
    public static KillZone createSpikes(float x, float y, float width) {
        KillZone spikes = new KillZone(x, y, width, 0.5f, 999); // High damage
        spikes.setColor(Color.SILVER);
        spikes.getRenderComponent().setShapeType(ShapeType.TRIANGLE);
        return spikes;
    }

    /**
     * Create a bottomless pit (instant kill)
     */
    public static KillZone createBottomlessPit(float x, float y, float width) {
        KillZone pit = new KillZone(x, y, width, 0.2f, 0); // 0 damage = instant kill
        pit.setColor(Color.BLACK);
        pit.getRenderComponent().setStrokeColor(Color.DARKGRAY);
        pit.setDestroyPlayerOnTouch(true);
        return pit;
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        // Check collision with player (will be handled by PhysicsSystem)
        // The actual damage logic is in onTouch() which should be called
        // when collision is detected by the physics system

        super.update(deltaTime);
    }

    /**
     * Called when player touches this kill zone
     * This should be called by the physics system on collision
     */
    public void onTouch(BlockPlayer player) {
        if (!active || player == null || !player.isAlive()) return;

        Logger.debug(name + " touched player at (" + getX() + ", " + getY() + ")");

        if (destroyPlayerOnTouch) {
            // Instant kill
            player.takeDamage(player.getHealth()); // Kill instantly
            Logger.info("Player instantly killed by " + name);
        } else {
            // Apply damage
            player.takeDamage(damageAmount);
            Logger.info("Player took " + damageAmount + " damage from " + name);
        }

        // Optional: Add visual/audio feedback
        onDamageEffect();
    }

    /**
     * Visual/audio feedback when damage occurs
     */
    private void onDamageEffect() {
        // Could flash red, play sound, etc.
        // For now, just log
    }

    // ========== CUSTOMIZATION ==========

    /**
     * Set kill zone color
     */
    public void setColor(Color color) {
        getRenderComponent().setFillColor(color);
    }

    /**
     * Set kill zone color using RGB
     */
    public void setColor(int r, int g, int b) {
        getRenderComponent().setFillColor(r, g, b);
    }

    /**
     * Set kill zone color using name or hex
     */
    public void setColor(String colorNameOrHex) {
        if (colorNameOrHex.startsWith("#")) {
            getRenderComponent().setFillColor(colorNameOrHex);
        } else {
            getRenderComponent().setFillColor(parseColorName(colorNameOrHex));
        }
    }

    /**
     * Set damage amount
     */
    public void setDamageAmount(int damage) {
        this.damageAmount = Math.max(0, damage);
        this.destroyPlayerOnTouch = (damage <= 0);
    }

    /**
     * Get damage amount
     */
    public int getDamageAmount() {
        return damageAmount;
    }

    /**
     * Set whether this kill zone destroys player instantly
     */
    public void setDestroyPlayerOnTouch(boolean destroy) {
        this.destroyPlayerOnTouch = destroy;
        if (destroy) {
            this.damageAmount = 0;
        }
    }

    /**
     * Check if this is an instant kill zone
     */
    public boolean isInstantKill() {
        return destroyPlayerOnTouch;
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
     * Parse common color names
     */
    private Color parseColorName(String name) {
        switch (name.toUpperCase()) {
            case "LAVA":
            case "ORANGE": return Color.ORANGE;
            case "RED": return Color.RED;
            case "ORANGERED": return Color.ORANGERED;
            case "SPIKE":
            case "SILVER": return Color.SILVER;
            case "PIT":
            case "BLACK": return Color.BLACK;
            default: return Color.web(name);
        }
    }

    @Override
    public String toString() {
        return String.format("KillZone[%s, damage=%d, instant=%b, pos=(%.1f,%.1f)]",
                name, damageAmount, destroyPlayerOnTouch, getX(), getY());
    }
}