package com.example.codeplayfx.engine.system;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entity.*;
import com.example.codeplayfx.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionSystem detects and resolves collisions between entities.
 *
 * Features:
 * - Detects all entity-entity collisions
 * - Resolves physics collisions (prevents overlap)
 * - Triggers collision callbacks
 * - Handles trigger colliders (no physics response)
 */
public class CollisionSystem extends GameSystem {

    // Collision pairs detected this frame
    private List<CollisionPair> collisions;

    /**
     * Stores a pair of colliding entities
     */
    private static class CollisionPair {
        Entity entityA;
        Entity entityB;

        CollisionPair(Entity a, Entity b) {
            this.entityA = a;
            this.entityB = b;
        }

        public boolean involves(Class<? extends Entity> type) {
            return type.isInstance(entityA) || type.isInstance(entityB);
        }

        public <T extends Entity> T getEntityOfType(Class<T> type) {
            if (type.isInstance(entityA)) return type.cast(entityA);
            if (type.isInstance(entityB)) return type.cast(entityB);
            return null;
        }

        public Entity getOther(Entity entity) {
            if (entityA == entity) return entityB;
            if (entityB == entity) return entityA;
            return null;
        }
    }

    public CollisionSystem(GameWorld gameWorld) {
        super(gameWorld);
        this.collisions = new ArrayList<>();
    }

    @Override
    public void initialize() {
        Logger.info("CollisionSystem initialized");
    }

    @Override
    public void update(float deltaTime) {
        if (!enabled) return;

        // Clear previous frame's collisions
        collisions.clear();

        // Detect all collisions
        detectCollisions();

        // Handle trigger callbacks FIRST (before physics resolution)
        handleTriggerCallbacks();

        // Resolve collisions (physics response) for non-triggers
        resolveCollisions();
    }

    /**
     * Detect all collisions between entities
     */
    private void detectCollisions() {
        List<Entity> entities = gameWorld.getAllEntities();

        // Check every pair of entities
        for (int i = 0; i < entities.size(); i++) {
            Entity entityA = entities.get(i);

            // Skip inactive entities or entities without colliders
            if (!entityA.isActive() || !entityA.hasCollider()) {
                continue;
            }

            for (int j = i + 1; j < entities.size(); j++) {
                Entity entityB = entities.get(j);

                // Skip inactive entities or entities without colliders
                if (!entityB.isActive() || !entityB.hasCollider()) {
                    continue;
                }

                // Check collision
                if (entityA.isColliding(entityB)) {
                    collisions.add(new CollisionPair(entityA, entityB));
                }
            }
        }
    }

    /**
     * Handle trigger callbacks (KillZone, GoalZone, etc.)
     */
    private void handleTriggerCallbacks() {
        for (CollisionPair pair : collisions) {
            // Check for KillZone + Player
            if (pair.involves(KillZone.class) && pair.involves(BlockPlayer.class)) {
                KillZone killZone = pair.getEntityOfType(KillZone.class);
                BlockPlayer player = pair.getEntityOfType(BlockPlayer.class);
                if (killZone != null && player != null) {
                    killZone.onTouch(player);
                }
            }

            // Check for GoalZone + Player
            if (pair.involves(GoalZone.class) && pair.involves(BlockPlayer.class)) {
                GoalZone goalZone = pair.getEntityOfType(GoalZone.class);
                BlockPlayer player = pair.getEntityOfType(BlockPlayer.class);
                if (goalZone != null && player != null) {
                    goalZone.onTouch(player);
                }
            }

            // Check for Enemy + Player (damage)
            if (pair.involves(Enemy.class) && pair.involves(BlockPlayer.class)) {
                Enemy enemy = pair.getEntityOfType(Enemy.class);
                BlockPlayer player = pair.getEntityOfType(BlockPlayer.class);
                if (enemy != null && player != null) {
                    player.takeDamage(10); // Enemy damage

                    // Knockback
                    com.example.codeplayfx.world.physics.Vector2 knockback =
                            player.getPosition().subtract(enemy.getPosition()).normalized().multiply(5);
                    player.setVelocity(knockback);
                }
            }

            // Check for Enemy + SolidBlock (AI reaction)
            if (pair.involves(Enemy.class) && pair.involves(SolidBlock.class)) {
                Enemy enemy = pair.getEntityOfType(Enemy.class);
                if (enemy != null && enemy.isPatrolling()) {
                    enemy.reverseDirection();
                }
            }

            // Call generic onCollision for any custom logic
            onCollision(pair.entityA, pair.entityB);
        }
    }

