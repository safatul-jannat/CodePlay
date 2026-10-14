package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.render.KillZoneRenderer;
import com.example.codeplayfx.world.physics.Vector2;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * KillZone - Hazardous area with extensive customization options.
 * Extends StaticEntity (immovable) with trigger collider.
 *
 * INHERITANCE HIERARCHY:
 * Entity (base)
 *   └── StaticEntity (immovable, solid by default)
 *        └── KillZone (hazard, trigger by default)
 *
 * What KillZone OVERRIDES from StaticEntity:
 * - Constructor - sets up trigger collider and renderer
 * - update() - adds damage-over-time and pulse logic
 * - show() - auto-adds to world
 *
 * What KillZone USES from StaticEntity:
 * - position methods (getX(), getY(), setPosition())
 * - collision detection (hasCollider(), getCollider())
 * - rendering (getRenderComponent(), setRenderer())
 * - visibility (isVisible(), setVisible())
 * - activation (isActive(), setActive())
 */
public class KillZone extends StaticEntity {

    // ========== INSTANCE FIELDS ==========
    private float width;
    private float height;
    private int damageAmount;
    private float damagePerSecond;
    private boolean instantKill = false;
    private boolean active = true;
    private float pushForce = 0f;
    private Vector2 pushDirection = Vector2.ZERO;

    // Target filtering
    private boolean killPlayers = true;
    private boolean killEnemies = false;
    private boolean killAll = false;
    private List<String> ignoredTags = new ArrayList<>();

    // Tracking objects inside
    private Map<Entity, Float> touchingObjects = new HashMap<>();
    private Map<Entity, Vector2> touchPoints = new HashMap<>();

    // Temporary effects
    private float disableTimer = 0f;
    private float enableTimer = 0f;
    private float safeTimer = 0f;
    private boolean pulseActive = false;
    private float pulseInterval = 0f;
    private float pulseDuration = 0f;
    private float pulseTimer = 0f;
    private Color activeColor = Color.RED;
    private Color inactiveColor = Color.GRAY;

    // Event callbacks
    private Consumer<LivingEntity> onObjectEnterCallback;
    private Consumer<LivingEntity> onObjectExitCallback;
    private Consumer<LivingEntity> onObjectKilledCallback;

    // Debug
    private boolean showBounds = false;
    private Color debugColor = Color.RED;

    // ========== CONSTRUCTORS ==========
    // OVERRIDES: StaticEntity constructor

    public KillZone(float x, float y, float width, float height) {
        this(x, y, width, height, 10);  // Default damage 10
    }

    public KillZone(float x, float y, float width, float height, int damage) {
        // Call StaticEntity constructor (sets position, size, adds collider)
        super(x, y, width, height);

        this.width = width;
        this.height = height;
        this.damageAmount = damage;
        this.damagePerSecond = 0;
        this.instantKill=false;

        // OVERRIDE: Make it a trigger instead of solid
        if (collider != null) {
            collider.setTrigger(true);  // KillZone is a trigger, not solid
        }

        // Set custom renderer
        this.renderer = new KillZoneRenderer();

        // Default appearance (uses StaticEntity's render component)
        getRenderComponent().setSize(width, height);
        getRenderComponent().setFillColor(Color.RED);
        getRenderComponent().setStrokeColor(Color.DARKRED);
        getRenderComponent().setStrokeWidth(1);

        this.name = "KillZone_" + id;

        // Auto-show (like other StaticEntity subclasses)
        show();
    }

    // ========== UPDATE ==========
    // OVERRIDES: StaticEntity.update() to add KillZone-specific logic

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        // Update timers
        updateTimers(deltaTime);

        // Apply damage over time to all touching entities
        if (!instantKill && damagePerSecond > 0) {
            applyDamageOverTime(deltaTime);
        }

        // Pulse effect
        if (pulseActive) {
            updatePulse(deltaTime);
        }

