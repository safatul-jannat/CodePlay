package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.component.TransformComponent;
import com.example.codeplayfx.world.render.EnemyRenderer;
import com.example.codeplayfx.world.render.ShapeType;
import javafx.scene.paint.Color;

/**
 * Enemy - An automatic moving entity that damages the player on contact.
 *
 * Extends LivingEntity so it has health, can take damage, and can die.
 * Gravity is disabled by default — enemies patrol freely in space.
 *
 * Movement Patterns:
 *   STRAIGHT  → patrols back and forth between minX and maxX
 *   ANGULAR   → orbits in a circle around (centerX, centerY)
 *
 * User API example:
 * <pre>
 *   Enemy enemy = new Enemy(5, 5);
 *   enemy.setPattern(Enemy.Pattern.STRAIGHT);
 *   enemy.setBounds(2, 10);
 *   enemy.setSpeed(2.0f);
 *   enemy.setDamage(10);
 *   enemy.setDamageCooldown(1.5f);  // seconds between damage ticks
 *   enemy.show();
 * </pre>
 */
public class Enemy extends LivingEntity {

    // ========== MOVEMENT PATTERN ==========

    public enum Pattern {
        STRAIGHT,   // Patrol left↔right between two X bounds
        ANGULAR     // Orbit in a circle around a centre point
    }

    private Pattern pattern = Pattern.STRAIGHT;

    // ========== CORE STATS ==========

    private float speed  = 1.0f;
    private int   damage = 10;

    // ========== DAMAGE COOLDOWN ==========
    // Prevents dealing damage every single frame while overlapping.
    // Tracked here so CollisionSystem can read/write it per-enemy.

    private float damageCooldown     = 1.0f;   // seconds between hits (user-configurable)
    private float damageCooldownTimer = 0f;     // counts DOWN to 0; damage fires when <= 0

    // ========== STRAIGHT MOVEMENT STATE ==========

    private float minX       = 0f;
    private float maxX       = 10f;
    private boolean movingRight = true;

    // ========== ANGULAR MOVEMENT STATE ==========

    private float centerX = 0f;
    private float centerY = 0f;
    private float radius  = 3f;
    private float angle   = 0f;   // current angle in radians

    // ========== CONSTRUCTOR ==========

    /**
     * Create an enemy at the given grid position.
     * Gravity is disabled — enemy floats and patrols freely.
     */
    public Enemy(float x, float y) {
        super(x, y, 100);

        // Enemies patrol freely — no gravity
        this.useGravity = false;
        this.friction   = 1.0f;   // No friction drag on patrol movement
        this.mass       = 1.0f;

        // Collider — 1×1 grid cell, same as BlockPlayer
        addCollider(1.0f, 1.0f);

        // Renderer
        this.renderer       = new EnemyRenderer();
        this.renderComponent = getRenderComponent();

        // Default appearance: red circle with dark-red outline
        renderComponent.setShapeType(ShapeType.CIRCLE);
        renderComponent.setFillColor(Color.RED);
        renderComponent.setStrokeColor(Color.DARKRED);
        renderComponent.setStrokeWidth(2);
        renderComponent.setSize(1.0f, 1.0f);

        // Health
        this.maxHealth = 100;
        this.health    = 100;

        this.name = "Enemy_" + id;
    }

    // ========== SHOW ==========

    /**
     * Make the enemy visible and add it to the current game world.
     * Call this after configuring the enemy.
     */
    @Override
    public void show() {
        super.show();
        UserGame.getCurrentGame().addEntity(this);
    }

    // ========== UPDATE ==========

    /**
     * Called every frame by the engine.
     * Moves the enemy according to its pattern and ticks the damage cooldown.
     */
    @Override
    public void update(float deltaTime) {
        if (!active || !alive) return;

        // Tick damage cooldown down — CollisionSystem checks canDamage()
        if (damageCooldownTimer > 0f) {
            damageCooldownTimer -= deltaTime;
        }

        // Move according to pattern
        TransformComponent transform = getComponent(TransformComponent.class);
        switch (pattern) {
            case STRAIGHT:
                moveStraight(transform);
                break;
            case ANGULAR:
                moveAngular(transform, deltaTime);
                break;
        }

        // Run base LivingEntity/PhysicalEntity update (component updates, death check)
        super.update(deltaTime);
    }

    // ========== MOVEMENT IMPLEMENTATIONS ==========

    /**
     * Patrol left and right between minX and maxX.
     * Speed is in grid-units per frame (frame-rate independent feel is
     * achieved by keeping speed small; multiply by deltaTime if needed).
     */
    private void moveStraight(TransformComponent transform) {
        if (movingRight) {
            transform.translate(speed * 0.05f, 0);
            if (transform.getX() >= maxX) {
                transform.setPosition(maxX, transform.getY());
                movingRight = false;
            }
        } else {
            transform.translate(-speed * 0.05f, 0);
            if (transform.getX() <= minX) {
                transform.setPosition(minX, transform.getY());
                movingRight = true;
            }
        }
    }

    /**
     * Orbit around (centerX, centerY) at the given radius.
     * Angle advances by speed each frame.
     */
    private void moveAngular(TransformComponent transform, float deltaTime) {
        angle += speed * 0.02f;

        float newX = centerX + (float) Math.cos(angle) * radius;
        float newY = centerY + (float) Math.sin(angle) * radius;

        transform.setPosition(newX, newY);
    }

