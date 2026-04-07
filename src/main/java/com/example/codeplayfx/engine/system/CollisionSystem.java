package com.example.codeplayfx.engine.system;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entity.BlockPlayer;
import com.example.codeplayfx.world.entity.Enemy;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.PhysicalEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * CollisionSystem detects and resolves collisions between entities.
 *
 * Features:
 * - Detects all entity-entity collisions each frame
 * - Resolves physics collisions (prevents overlap)
 * - Triggers collision callbacks
 * - Handles trigger colliders (no physics response)
 * - Applies Enemy→BlockPlayer damage with per-enemy cooldown
 */
public class CollisionSystem extends GameSystem {

    // Collision pairs detected this frame
    private List<CollisionPair> collisions;

    /**
     * Stores a pair of colliding entities.
     */
    private static class CollisionPair {
        Entity entityA;
        Entity entityB;

        CollisionPair(Entity a, Entity b) {
            this.entityA = a;
            this.entityB = b;
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

        // Clear previous frame's collision list
        collisions.clear();

        // Broad-phase: detect all overlapping pairs
        detectCollisions();

        // Narrow-phase: push overlapping entities apart
        resolveCollisions();
    }

    // ========== DETECTION ==========

    private void detectCollisions() {
        List<Entity> entities = gameWorld.getAllEntities();

        for (int i = 0; i < entities.size(); i++) {
            Entity entityA = entities.get(i);

            if (!entityA.isActive() || !entityA.hasCollider()) continue;

            for (int j = i + 1; j < entities.size(); j++) {
                Entity entityB = entities.get(j);

                if (!entityB.isActive() || !entityB.hasCollider()) continue;

                if (entityA.isColliding(entityB)) {
                    collisions.add(new CollisionPair(entityA, entityB));
                    onCollision(entityA, entityB);
                }
            }
        }
    }

    // ========== RESOLUTION ==========

    private void resolveCollisions() {
        for (CollisionPair pair : collisions) {
            Entity entityA = pair.entityA;
            Entity entityB = pair.entityB;

            // Trigger colliders: fire callback only, no positional separation
            if (entityA.getCollider().isTrigger() || entityB.getCollider().isTrigger()) {
                continue;
            }

            // Enemy colliders intentionally do NOT push the player around.
            // They deal damage but should not physically separate — that would
            // make enemies feel like solid walls. Skip physics resolution when
            // one side is an Enemy.
            if (entityA instanceof Enemy || entityB instanceof Enemy) {
                continue;
            }

            if (entityA instanceof PhysicalEntity || entityB instanceof PhysicalEntity) {
                resolvePhysicsCollision(entityA, entityB);
            }
        }
    }

    /**
     * Resolve physics collision (separate entities).
     */
    private void resolvePhysicsCollision(Entity entityA, Entity entityB) {
        var boundsA = entityA.getCollider().getBounds(entityA.getPosition());
        var boundsB = entityB.getCollider().getBounds(entityB.getPosition());

        double overlapX = Math.min(boundsA.getMaxX(), boundsB.getMaxX())
                - Math.max(boundsA.getMinX(), boundsB.getMinX());
        double overlapY = Math.min(boundsA.getMaxY(), boundsB.getMaxY())
                - Math.max(boundsA.getMinY(), boundsB.getMinY());

        if (overlapX < overlapY) {
            separateHorizontally(entityA, entityB, overlapX, boundsA, boundsB);
        } else {
            separateVertically(entityA, entityB, overlapY, boundsA, boundsB);
        }
    }