        // Clean up destroyed entities from tracking
        touchingObjects.entrySet().removeIf(e -> !e.getKey().isActive() ||
                (e.getKey() instanceof LivingEntity && !((LivingEntity) e.getKey()).isAlive()));
        touchPoints.entrySet().removeIf(e -> !e.getKey().isActive() ||
                (e.getKey() instanceof LivingEntity && !((LivingEntity) e.getKey()).isAlive()));

        // Call parent update (handles component updates)
        super.update(deltaTime);
    }

    private void updateTimers(float deltaTime) {
        if (disableTimer > 0) {
            disableTimer -= deltaTime;
            if (disableTimer <= 0) {
                setActive(true);
            }
        }

        if (enableTimer > 0) {
            enableTimer -= deltaTime;
            if (enableTimer <= 0) {
                setActive(false);
            }
        }

        if (safeTimer > 0) {
            safeTimer -= deltaTime;
        }
    }

    private void applyDamageOverTime(float deltaTime) {
        for (Map.Entry<Entity, Float> entry : touchingObjects.entrySet()) {
            Entity entity = entry.getKey();

            if (entity instanceof LivingEntity && shouldAffect((LivingEntity) entity)) {
                LivingEntity living = (LivingEntity) entity;

                // Apply continuous damage
                float damageThisFrame = damagePerSecond * deltaTime;
                living.takeDamage(damageThisFrame);

                // Check if killed
                if (!living.isAlive() && onObjectKilledCallback != null) {
                    onObjectKilledCallback.accept(living);
                }
            }
        }
    }

    private void updatePulse(float deltaTime) {
        pulseTimer += deltaTime;
        if (pulseTimer >= pulseInterval) {
            // Toggle active state
            setActive(!active);
            pulseTimer = 0;
        }
    }

    private boolean shouldAffect(LivingEntity entity) {
        if (!active || safeTimer > 0 || entity == null || !entity.isAlive()) return false;

        // Check ignored tags
        for (String tag : ignoredTags) {
            if (entity.getName().toLowerCase().contains(tag.toLowerCase())) {
                return false;
            }
        }

        if (killAll) return true;

        if (killPlayers && entity instanceof BlockPlayer) return true;
        if (killEnemies && entity instanceof Enemy) return true;

        return false;
    }

    // ========== SHOW ==========
    // OVERRIDES: StaticEntity.show() to ensure proper registration

    @Override
    public void show() {
        super.show();  // Call StaticEntity.show() which handles visibility
        UserGame userGame = UserGame.getCurrentGame();
        if (userGame != null) {
            userGame.addEntity(this);  // Add to world
        }
    }

    // ========== COLLISION HANDLING ==========
    // USES: StaticEntity.onCollision() is called by CollisionSystem

    /**
     * Called by CollisionSystem when a LivingEntity enters this killzone
     * This is the main entry point for damage
     */
    public void onTouch(LivingEntity entity) {
        if (!active || entity == null || !entity.isAlive()) return;

        // Check if this entity should be affected
        if (!shouldAffect(entity)) return;

        // Apply damage based on configuration
        if (instantKill) {
            // Kill instantly
            entity.takeDamage(entity.getHealth());
            if (onObjectKilledCallback != null) {
                onObjectKilledCallback.accept(entity);
            }
        }
        else if (damagePerSecond > 0) {
            // Type 2: Damage Over Time - handled in update()
            // This just tracks the entity, damage is applied in applyDamageOverTime()
            if (!touchingObjects.containsKey(entity)) {
                touchingObjects.put(entity, 0f);
                touchPoints.put(entity, getPosition());

                if (onObjectEnterCallback != null) {
                    onObjectEnterCallback.accept(entity);
                }
            }
            // Don't apply damage here - it's done in update()
        }
        else if (damageAmount > 0) {
            // Apply instant damage amount
            entity.takeDamage(damageAmount);
            if (!entity.isAlive() && onObjectKilledCallback != null) {
                onObjectKilledCallback.accept(entity);
            }
        }

        // Apply push force if configured
        if (pushForce > 0 && entity instanceof PhysicalEntity) {
            ((PhysicalEntity) entity).applyForce(pushDirection.multiply(pushForce));
        }

        // Track touch for duration
        if (!touchingObjects.containsKey(entity)) {
            touchingObjects.put(entity, 0f);
            touchPoints.put(entity, getPosition());

            if (onObjectEnterCallback != null) {
                onObjectEnterCallback.accept(entity);
            }
        }

        // Update touch duration
        touchingObjects.put(entity, touchingObjects.get(entity) + 0.016f);
    }

    /**
     * Called when entity exits the killzone
     */
    public void onTouchExit(LivingEntity entity) {
        if (entity == null) return;

        touchingObjects.remove(entity);
        touchPoints.remove(entity);

        if (onObjectExitCallback != null) {
            onObjectExitCallback.accept(entity);
        }
    }

    // ========== TARGET FILTERING ==========
    // NEW methods specific to KillZone

    /**
     * Kill only players (default behavior)
     */
    public KillZone killOnlyPlayers() {
        this.killPlayers = true;
        this.killEnemies = false;
        this.killAll = false;
        return this;
    }

    /**
     * Kill only enemies
     */
    public KillZone killOnlyEnemies() {
        this.killPlayers = false;
        this.killEnemies = true;
        this.killAll = false;
        return this;
    }

    /**
     * Kill all living entities
     */
    public KillZone killAll() {
        this.killPlayers = true;
        this.killEnemies = true;
        this.killAll = true;
        return this;
    }

    /**
     * Ignore entities with specific tag in their name
     */
    public KillZone ignoreTag(String tag) {
        this.ignoredTags.add(tag);
        return this;
    }

    // ========== DAMAGE SETUP ==========

    /**
     * Set instant damage amount (applied once on touch)
     * @param damage Amount of damage dealt immediately when touched
     */
    public KillZone setDamage(int damage) {
        this.damageAmount = damage;
        this.damagePerSecond = 0;   // Turn off damage over time
        this.instantKill = false;    // Turn off instant kill
        return this;
    }

    /**
     * Set damage over time (damage per second while touching)
     * @param dps Damage per second while entity remains in killzone
     */
    public KillZone setDamageOverTime(float dps) {
        this.damageAmount = (int) dps;  // Store for reference
        this.damagePerSecond = dps;
        this.instantKill = false;
        return this;
    }

    /**
     * Set instant kill (entity dies immediately on touch)
     */
    public KillZone setInstantKill() {
        this.instantKill = true;
        this.damageAmount = 0;
        this.damagePerSecond = 0;
        return this;
    }

    /**
     * Clear all damage (safe zone)
     */
    public KillZone setNoDamage() {
        this.damageAmount = 0;
        this.damagePerSecond = 0;
        this.instantKill = false;
        return this;
    }

    /**
     * Push entities instead of damaging them
     */
    public KillZone setPushOnly(float force) {
        return setPushOnly(force, Vector2.UP);
    }

    /**
     * Push entities in specific direction
     */
    public KillZone setPushOnly(float force, Vector2 direction) {
        this.instantKill = false;
        this.damagePerSecond = 0;
        this.damageAmount = 0;
        this.pushForce = force;
        this.pushDirection = direction.normalized();
        return this;
    }

