package com.example.codeplayfx.world;

import com.example.codeplayfx.engine.system.PhysicsSystem;
import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.physics.CoordinateSystem;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameWorld {

    // All entities in the world
    private List<Entity> entities;
    private List<Entity> entitiesToAdd;    // Entities to add next frame
    private List<Entity> entitiesToRemove; // Entities to remove next frame

    // Coordinate system
    private CoordinateSystem coordinateSystem;

    // Game state
    private boolean running;
    private float totalTime;  // Total time elapsed since game start

    private PhysicsSystem physicsSystem;

    public GameWorld() {
        // Use thread-safe list to prevent concurrent modification
        this.entities = new CopyOnWriteArrayList<>();
        this.entitiesToAdd = new ArrayList<>();
        this.entitiesToRemove = new ArrayList<>();

        // Create coordinate system (50px grid by default)
        this.coordinateSystem = new CoordinateSystem(50.0);

        // Give all entities access to coordinate system
        Entity.setCoordinateSystem(coordinateSystem);

        this.physicsSystem = new PhysicsSystem(this);
        this.running = false;
        this.totalTime = 0f;

        Logger.info("GameWorld created");
    }


    public GameWorld(double gridSize) {
        this();
        this.coordinateSystem = new CoordinateSystem(gridSize);
        Entity.setCoordinateSystem(coordinateSystem);
    }

    // ========== ENTITY MANAGEMENT ==========


    public void addEntity(Entity entity) {
        if (entity == null) {
            Logger.warning("Attempted to add null entity");
            return;
        }

        entitiesToAdd.add(entity);
        Logger.debug("Queued entity for addition: " + entity.getName());
    }


    public void removeEntity(Entity entity) {
        if (entity == null) return;

        entitiesToRemove.add(entity);
        Logger.debug("Queued entity for removal: " + entity.getName());
    }

    /**
     * Remove entity by ID
     */
    public void removeEntity(long entityId) {
        Entity entity = findEntityById(entityId);
        if (entity != null) {
            removeEntity(entity);
        }
    }

    /**
     * Process pending additions and removals
     * Called at the end of each update
     */
    private void processPendingChanges() {
        // Remove entities
        if (!entitiesToRemove.isEmpty()) {
            for (Entity entity : entitiesToRemove) {
                entities.remove(entity);
                entity.onDestroy();
                Logger.debug("Removed entity: " + entity.getName());
            }
            entitiesToRemove.clear();
        }

        // Add entities
        if (!entitiesToAdd.isEmpty()) {
            entities.addAll(entitiesToAdd);
            Logger.debug("Added " + entitiesToAdd.size() + " entities");
            entitiesToAdd.clear();
        }
    }

    /**
     * Remove all entities from the world
     */
    public void clear() {
        for (Entity entity : entities) {
            entity.onDestroy();
        }
        entities.clear();
        entitiesToAdd.clear();
        entitiesToRemove.clear();
        Logger.info("GameWorld cleared");
    }

    // ========== UPDATE LOOP ==========

    /**
     * Update all entities (called every frame by engine)
     * @param deltaTime time since last frame in seconds
     */
    public void update(float deltaTime) {
        if (!running) return;

        totalTime += deltaTime;

        for (Entity entity : entities) {
            if (entity.isActive()) {
                entity.update(deltaTime);
            }
        }

        if (physicsSystem != null) {
            physicsSystem.update(deltaTime);
        }
        // Update all active entities


        // Process additions/removals after all updates
        processPendingChanges();
    }

    /**
     * Start the game world
     */
    public void start() {
        running = true;
        totalTime = 0f;
        if (physicsSystem != null) {
            physicsSystem.initialize();
        }
        Logger.info("GameWorld started");
    }

    /**
     * Stop the game world
     */
    public void stop() {
        running = false;
        Logger.info("GameWorld stopped");
    }

    /**
     * Pause the game world
     */
    public void pause() {
        running = false;
        Logger.info("GameWorld paused");
    }

    /**
     * Resume the game world
     */
    public void resume() {
        running = true;
        Logger.info("GameWorld resumed");
    }

    // ========== ENTITY QUERIES ==========

    /**
     * Get all entities in the world
     */
    public List<Entity> getAllEntities() {
        return new ArrayList<>(entities);
    }

    /**
     * Get all visible entities
     */
    public List<Entity> getVisibleEntities() {
        List<Entity> visible = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.isVisible()) {
                visible.add(entity);
            }
        }
        return visible;
    }

    /**
     * Find entity by ID
     */
    public Entity findEntityById(long id) {
        for (Entity entity : entities) {
            if (entity.getId() == id) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Find entity by name
     */
    public Entity findEntityByName(String name) {
        for (Entity entity : entities) {
            if (entity.getName().equals(name)) {
                return entity;
            }
        }
        return null;
    }

    /**
     * Find all entities of a specific type
     */
    @SuppressWarnings("unchecked")
    public <T extends Entity> List<T> findEntitiesByType(Class<T> type) {
        List<T> result = new ArrayList<>();
        for (Entity entity : entities) {
            if (type.isInstance(entity)) {
                result.add((T) entity);
            }
        }
        return result;
    }

    /**
     * Count total entities
     */
    public int getEntityCount() {
        return entities.size();
    }

    /**
     * Count active entities
     */
    public int getActiveEntityCount() {
        int count = 0;
        for (Entity entity : entities) {
            if (entity.isActive()) count++;
        }
        return count;
    }

    // ========== COORDINATE SYSTEM ==========

    /**
     * Get the coordinate system
     */
    public CoordinateSystem getCoordinateSystem() {
        return coordinateSystem;
    }

    /**
     * Update coordinate system when canvas size changes
     */
    public void updateCanvasSize(double width, double height) {
        coordinateSystem.setCanvasSize(width, height);
        Logger.debug("Canvas size updated: " + width + "x" + height);
    }

    /**
     * Set grid scale
     */
    public void setGridScale(double gridScale) {
        coordinateSystem.setGridScale(gridScale);
        Logger.debug("Grid scale set to: " + gridScale);
    }
    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }

    // ========== GETTERS ==========

    public boolean isRunning() {
        return running;
    }

    public float getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return String.format("GameWorld[entities=%d, active=%d, running=%b, time=%.2fs]",
                getEntityCount(), getActiveEntityCount(), running, totalTime);
    }
}