    /**
     * Resolve collisions (separate overlapping entities) - only for non-triggers
     */
    private void resolveCollisions() {
        for (CollisionPair pair : collisions) {
            Entity entityA = pair.entityA;
            Entity entityB = pair.entityB;

            // Skip if either is a trigger (no physics response)
            if (entityA.getCollider().isTrigger() || entityB.getCollider().isTrigger()) {
                continue;
            }

            // Handle SolidBlock + PhysicalEntity specially
            if (pair.involves(SolidBlock.class) && pair.involves(PhysicalEntity.class)) {
                SolidBlock block = pair.getEntityOfType(SolidBlock.class);
                PhysicalEntity physical = pair.getEntityOfType(PhysicalEntity.class);
                if (block != null && physical != null) {
                    resolveSolidBlockCollision(block, physical);
                    continue;
                }
            }

            // Default physics resolution for other non-trigger pairs
            if (entityA instanceof PhysicalEntity || entityB instanceof PhysicalEntity) {
                resolvePhysicsCollision(entityA, entityB);
            }
        }
    }

    /**
     * Special resolution for SolidBlock + PhysicalEntity
     */
    private void resolveSolidBlockCollision(SolidBlock block, PhysicalEntity entity) {
        // Get collision bounds
        var boundsBlock = block.getCollider().getBounds(block.getPosition());
        var boundsEntity = entity.getCollider().getBounds(entity.getPosition());

        // Calculate overlap
        double overlapX = Math.min(boundsBlock.getMaxX(), boundsEntity.getMaxX()) -
                Math.max(boundsBlock.getMinX(), boundsEntity.getMinX());
        double overlapY = Math.min(boundsBlock.getMaxY(), boundsEntity.getMaxY()) -
                Math.max(boundsBlock.getMinY(), boundsEntity.getMinY());

        // Separate along the axis with the smallest overlap
        if (overlapX < overlapY) {
            // Separate horizontally
            if (boundsEntity.getMinX() < boundsBlock.getMinX()) {
                // Entity is to the left of block
                entity.setPosition(
                        (float)(boundsBlock.getMinX() - entity.getCollider().getWidth()),
                        entity.getY()
                );
            } else {
                // Entity is to the right of block
                entity.setPosition(
                        (float)(boundsBlock.getMaxX()),
                        entity.getY()
                );
            }
            // Stop horizontal velocity
            entity.setVelocity(0, entity.getVelocity().y);
        } else {
            // Separate vertically
            if (boundsEntity.getMinY() < boundsBlock.getMinY()) {
                // Entity is below block
                entity.setPosition(
                        entity.getX(),
                        (float)(boundsBlock.getMinY() - entity.getCollider().getHeight())
                );
            } else {
                // Entity is above block (landing on top)
                entity.setPosition(
                        entity.getX(),
                        (float)(boundsBlock.getMaxY())
                );

                // If this is a player, set on ground
                if (entity instanceof BlockPlayer) {
                    ((BlockPlayer) entity).setOnGround(true);
                }
            }
            // Stop vertical velocity
            entity.setVelocity(entity.getVelocity().x, 0);
        }
    }

    /**
     * Resolve physics collision (separate entities)
     */
    private void resolvePhysicsCollision(Entity entityA, Entity entityB) {
        // Get collision bounds
        var boundsA = entityA.getCollider().getBounds(entityA.getPosition());
        var boundsB = entityB.getCollider().getBounds(entityB.getPosition());

        // Calculate overlap
        double overlapX = Math.min(boundsA.getMaxX(), boundsB.getMaxX()) -
                Math.max(boundsA.getMinX(), boundsB.getMinX());
        double overlapY = Math.min(boundsA.getMaxY(), boundsB.getMaxY()) -
                Math.max(boundsA.getMinY(), boundsB.getMinY());

        // Separate along the axis with the smallest overlap
        if (overlapX < overlapY) {
            // Separate horizontally
            separateHorizontally(entityA, entityB, overlapX, boundsA, boundsB);
        } else {
            // Separate vertically
            separateVertically(entityA, entityB, overlapY, boundsA, boundsB);
        }
    }