    private void separateHorizontally(Entity entityA, Entity entityB, double overlap,
                                      javafx.geometry.Rectangle2D boundsA,
                                      javafx.geometry.Rectangle2D boundsB) {
        boolean aIsLeft = boundsA.getMinX() < boundsB.getMinX();
        float separation = (float) (overlap / 2.0);

        if (entityA instanceof PhysicalEntity && entityB instanceof PhysicalEntity) {
            if (aIsLeft) {
                entityA.move(-separation, 0);
                entityB.move(separation, 0);
            } else {
                entityA.move(separation, 0);
                entityB.move(-separation, 0);
            }
            ((PhysicalEntity) entityA).setVelocity(0, ((PhysicalEntity) entityA).getVelocity().y);
            ((PhysicalEntity) entityB).setVelocity(0, ((PhysicalEntity) entityB).getVelocity().y);

        } else if (entityA instanceof PhysicalEntity) {
            entityA.move(aIsLeft ? -(float) overlap : (float) overlap, 0);
            ((PhysicalEntity) entityA).setVelocity(0, ((PhysicalEntity) entityA).getVelocity().y);

        } else if (entityB instanceof PhysicalEntity) {
            entityB.move(aIsLeft ? (float) overlap : -(float) overlap, 0);
            ((PhysicalEntity) entityB).setVelocity(0, ((PhysicalEntity) entityB).getVelocity().y);
        }
    }

    private void separateVertically(Entity entityA, Entity entityB, double overlap,
                                    javafx.geometry.Rectangle2D boundsA,
                                    javafx.geometry.Rectangle2D boundsB) {
        boolean aIsAbove = boundsA.getMinY() < boundsB.getMinY();
        float separation = (float) (overlap / 2.0);

        if (entityA instanceof PhysicalEntity && entityB instanceof PhysicalEntity) {
            if (aIsAbove) {
                entityA.move(0, -separation);
                entityB.move(0, separation);
            } else {
                entityA.move(0, separation);
                entityB.move(0, -separation);
            }
            ((PhysicalEntity) entityA).setVelocity(((PhysicalEntity) entityA).getVelocity().x, 0);
            ((PhysicalEntity) entityB).setVelocity(((PhysicalEntity) entityB).getVelocity().x, 0);

        } else if (entityA instanceof PhysicalEntity) {
            entityA.move(0, aIsAbove ? -(float) overlap : (float) overlap);
            ((PhysicalEntity) entityA).setVelocity(((PhysicalEntity) entityA).getVelocity().x, 0);

            if (!aIsAbove && entityA instanceof BlockPlayer) {
                ((BlockPlayer) entityA).setOnGround(true);
            }

        } else if (entityB instanceof PhysicalEntity) {
            entityB.move(0, aIsAbove ? (float) overlap : -(float) overlap);
            ((PhysicalEntity) entityB).setVelocity(((PhysicalEntity) entityB).getVelocity().x, 0);

            if (aIsAbove && entityB instanceof BlockPlayer) {
                ((BlockPlayer) entityB).setOnGround(true);
            }
        }
    }

    // ========== COLLISION CALLBACKS ==========

    /**
     * Called for every overlapping pair detected this frame.
     *
     * Priority order:
     *  1. Collectible pickup  (BlockPlayer touches a collectible)
     *  2. Enemy damage        (Enemy touches BlockPlayer, with cooldown)
     */
    protected void onCollision(Entity entityA, Entity entityB) {

        // ── 1. Collectible pickup ──────────────────────────────────────────
        if (entityA instanceof BlockPlayer && entityB.isCollectible()) {
            entityB.collect();
            return;
        }
        if (entityB instanceof BlockPlayer && entityA.isCollectible()) {
            entityA.collect();
            return;
        }

        // ── 2. Enemy → BlockPlayer damage (with per-enemy cooldown) ────────
        // Identify which entity is the Enemy and which is the BlockPlayer.
        Enemy       enemy  = null;
        BlockPlayer player = null;

        if (entityA instanceof Enemy && entityB instanceof BlockPlayer) {
            enemy  = (Enemy)       entityA;
            player = (BlockPlayer) entityB;
        } else if (entityB instanceof Enemy && entityA instanceof BlockPlayer) {
            enemy  = (Enemy)       entityB;
            player = (BlockPlayer) entityA;
        }

        if (enemy != null && player != null) {
            // Only deal damage if the enemy's cooldown has expired
            if (enemy.canDamage()) {
                player.takeDamage(enemy.getDamage());
                enemy.resetDamageCooldown();   // start the cooldown timer
            }
        }
    }

    // ========== QUERIES ==========

    /**
     * Get all collision pairs detected this frame (returns a defensive copy).
     */
    public List<CollisionPair> getCollisions() {
        return new ArrayList<>(collisions);
    }

    /**
     * Check if two specific entities are colliding this frame.
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
}