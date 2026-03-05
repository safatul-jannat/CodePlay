package com.example.codeplayfx.engine.system;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entity.BlockPlayer;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.PhysicalEntity;
import com.example.codeplayfx.world.entity.KillZone;
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
        
        // Resolve collisions (physics response)
        resolveCollisions();
        handleTriggerCollisions();
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
                    
                    // Trigger callbacks
                    onCollision(entityA, entityB);
                }
            }
        }
    }
    
    /**
     * Resolve collisions (separate overlapping entities)
     */
    private void resolveCollisions() {
        for (CollisionPair pair : collisions) {
            Entity entityA = pair.entityA;
            Entity entityB = pair.entityB;
            
            // Skip if either is a trigger (no physics response)
            if (entityA.getCollider().isTrigger() || entityB.getCollider().isTrigger()) {
                continue;
            }
            
            // Only resolve if at least one is a PhysicalEntity
            if (entityA instanceof PhysicalEntity || entityB instanceof PhysicalEntity) {
                resolvePhysicsCollision(entityA, entityB);
            }
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
            } else {
                entityA.move(0, (float)overlap);
            }
            ((PhysicalEntity) entityA).setVelocity(((PhysicalEntity) entityA).getVelocity().x, 0);
            
            // If A is landing on B from above, set on ground
            if (!aIsAbove && entityA instanceof com.example.codeplayfx.world.entity.BlockPlayer) {
                ((com.example.codeplayfx.world.entity.BlockPlayer) entityA).setOnGround(true);
            }
        }
        // Only B is physical
        else if (entityB instanceof PhysicalEntity) {
            if (aIsAbove) {
                entityB.move(0, (float)overlap);
            } else {
                entityB.move(0, -(float)overlap);
            }
            ((PhysicalEntity) entityB).setVelocity(((PhysicalEntity) entityB).getVelocity().x, 0);
            
            // If B is landing on A from above, set on ground
            if (aIsAbove && entityB instanceof com.example.codeplayfx.world.entity.BlockPlayer) {
                ((com.example.codeplayfx.world.entity.BlockPlayer) entityB).setOnGround(true);
            }
        }
    }
    
    /**
     * Called when collision is detected (override for custom behavior)
     */
    protected void onCollision(Entity entityA, Entity entityB) {
        // Can be overridden for custom collision logic
        // e.g., damage, pickup items, trigger events, etc.
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
     * Handle trigger collisions (like KillZone)
     */
    private void handleTriggerCollisions() {
        for (CollisionPair pair : collisions) {
            Entity a = pair.entityA;
            Entity b = pair.entityB;

            // Check for KillZone + BlockPlayer
            if (a instanceof KillZone && b instanceof BlockPlayer) {
                ((KillZone) a).onTouch((BlockPlayer) b);
            } else if (b instanceof KillZone && a instanceof BlockPlayer) {
                ((KillZone) b).onTouch((BlockPlayer) a);
            }
        }
    }
}