    /**
     * Separate entities horizontally
     */
    private void separateHorizontally(Entity entityA, Entity entityB, double overlap,
                                      javafx.geometry.Rectangle2D boundsA,
                                      javafx.geometry.Rectangle2D boundsB) {
        // Determine direction
        boolean aIsLeft = boundsA.getMinX() < boundsB.getMinX();
        float separation = (float) (overlap / 2.0);

        // Move both entities (if both are physical)
        if (entityA instanceof PhysicalEntity && entityB instanceof PhysicalEntity) {
            if (aIsLeft) {
                entityA.move(-separation, 0);
                entityB.move(separation, 0);
            } else {
                entityA.move(separation, 0);
                entityB.move(-separation, 0);
            }

            // Stop horizontal velocity
            ((PhysicalEntity) entityA).setVelocity(0, ((PhysicalEntity) entityA).getVelocity().y);
            ((PhysicalEntity) entityB).setVelocity(0, ((PhysicalEntity) entityB).getVelocity().y);
        }
        // Only A is physical
        else if (entityA instanceof PhysicalEntity) {
            if (aIsLeft) {
                entityA.move(-(float)overlap, 0);
            } else {
                entityA.move((float)overlap, 0);
            }
            ((PhysicalEntity) entityA).setVelocity(0, ((PhysicalEntity) entityA).getVelocity().y);
        }
        // Only B is physical
        else if (entityB instanceof PhysicalEntity) {
            if (aIsLeft) {
                entityB.move((float)overlap, 0);
            } else {
                entityB.move(-(float)overlap, 0);
            }
            ((PhysicalEntity) entityB).setVelocity(0, ((PhysicalEntity) entityB).getVelocity().y);
        }
    }

    /**
     * Separate entities vertically
     */
    private void separateVertically(Entity entityA, Entity entityB, double overlap,
                                    javafx.geometry.Rectangle2D boundsA,
                                    javafx.geometry.Rectangle2D boundsB) {
        // Determine direction
        boolean aIsAbove = boundsA.getMinY() < boundsB.getMinY();
        float separation = (float) (overlap / 2.0);

        // Move both entities (if both are physical)
        if (entityA instanceof PhysicalEntity && entityB instanceof PhysicalEntity) {
            if (aIsAbove) {
                entityA.move(0, -separation);
                entityB.move(0, separation);
            } else {
                entityA.move(0, separation);
                entityB.move(0, -separation);
            }

            // Stop vertical velocity
            ((PhysicalEntity) entityA).setVelocity(((PhysicalEntity) entityA).getVelocity().x, 0);
            ((PhysicalEntity) entityB).setVelocity(((PhysicalEntity) entityB).getVelocity().x, 0);
        }
        // Only A is physical
        else if (entityA instanceof PhysicalEntity) {
            if (aIsAbove) {
                entityA.move(0, -(float)overlap);
                // If landing on something from above
                if (!aIsAbove) {
                    if (entityA instanceof BlockPlayer) {
                        ((BlockPlayer) entityA).setOnGround(true);
                    }
                }
            } else {
                entityA.move(0, (float)overlap);
            }
            ((PhysicalEntity) entityA).setVelocity(((PhysicalEntity) entityA).getVelocity().x, 0);
        }
        // Only B is physical
        else if (entityB instanceof PhysicalEntity) {
            if (aIsAbove) {
                entityB.move(0, (float)overlap);
                // If B is landing on A from above
                if (aIsAbove) {
                    if (entityB instanceof BlockPlayer) {
                        ((BlockPlayer) entityB).setOnGround(true);
                    }
                }
            } else {
                entityB.move(0, -(float)overlap);
            }
            ((PhysicalEntity) entityB).setVelocity(((PhysicalEntity) entityB).getVelocity().x, 0);
        }
    }

    /**
     * Called when collision is detected (override for custom behavior)
     */
    protected void onCollision(Entity entityA, Entity entityB) {
        // Can be overridden for custom collision logic
    }

    /**
     * Get all collisions detected this frame
     */
    public List<CollisionPair> getCollisions() {
        return new ArrayList<>(collisions);
    }

    /**
     * Check if two specific entities are colliding this frame
     */
    public boolean areColliding(Entity entityA, Entity entityB) {
        for (CollisionPair pair : collisions) {
            if ((pair.entityA == entityA && pair.entityB == entityB) ||
                    (pair.entityA == entityB && pair.entityB == entityA)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get all entities colliding with a specific entity
     */
    public List<Entity> getCollidingEntities(Entity entity) {
        List<Entity> result = new ArrayList<>();
        for (CollisionPair pair : collisions) {
            if (pair.entityA == entity) {
                result.add(pair.entityB);
            } else if (pair.entityB == entity) {
                result.add(pair.entityA);
            }
        }
        return result;
    }
}