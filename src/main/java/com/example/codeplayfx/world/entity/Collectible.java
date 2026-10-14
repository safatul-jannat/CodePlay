package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.render.CollectibleRenderer;
import com.example.codeplayfx.world.render.ShapeType;
import com.example.codeplayfx.world.physics.Vector2;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.BooleanSupplier;

public class Collectible extends Entity {

    public enum Type {
        COIN,
        GEM,
        POWERUP
    }

    public enum PowerupType {
        HEALTH,
        SPEED,
        INVINCIBILITY,
        DOUBLE_JUMP
    }

    // ========== STATIC FIELDS ==========
    private static final Random random = new Random();

    // ========== INSTANCE FIELDS ==========
    private Type type;
    private PowerupType powerupType;
    private int pointValue;
    private float scale = 1.0f;
    private boolean collected = false;
    private String collectionId;          // For set collection (e.g., "gemSet1")
    private String doorId; // For keys

    // Who can collect
    private boolean collectibleByPlayers = true;
    private boolean collectibleByEnemies = false;
    private boolean collectibleByAll = false;
    private List<String> allowedTags = new ArrayList<>();
    private List<Entity> ignoredCollectors = new ArrayList<>();
    private BooleanSupplier customCondition = null;

    // Collection behavior
    private boolean destroyOnCollect = true;
    private float respawnTime = 0f;
    private float respawnTimer = 0f;
    private Entity lastCollector = null;

    // Visual effects
    private boolean floatAnimation = false;
    private boolean rotateAnimation = false;
    private boolean pulseAnimation = false;
    private float floatOffset = 0f;
    private float rotation = 0f;
    private float pulseScale = 1.0f;
    private float pulseMin = 0.8f;
    private float pulseMax = 1.2f;
    private float pulseSpeed = 2.0f;
    private boolean showBeam = false;
    private boolean showParticles = false;
    private Color glowColor = Color.GOLD;

    // Attraction
    private boolean attractEnabled = false;
    private float attractRadius = 3f;
    private float attractSpeed = 5f;
    private BlockPlayer targetPlayer = null;

    // Spawn area
    private Vector2 spawnAreaMin;
    private Vector2 spawnAreaMax;
    private float minDistanceFromPlayer = 0f;
    private boolean avoidWalls = false;
    private boolean groundOnly = false;
    private float minDistanceFromOthers = 0f;
    private int maxSpawnAttempts = 10;

    // Event callbacks
    private Consumer<Entity> onCollectedCallback;
    private Consumer<BlockPlayer> onCollectedByPlayerCallback;
    private Runnable onExpiredCallback;
    private Runnable onRespawnedCallback;

    // Chain reactions
    private String chainCollectibleId;
    private String unlockDoorId;
    private String triggerEventName;

    // ========== CONSTRUCTORS ==========

    public Collectible(float x, float y, Type type) {
        super(x, y);
        this.type = type;

        switch (type) {
            case COIN:    this.pointValue = 10; break;
            case GEM:     this.pointValue = 50; break;
            case POWERUP: this.pointValue = 0;  break;
            default:      this.pointValue = 10;
        }

        setupAppearance();
        setupCollider();
        this.renderer = new CollectibleRenderer();
    }

    public Collectible(float x, float y) {
        this(x, y, Type.COIN);
    }

    // ========== UPDATE ==========

    @Override
    public void update(float deltaTime) {
        if (collected && respawnTimer > 0) {
            respawnTimer -= deltaTime;
            if (respawnTimer <= 0) {
                respawn();
            }
        }

        if (!collected && isVisible()) {
            // Animations
            if (floatAnimation) {
                floatOffset = (float) Math.sin(System.currentTimeMillis() * 0.003) * 0.2f;
                setPosition(getX(), getY() + floatOffset * deltaTime);
            }

            if (rotateAnimation) {
                rotation += 100f * deltaTime;
            }

            if (pulseAnimation) {
                pulseScale = pulseMin + (float) (Math.sin(System.currentTimeMillis() * pulseSpeed) + 1) / 2 * (pulseMax - pulseMin);
                getRenderComponent().setWidth(0.7f * pulseScale);
                getRenderComponent().setHeight(0.7f * pulseScale);
            }

            // Attraction to player
            if (attractEnabled && targetPlayer != null) {
                float distance = getPosition().distanceTo(targetPlayer.getPosition());
                if (distance <= attractRadius) {
                    Vector2 direction = targetPlayer.getPosition().subtract(getPosition()).normalized();
                    float speed = attractSpeed * (1 - distance / attractRadius);
                    setPosition(getX() + direction.x * speed * deltaTime * 10,
                            getY() + direction.y * speed * deltaTime * 10);
                }
            }
        }

        super.update(deltaTime);
    }