    // ========== DAMAGE COOLDOWN HELPERS ==========
    // Called by CollisionSystem — not part of the public user API.

    /**
     * Returns true when this enemy is allowed to deal damage.
     * (Cooldown timer has expired.)
     */
    public boolean canDamage() {
        return damageCooldownTimer <= 0f;
    }

    /**
     * Reset the cooldown timer after dealing damage.
     * Called by CollisionSystem immediately after applying damage.
     */
    public void resetDamageCooldown() {
        damageCooldownTimer = damageCooldown;
    }

    // ========== DEATH ==========

    /**
     * When this enemy dies: hide it and remove it from the world.
     */
    @Override
    protected void onDeath() {
        hide();
        // Deactivate so CollisionSystem and PhysicsSystem skip it
        this.active = false;
    }

    // ========== USER API — CONFIGURATION ==========

    /**
     * Set the movement pattern (STRAIGHT or ANGULAR).
     */
    public void setPattern(Pattern pattern) {
        this.pattern = pattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    /**
     * Set patrol boundaries for STRAIGHT pattern.
     * @param minX left  boundary (grid X)
     * @param maxX right boundary (grid X)
     */
    public void setBounds(float minX, float maxX) {
        this.minX = minX;
        this.maxX = maxX;
    }

    /**
     * Set orbit parameters for ANGULAR pattern.
     * The enemy orbits around (centerX, centerY) at the given radius.
     * centerY defaults to the enemy's spawn Y if you only need a horizontal orbit.
     *
     * @param centerX X of the orbit centre (grid units)
     * @param radius  orbit radius (grid units)
     */
    public void setOrbit(float centerX, float radius) {
        this.centerX = centerX;
        this.centerY = getY();   // default: same height as spawn
        this.radius  = radius;
    }

    /**
     * Set orbit with explicit centre Y.
     *
     * @param centerX X of the orbit centre (grid units)
     * @param centerY Y of the orbit centre (grid units)
     * @param radius  orbit radius (grid units)
     */
    public void setOrbit(float centerX, float centerY, float radius) {
        this.centerX = centerX;
        this.centerY = centerY;
        this.radius  = radius;
    }

    /**
     * Set movement speed.
     * For STRAIGHT: higher = faster patrol.
     * For ANGULAR:  higher = faster orbit.
     */
    public void setSpeed(float speed) {
        this.speed = Math.max(0.1f, speed);
    }

    public float getSpeed() {
        return speed;
    }

    /**
     * Set how much damage the enemy deals to the player per hit.
     */
    public void setDamage(int damage) {
        this.damage = Math.max(0, damage);
    }

    public int getDamage() {
        return damage;
    }

    /**
     * Set the cooldown (in seconds) between successive damage events.
     * Default is 1.0 second.
     * Example: enemy.setDamageCooldown(0.5f)  → hits twice per second
     *          enemy.setDamageCooldown(2.0f)  → hits once every 2 seconds
     */
    public void setDamageCooldown(float seconds) {
        this.damageCooldown = Math.max(0.05f, seconds);  // minimum 50 ms
    }

    public float getDamageCooldown() {
        return damageCooldown;
    }

    // ========== APPEARANCE API ==========

    /**
     * Change the enemy's fill colour.
     */
    public void setColor(Color color) {
        getRenderComponent().setFillColor(color);
    }

    public void setColor(String colorNameOrHex) {
        if (colorNameOrHex.startsWith("#")) {
            getRenderComponent().setFillColor(colorNameOrHex);
        } else {
            getRenderComponent().setFillColor(parseColorName(colorNameOrHex));
        }
    }

    public void setColor(int r, int g, int b) {
        getRenderComponent().setFillColor(r, g, b);
    }

    /**
     * Change the enemy's size (also updates collider).
     */
    public void setSize(float width, float height) {
        getRenderComponent().setSize(width, height);
        if (hasCollider()) {
            getCollider().setSize(width, height);
        }
    }

    /**
     * Switch between CIRCLE and RECTANGLE shape.
     */
    public void setShape(ShapeType shapeType) {
        getRenderComponent().setShapeType(shapeType);
    }

    /**
     * Load a sprite image instead of the default coloured shape.
     */
    public void loadSprite(String imagePath) {
        getRenderComponent().loadImage(imagePath);
    }

    // ========== INTERNAL HELPERS ==========

    private Color parseColorName(String name) {
        switch (name.toUpperCase()) {
            case "RED":     return Color.RED;
            case "BLUE":    return Color.BLUE;
            case "GREEN":   return Color.GREEN;
            case "YELLOW":  return Color.YELLOW;
            case "ORANGE":  return Color.ORANGE;
            case "PURPLE":  return Color.PURPLE;
            case "PINK":    return Color.PINK;
            case "CYAN":    return Color.CYAN;
            case "MAGENTA": return Color.MAGENTA;
            case "LIME":    return Color.LIME;
            case "BROWN":   return Color.BROWN;
            case "GRAY":    return Color.GRAY;
            case "BLACK":   return Color.BLACK;
            case "WHITE":   return Color.WHITE;
            case "GOLD":    return Color.GOLD;
            default:        return Color.web(name);
        }
    }
}