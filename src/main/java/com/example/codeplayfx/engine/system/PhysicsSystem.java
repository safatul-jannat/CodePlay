package com.example.codeplayfx.engine.system;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.PhysicalEntity;

/**
 * PhysicsSystem handles physics simulation for all physical entities.
 * 
 * Features:
 * - Centralized physics updates
 * - Gravity application
 * - Velocity/acceleration updates
 * - World bounds checking
 */
public class PhysicsSystem extends GameSystem {

    // Physics constants
    private float gravity = 0.5f;
    private boolean useWorldBounds = true;
    private float worldMinY = 0f;     // Ground level
    private float worldMaxY = 100f;   // Sky limit
    private float worldMinX = 0f;     // Left wall
    private float worldMaxX = 100f;   // Right wall

    public PhysicsSystem(GameWorld gameWorld) {
        super(gameWorld);
    }

    @Override
    public void initialize() {
        Logger.info("PhysicsSystem initialized");
    }

    @Override
    public void update(float deltaTime) {
        if (!enabled) return;

        // Update physics for all physical entities
        for (Entity entity : gameWorld.getAllEntities()) {
            if (entity instanceof PhysicalEntity && entity.isActive()) {
                updatePhysicalEntity((PhysicalEntity) entity, deltaTime);
            }
        }
    }

    /**
     * Update a single physical entity
     */
    private void updatePhysicalEntity(PhysicalEntity entity, float deltaTime) {
        // Entity handles its own physics update
        // (This is already done in PhysicalEntity.update())
        // But we can add global physics rules here

        // Apply world bounds
        if (useWorldBounds) {
            applyWorldBounds(entity);
        }
    }

    /**
     * Keep entity within world bounds
     */
    private void applyWorldBounds(PhysicalEntity entity) {
        float x = entity.getX();
        float y = entity.getY();

        // ONLY apply bounds if gravity is enabled (prevents flying entities from being restricted)
        if (!entity.isUsingGravity()) {
            return; // Flying entities ignore world bounds
        }

        // Bottom bound (ground)
        if (y >= worldMinY) {
            entity.setPosition(x, worldMinY);
            entity.setVelocity(entity.getVelocity().x, 0);  // Stop falling

            // Set on ground for BlockPlayer
            if (entity instanceof com.example.codeplayfx.world.entity.BlockPlayer) {
                ((com.example.codeplayfx.world.entity.BlockPlayer) entity).setOnGround(true);
            }
        }

        // Top bound (sky) - only for gravity entities
        if (y <= worldMaxY - 1) {
            entity.setPosition(x, worldMaxY - 1);
            entity.setVelocity(entity.getVelocity().x, 0);  // Stop rising
        }

        // Left bound
        if (x <= worldMinX) {
            entity.setPosition(worldMinX, y);
            entity.setVelocity(0, entity.getVelocity().y);  // Stop moving left
        }

        // Right bound
        if (x >= worldMaxX - 1) {
            entity.setPosition(worldMaxX - 1, y);
            entity.setVelocity(0, entity.getVelocity().y);  // Stop moving right
        }
    }

    // ========== CONFIGURATION ==========

    /**
     * Set global gravity strength
     */
    public void setGravity(float gravity) {
        this.gravity = gravity;
    }

    public float getGravity() {
        return gravity;
    }

    public void setGlobalGravityEnabled(boolean enabled) {
        for (Entity entity : gameWorld.getAllEntities()) {
            if (entity instanceof PhysicalEntity) {
                ((PhysicalEntity) entity).setUseGravity(enabled);
            }
        }
    }

    public void setGlobalGravity(float gravityValue) {
        this.gravity = gravityValue;
        for (Entity entity : gameWorld.getAllEntities()) {
            if (entity instanceof PhysicalEntity) {
                ((PhysicalEntity) entity).setGravity(gravityValue);
            }
        }
    }

    /**
     * Enable/disable world bounds
     */
    public void setUseWorldBounds(boolean use) {
        this.useWorldBounds = use;
    }

    public boolean isUsingWorldBounds() {
        return useWorldBounds;
    }

    /**
     * Set world bounds (in grid coordinates)
     */
    public void setWorldBounds(float minX, float minY, float maxX, float maxY) {
        this.worldMinX = minX;
        this.worldMinY = minY;
        this.worldMaxX = maxX;
        this.worldMaxY = maxY;
    }

    /**
     * Set world bounds from grid size
     */
    public void setWorldBoundsFromGrid(int gridWidth, int gridHeight) {
        this.worldMinX = 0;
        this.worldMinY = 0;
        this.worldMaxX = gridWidth;
        this.worldMaxY = gridHeight;
    }



    public float getWorldMinX() { return worldMinX; }
    public float getWorldMinY() { return worldMinY; }
    public float getWorldMaxX() { return worldMaxX; }
    public float getWorldMaxY() { return worldMaxY; }

}