    // ========== APPEARANCE ==========

    private void setupAppearance() {
        RenderComponent render = getRenderComponent();
        render.setWidth(0.7f);
        render.setHeight(0.7f);

        switch (type) {
            case COIN:
                render.setFillColor(Color.GOLD);
                render.setStrokeColor(Color.DARKORANGE);
                render.setStrokeWidth(2);
                render.setShapeType(ShapeType.CIRCLE);
                break;
            case GEM:
                render.setFillColor(Color.CYAN);
                render.setStrokeColor(Color.DEEPSKYBLUE);
                render.setStrokeWidth(2);
                render.setShapeType(ShapeType.CIRCLE);
                break;
            case POWERUP:
                render.setFillColor(Color.MAGENTA);
                render.setStrokeColor(Color.PURPLE);
                render.setStrokeWidth(2);
                render.setShapeType(ShapeType.ROUNDED_RECTANGLE);
                break;
        }
    }

    private void setupCollider() {
        addCollider(0.5f, 0.5f);
        getCollider().setTrigger(true);
    }

    // ========== SPAWN METHODS ==========

    public static void spawnSequence(Collectible... collectibles) {
        if (collectibles == null || collectibles.length == 0) return;
        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            game.addToSequence(collectibles);
        }
    }

    public static void spawnRandom(int count) {
        spawnRandom(count, Type.COIN);
    }

    public static void spawnRandom(int count, Type type) {
        if (count <= 0) return;
        UserGame game = UserGame.getCurrentGame();
        if (game == null) return;

        int gridWidth  = 14;
        int gridHeight = 9;
        if (game.getWorld() != null) {
            gridWidth  = game.getWorld().getCoordinateSystem().getGridWidth()  - 2;
            gridHeight = game.getWorld().getCoordinateSystem().getGridHeight() - 2;
        }

        Collectible[] coins = new Collectible[count];
        for (int i = 0; i < count; i++) {
            float x = 1 + random.nextInt(Math.max(1, gridWidth  - 1));
            float y = 1 + random.nextInt(Math.max(1, gridHeight - 1));
            coins[i] = new Collectible(x, y, type);
        }
        game.addToSequence(coins);
    }

    // ========== RANDOM POSITION HELPERS ==========

    /**
     * Spawn at random position within rectangle bounds
     */
    public Collectible spawnAtRandomPosition(Vector2 min, Vector2 max) {
        this.spawnAreaMin = min;
        this.spawnAreaMax = max;
        return spawnAtValidPosition();
    }

    /**
     * Spawn at random position within circle
     */
    public Collectible spawnAtRandomPosition(Vector2 center, float radius) {
        this.spawnAreaMin = new Vector2(center.x - radius, center.y - radius);
        this.spawnAreaMax = new Vector2(center.x + radius, center.y + radius);
        return spawnAtValidPosition();
    }

    /**
     * Spawn multiple collectibles at random positions
     */
    public static List<Collectible> spawnMultiple(int count, Type type, Vector2 min, Vector2 max) {
        List<Collectible> list = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            float x = min.x + random.nextFloat() * (max.x - min.x);
            float y = min.y + random.nextFloat() * (max.y - min.y);
            Collectible c = new Collectible(x, y, type);
            c.show();
            list.add(c);
        }
        return list;
    }

    /**
     * Spawn wave of collectibles over time
     */
    public static void spawnWave(int count, float interval, Type type, Vector2 min, Vector2 max) {
        new Thread(() -> {
            for (int i = 0; i < count; i++) {
                try { Thread.sleep((long)(interval * 1000)); } catch (Exception e) {}
                float x = min.x + random.nextFloat() * (max.x - min.x);
                float y = min.y + random.nextFloat() * (max.y - min.y);
                Collectible c = new Collectible(x, y, type);
                javafx.application.Platform.runLater(() -> c.show());
            }
        }).start();
    }

    /**
     * Spawn near player
     */
    public Collectible spawnNearPlayer(float minDistance, float maxDistance) {
        UserGame game = UserGame.getCurrentGame();
        if (game == null) return this;

        BlockPlayer player = game.getWorld().findEntitiesByType(BlockPlayer.class)
                .stream().findFirst().orElse(null);
        if (player == null) return this;

        float angle = random.nextFloat() * (float)Math.PI * 2;
        float distance = minDistance + random.nextFloat() * (maxDistance - minDistance);
        float x = player.getX() + (float)Math.cos(angle) * distance;
        float y = player.getY() + (float)Math.sin(angle) * distance;

        setPosition(x, y);
        show();
        return this;
    }

    /**
     * Spawn avoiding player
     */
    public Collectible spawnAtRandomPositionAvoidPlayer(float minDistance) {
        this.minDistanceFromPlayer = minDistance;
        return spawnAtValidPosition();
    }

    private Collectible spawnAtValidPosition() {
        UserGame game = UserGame.getCurrentGame();
        if (game == null || spawnAreaMin == null || spawnAreaMax == null) return this;

        BlockPlayer player = game.getWorld().findEntitiesByType(BlockPlayer.class)
                .stream().findFirst().orElse(null);

        for (int attempt = 0; attempt < maxSpawnAttempts; attempt++) {
            float x = spawnAreaMin.x + random.nextFloat() * (spawnAreaMax.x - spawnAreaMin.x);
            float y = spawnAreaMin.y + random.nextFloat() * (spawnAreaMax.y - spawnAreaMin.y);

            // Check distance from player
            if (player != null && minDistanceFromPlayer > 0) {
                float dist = (float)Math.sqrt(Math.pow(x - player.getX(), 2) + Math.pow(y - player.getY(), 2));
                if (dist < minDistanceFromPlayer) continue;
            }

            setPosition(x, y);
            show();
            break;
        }
        return this;
    }

    // ========== VALUE SETUP ==========

    /**
     * Set point value (how much this collectible is worth)
     */
    public Collectible setValue(int amount) {
        this.pointValue = amount;
        return this;
    }

    /**
     * Set scale/multiplier (for combos)
     */
    public Collectible setScale(float multiplier) {
        this.scale = multiplier;
        return this;
    }

    /**
     * Set random value between min and max
     */
    public Collectible setRandomValue(int min, int max) {
        this.pointValue = min + random.nextInt(max - min + 1);
        return this;
    }

    // ========== TYPE IDENTIFICATION ==========

    /**
     * Set as standard coin
     */
    public Collectible setAsCoin() {
        this.type = Type.COIN;
        this.pointValue = 10;
        setupAppearance();
        return this;
    }

    /**
     * Set as powerup with specific type
     */
    public Collectible setAsPowerup(PowerupType powerupType) {
        this.type = Type.POWERUP;
        this.powerupType = powerupType;
        setupAppearance();

        // Set value based on powerup type
        switch (powerupType) {
            case HEALTH: this.pointValue = 25; break;
            case SPEED: this.pointValue = 0; break;
            case INVINCIBILITY: this.pointValue = 0; break;
            case DOUBLE_JUMP: this.pointValue = 0; break;
        }
        return this;
    }

    /**
     * Set as key for a specific door
     */
    public Collectible setAsKey(String doorId) {
        this.type = Type.GEM;
        this.doorId = doorId;
        this.pointValue = 0;
        setupAppearance();
        getRenderComponent().setFillColor(Color.GOLD);
        getRenderComponent().setStrokeColor(Color.DARKGOLDENROD);
        return this;
    }

    /**
     * Set as part of a collection (e.g., 5 gems)
     */
    public Collectible setAsCollectible(String collectionId) {
        this.collectionId = collectionId;
        return this;
    }

    // ========== COLLECTION BEHAVIOR ==========

    /**
     * Manual collection trigger
     */
    public void collect() {
        collect(null);
    }

    /**
     * Collect by specific entity
     */
    public void collect(Entity collector) {
        if (collected) return;

        // Check if collector is allowed
        if (!canBeCollectedBy(collector)) return;

        // Check custom condition
        if (customCondition != null && !customCondition.getAsBoolean()) return;

        this.lastCollector = collector;
        this.collected = true;

        // Visual feedback
        if (showParticles) {
            // Would trigger particle effect
        }

        hide();
        setActive(false);

        // Callbacks
        if (onCollectedCallback != null) {
            onCollectedCallback.accept(this);
        }

        if (collector instanceof BlockPlayer && onCollectedByPlayerCallback != null) {
            onCollectedByPlayerCallback.accept((BlockPlayer) collector);
        }

        // Chain reactions
        if (chainCollectibleId != null) {
            // Spawn another collectible
        }

        if (unlockDoorId != null) {
            // Unlock door
            System.out.println("Door " + unlockDoorId + " unlocked!");
        }

        if (triggerEventName != null) {
            // Trigger custom event
            System.out.println("Event triggered: " + triggerEventName);
        }

        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            game.onCollectibleCollected(this);
        }

        // Start respawn timer if enabled
        if (respawnTime > 0) {
            respawnTimer = respawnTime;
        } else if (!destroyOnCollect) {
            // Respawn immediately if not destroying
            respawn();
        }
    }

    /**
     * Set whether to destroy or respawn on collect
     */
    public Collectible destroyOnCollect(boolean destroy) {
        this.destroyOnCollect = destroy;
        return this;
    }

    /**
     * Set respawn time after collection
     */
    public Collectible respawnAfter(float seconds) {
        this.respawnTime = seconds;
        this.destroyOnCollect = false;
        return this;
    }

    private void respawn() {
        this.collected = false;
        this.respawnTimer = 0;
        setActive(true);
        setVisible(true);

        if (onRespawnedCallback != null) {
            onRespawnedCallback.run();
        }
    }

    // ========== ACCESS RULES ==========

    /**
     * Only players can collect
     */
    public Collectible collectibleByPlayers() {
        this.collectibleByPlayers = true;
        this.collectibleByEnemies = false;
        this.collectibleByAll = false;
        return this;
    }

    /**
     * Only enemies can collect
     */
    public Collectible collectibleByEnemies() {
        this.collectibleByPlayers = false;
        this.collectibleByEnemies = true;
        this.collectibleByAll = false;
        return this;
    }

    /**
     * Anyone can collect
     */
    public Collectible collectibleByAll() {
        this.collectibleByPlayers = true;
        this.collectibleByEnemies = true;
        this.collectibleByAll = true;
        return this;
    }

    /**
     * Only entities with specific tag can collect
     */
    public Collectible collectibleByTag(String tag) {
        this.allowedTags.add(tag);
        return this;
    }

    /**
     * Ignore specific collector
     */
    public Collectible ignoreCollector(Entity collector) {
        this.ignoredCollectors.add(collector);
        return this;
    }

    /**
     * Set custom condition for collection
     */
    public Collectible requireCondition(BooleanSupplier condition) {
        this.customCondition = condition;
        return this;
    }

    /**
     * Check if entity can collect this
     */
    public boolean canBeCollectedBy(Entity collector) {
        if (collector == null) return collectibleByAll;
        if (ignoredCollectors.contains(collector)) return false;

        // Check tags
        for (String tag : allowedTags) {
            if (collector.getName().contains(tag)) return true;
        }

        if (collectibleByAll) return true;
        if (collectibleByPlayers && collector instanceof BlockPlayer) return true;
        if (collectibleByEnemies && collector instanceof Enemy) return true;

        return false;
    }

    // ========== ANIMATIONS ==========

    /**
     * Enable floating up/down animation
     */
    public Collectible setFloatAnimation(boolean enabled) {
        this.floatAnimation = enabled;
        return this;
    }

    /**
     * Enable spinning animation
     */
    public Collectible setRotateAnimation(boolean enabled) {
        this.rotateAnimation = enabled;
        return this;
    }

    /**
     * Enable size pulsing animation
     */
    public Collectible setPulseAnimation(boolean enabled) {
        this.pulseAnimation = enabled;
        return this;
    }

    /**
     * Configure pulse animation
     */
    public Collectible setPulse(float minScale, float maxScale, float speed) {
        this.pulseMin = minScale;
        this.pulseMax = maxScale;
        this.pulseSpeed = speed;
        this.pulseAnimation = true;
        return this;
    }

    // ========== VISUAL EFFECTS ==========

    /**
     * Show/hide light beam
     */
    public Collectible showBeam(boolean show) {
        this.showBeam = show;
        return this;
    }

    /**
     * Show/hide particles
     */
    public Collectible showParticles(boolean show) {
        this.showParticles = show;
        return this;
    }

    /**
     * Set glow color
     */
    public Collectible setGlowColor(Color color) {
        this.glowColor = color;
        getRenderComponent().setStrokeColor(color);
        return this;
    }

    // ========== ATTRACTION ==========

    /**
     * Make collectible attract to player
     */
    public Collectible attractToPlayer(float radius, float speed) {
        this.attractEnabled = true;
        this.attractRadius = radius;
        this.attractSpeed = speed;

        // Find player
        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            List<BlockPlayer> players = game.getWorld().findEntitiesByType(BlockPlayer.class);
            if (!players.isEmpty()) {
                this.targetPlayer = players.get(0);
            }
        }
        return this;
    }

    // ========== STATE MANAGEMENT ==========

    /**
     * Make appear
     */
    public Collectible spawn() {
        show();
        return this;
    }

    /**
     * Make disappear
     */
    public Collectible despawn() {
        hide();
        return this;
    }

    /**
     * Set available state
     */
    public Collectible setAvailable(boolean available) {
        if (available) {
            show();
        } else {
            hide();
        }
        return this;
    }

    /**
     * Appear after delay
     */
    public Collectible appearAfter(float seconds) {
        hide();
        new Thread(() -> {
            try { Thread.sleep((long)(seconds * 1000)); } catch (Exception e) {}
            javafx.application.Platform.runLater(() -> show());
        }).start();
        return this;
    }

    /**
     * Disappear after delay
     */
    public Collectible disappearAfter(float seconds) {
        new Thread(() -> {
            try { Thread.sleep((long)(seconds * 1000)); } catch (Exception e) {}
            javafx.application.Platform.runLater(() -> hide());
        }).start();
        return this;
    }

    // ========== EVENT SYSTEM ==========

    /**
     * Callback when collected
     */
    public Collectible onCollected(Consumer<Entity> callback) {
        this.onCollectedCallback = callback;
        return this;
    }

    /**
     * Callback when collected by player
     */
    public Collectible onCollectedByPlayer(Consumer<BlockPlayer> callback) {
        this.onCollectedByPlayerCallback = callback;
        return this;
    }

    /**
     * Callback when expired (despawns)
     */
    public Collectible onExpired(Runnable callback) {
        this.onExpiredCallback = callback;
        return this;
    }

    /**
     * Callback when respawned
     */
    public Collectible onRespawned(Runnable callback) {
        this.onRespawnedCallback = callback;
        return this;
    }

    // ========== CHAIN REACTIONS ==========

    /**
     * Spawn another collectible when collected
     */
    public Collectible chainCollectible(String collectibleId) {
        this.chainCollectibleId = collectibleId;
        return this;
    }

    /**
     * Unlock door when collected
     */
    public Collectible unlockDoor(String doorId) {
        this.unlockDoorId = doorId;
        return this;
    }

    /**
     * Trigger custom event when collected
     */
    public Collectible triggerEvent(String eventName) {
        this.triggerEventName = eventName;
        return this;
    }

    // ========== TRACKING & QUERIES ==========

    /**
     * Get position
     */
    public Vector2 getPosition() {
        return new Vector2(getX(), getY());
    }

    /**
     * Get distance from point
     */
    public float distanceFrom(Vector2 point) {
        return (float)Math.sqrt(Math.pow(getX() - point.x, 2) + Math.pow(getY() - point.y, 2));
    }

    /**
     * Check if in range of collector
     */
    public boolean isInRange(Entity collector, float range) {
        if (collector == null) return false;
        return getPosition().distanceTo(collector.getPosition()) <= range;
    }