// ========== ACTIVATION CONTROL ==========

    /**
     * Activate the killzone (fluent version)
     */
    public KillZone activate() {
        return setActiveState(true);
    }

    /**
     * Deactivate the killzone (fluent version)
     */
    public KillZone deactivate() {
        return setActiveState(false);
    }

    /**
     * Toggle active state (fluent version)
     */
    public KillZone toggle() {
        return setActiveState(!this.active);
    }

    /**
     * Set active state with fluent return
     */
    public KillZone setActiveState(boolean active) {
        this.active = active;

        // Update appearance
        if (active) {
            getRenderComponent().setFillColor(activeColor);
        } else {
            getRenderComponent().setFillColor(inactiveColor);
        }

        return this;
    }

    /**
     * Override parent's setActive (void return)
     */
    @Override
    public void setActive(boolean active) {
        super.setActive(active);
        this.active = active;
    }

    // ========== TOUCH HELPERS ==========
    // NEW query methods

    /**
     * Check if specific entity is touching this killzone
     */
    public boolean isTouching(Entity entity) {
        return touchingObjects.containsKey(entity);
    }

    /**
     * Get all entities currently touching this killzone
     */
    public List<LivingEntity> getTouchingObjects() {
        List<LivingEntity> result = new ArrayList<>();
        for (Entity e : touchingObjects.keySet()) {
            if (e instanceof LivingEntity) {
                result.add((LivingEntity) e);
            }
        }
        return result;
    }

    /**
     * Check if a point is inside this killzone
     */
    public boolean isPointInside(Vector2 point) {
        return point.x >= getX() && point.x <= getX() + width &&
                point.y >= getY() && point.y <= getY() + height;
    }

    /**
     * Check if a point is inside this killzone (float version)
     */
    public boolean isPointInside(float x, float y) {
        return isPointInside(new Vector2(x, y));
    }

    /**
     * Get how long an entity has been touching this killzone
     */
    public float getTouchDuration(LivingEntity entity) {
        return touchingObjects.getOrDefault(entity, 0f);
    }

    /**
     * Get the point where entity first touched the killzone
     */
    public Vector2 getTouchPoint(Entity entity) {
        return touchPoints.get(entity);
    }

    // ========== STATE QUERIES ==========
    // NEW methods

    /**
     * Check if killzone is active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Check if killzone is currently dangerous
     */
    public boolean isDangerous() {
        return active && safeTimer <= 0;
    }

    /**
     * Get danger level at specific position (0 = safe, 1 = deadly)
     */
    public float getDangerLevel(Vector2 position) {
        if (!active || safeTimer > 0) return 0f;
        if (!isPointInside(position)) return 0f;

        // Use Vector2's distanceTo method directly
        float distanceToCenter = getPosition().distanceTo(getCenter());
        float maxDistance = (float) Math.sqrt(width * width + height * height) / 2;

        return 1.0f - (distanceToCenter / maxDistance);
    }


    // ========== POSITION HELPERS ==========
    // USES: StaticEntity.getX(), getY(), etc.

    /**
     * Get closest point on killzone boundary to given point
     */
    public Vector2 getClosestPoint(Vector2 from) {
        float closestX = Math.max(getX(), Math.min(getX() + width, from.x));
        float closestY = Math.max(getY(), Math.min(getY() + height, from.y));
        return new Vector2(closestX, closestY);
    }

    /**
     * Get center of killzone
     */
    public Vector2 getCenter() {
        return new Vector2(getX() + width / 2, getY() + height / 2);
    }

    /**
     * Get distance from given point to killzone
     */
    public float distanceFrom(Vector2 point) {
        Vector2 closest = getClosestPoint(point);
        return point.distanceTo(closest);
    }

    /**
     * Get safe direction away from killzone
     */
    public Vector2 getSafeDirection(Vector2 from) {
        Vector2 center = getCenter();
        Vector2 away = from.subtract(center);
        return away.normalized();
    }

    /**
     * Check if killzone contains specific entity
     */
    public boolean contains(Entity entity) {
        return touchingObjects.containsKey(entity);
    }

    /**
     * Get number of entities currently in killzone
     */
    public int getObjectCount() {
        return touchingObjects.size();
    }

    /**
     * Get a random point inside the killzone
     */
    public Vector2 getRandomPointInside() {
        float randX = getX() + (float) Math.random() * width;
        float randY = getY() + (float) Math.random() * height;
        return new Vector2(randX, randY);
    }

    // ========== TEMPORARY EFFECTS ==========
    // NEW methods

    /**
     * Disable for specified seconds
     */
    public KillZone disableFor(float seconds) {
        this.disableTimer = seconds;
        setActive(false);
        return this;
    }

    /**
     * Enable for specified seconds
     */
    public KillZone enableFor(float seconds) {
        this.enableTimer = seconds;
        setActive(true);
        return this;
    }

    /**
     * Make safe for specified seconds (no damage)
     */
    public KillZone setSafeFor(float seconds) {
        this.safeTimer = seconds;
        return this;
    }

    /**
     * Make killzone pulse on and off
     */
    public KillZone pulse(float interval, float duration) {
        this.pulseActive = true;
        this.pulseInterval = interval;
        this.pulseDuration = duration;
        this.pulseTimer = 0;
        return this;
    }

    // ========== EVENT SUBSCRIPTIONS ==========
    // NEW callback methods

    /**
     * Callback when entity enters killzone
     */
    public KillZone onObjectEnter(Consumer<LivingEntity> callback) {
        this.onObjectEnterCallback = callback;
        return this;
    }

    /**
     * Callback when entity exits killzone
     */
    public KillZone onObjectExit(Consumer<LivingEntity> callback) {
        this.onObjectExitCallback = callback;
        return this;
    }

    /**
     * Callback when entity is killed by killzone
     */
    public KillZone onObjectKilled(Consumer<LivingEntity> callback) {
        this.onObjectKilledCallback = callback;
        return this;
    }

    /**
     * Clear all event callbacks
     */
    public KillZone clearEvents() {
        this.onObjectEnterCallback = null;
        this.onObjectExitCallback = null;
        this.onObjectKilledCallback = null;
        return this;
    }

    // ========== VISUAL DEBUG ==========
    // NEW debug methods

    /**
     * Show/hide killzone bounds for debugging
     */
    public KillZone showBounds(boolean show) {
        this.showBounds = show;
        return this;
    }

    /**
     * Set debug color
     */
    public KillZone setDebugColor(Color color) {
        this.debugColor = color;
        return this;
    }

    /**
     * Set active color (USES parent's render component)
     */
    public KillZone setActiveColor(Color color) {
        this.activeColor = color;
        if (active) {
            getRenderComponent().setFillColor(color);
        }
        return this;
    }

    /**
     * Set inactive color (USES parent's render component)
     */
    public KillZone setInactiveColor(Color color) {
        this.inactiveColor = color;
        if (!active) {
            getRenderComponent().setFillColor(color);
        }
        return this;
    }

    // ========== CUSTOMIZATION ==========
    // Additional setters

    public void setColor(String colorName) {
        if (colorName.equalsIgnoreCase("RED")) {
            getRenderComponent().setFillColor(Color.RED);
            activeColor = Color.RED;
        } else if (colorName.equalsIgnoreCase("ORANGE")) {
            getRenderComponent().setFillColor(Color.ORANGE);
            activeColor = Color.ORANGE;
        } else if (colorName.equalsIgnoreCase("SILVER")) {
            getRenderComponent().setFillColor(Color.SILVER);
            activeColor = Color.SILVER;
        } else if (colorName.equalsIgnoreCase("BLACK")) {
            getRenderComponent().setFillColor(Color.BLACK);
            activeColor = Color.BLACK;
        } else if (colorName.equalsIgnoreCase("BLUE")) {
            getRenderComponent().setFillColor(Color.BLUE);
            activeColor = Color.BLUE;
        } else if (colorName.equalsIgnoreCase("GREEN")) {
            getRenderComponent().setFillColor(Color.GREEN);
            activeColor = Color.GREEN;
        }
    }

    public float getBlockWidth() {
        return width;
    }

    public float getBlockHeight() {
        return height;
    }

// Add at the end of KillZone class (before toString)

    public int getDamageAmount() {
        return damageAmount;
    }

    public float getDamagePerSecond() {
        return damagePerSecond;
    }

    public boolean isInstantKill() {
        return instantKill;
    }

    public float getPushForce() {
        return pushForce;
    }

    public Vector2 getPushDirection() {
        return pushDirection;
    }

    // ========== TO STRING ==========
    // OVERRIDES: Object.toString()

    @Override
    public String toString() {
        return String.format("KillZone[%s, size=%.1fx%.1f, damage=%d, active=%b]",
                super.toString(), width, height, damageAmount, active);
    }
}