//    /**
//     * Check if collected
//     */
//    public boolean isCollected() {
//        return collected;
//    }

    /**
     * Get time until respawn
     */
    public float getTimeUntilRespawn() {
        return respawnTimer;
    }

    /**
     * Get last collector
     */
    public Entity getLastCollector() {
        return lastCollector;
    }

    // ========== SPAWN AREA CONFIGURATION ==========

    /**
     * Set spawn area (rectangle)
     */
    public Collectible setSpawnArea(Vector2 min, Vector2 max) {
        this.spawnAreaMin = min;
        this.spawnAreaMax = max;
        return this;
    }

    /**
     * Set spawn area (circle)
     */
    public Collectible setSpawnArea(Vector2 center, float radius) {
        this.spawnAreaMin = new Vector2(center.x - radius, center.y - radius);
        this.spawnAreaMax = new Vector2(center.x + radius, center.y + radius);
        return this;
    }

    /**
     * Set ground check (must spawn on ground)
     */
    public Collectible setGroundCheck(boolean enabled) {
        this.groundOnly = enabled;
        return this;
    }

    /**
     * Set avoid walls
     */
    public Collectible setAvoidWalls(boolean enabled) {
        this.avoidWalls = enabled;
        return this;
    }

    /**
     * Set minimum distance from other collectibles
     */
    public Collectible setMinDistanceFromOthers(float distance) {
        this.minDistanceFromOthers = distance;
        return this;
    }

    /**
     * Set max spawn attempts
     */
    public Collectible setMaxSpawnAttempts(int attempts) {
        this.maxSpawnAttempts = attempts;
        return this;
    }

    // ========== STATIC HELPERS ==========

    public static int getScore() {
        UserGame game = UserGame.getCurrentGame();
        return game != null ? game.getCollectibleScore() : 0;
    }

    public static int getRemainingCount() {
        UserGame game = UserGame.getCurrentGame();
        return game != null ? game.getCollectibleRemaining() : 0;
    }

    // ========== CUSTOMIZATION (existing) ==========

    public void setColor(Color fill, Color stroke) {
        getRenderComponent().setFillColor(fill);
        getRenderComponent().setStrokeColor(stroke);
    }

    public void setColor(String fillHex) {
        getRenderComponent().setFillColor(Color.web(fillHex));
    }

    public void setSize(float size) {
        getRenderComponent().setWidth(size);
        getRenderComponent().setHeight(size);
        if (hasCollider()) {
            getCollider().setSize(size, size);
        }
    }

    public void setPointValue(int points) {
        this.pointValue = points;
    }
    // ========== LIFECYCLE ==========

    private void showNow() {
        if (visible) return;
        super.show();
        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            game.addEntity(this);
        }
    }

    @Override
    public void show() {
        showNow();
    }

    // ========== GETTERS ==========

    public int getPointValue()   { return pointValue; }
    public Type getType()        { return type; }
    public PowerupType getPowerupType() { return powerupType; }
    public boolean isCollected() { return collected; }
    public String getDoorId()    { return doorId; }
    public String getCollectionId() { return collectionId; }

    @Override
    public boolean isCollectible() { return true; }

    @Override
    public String toString() {
        return String.format("Collectible[%s, value=%d, collected=%b]", type, pointValue, collected);
    }
}