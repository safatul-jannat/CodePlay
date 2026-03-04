package com.example.codeplayfx.engine.system;

import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;

import java.util.ArrayList;
import java.util.List;

/**
 * SystemManager coordinates all game systems.
 * 
 * Systems are updated in order:
 * 1. InputSystem - Gather input
 * 2. PhysicsSystem - Update physics
 * 3. CollisionSystem - Detect and resolve collisions
 * 4. (Future systems...)
 */
public class SystemManager {
    
    private List<GameSystem> systems;
    private GameWorld gameWorld;
    
    // Individual systems (for easy access)
    private InputSystem inputSystem;
    private PhysicsSystem physicsSystem;
    private CollisionSystem collisionSystem;
    
    /**
     * Create system manager
     */
    public SystemManager(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.systems = new ArrayList<>();
        
        // Create and register systems
        initializeSystems();
    }
    
    /**
     * Initialize all systems
     */
    private void initializeSystems() {
        Logger.info("Initializing game systems...");
        
        // Create systems in order
        inputSystem = new InputSystem(gameWorld);
        physicsSystem = new PhysicsSystem(gameWorld);
        collisionSystem = new CollisionSystem(gameWorld);
        
        // Register systems (order matters!)
        registerSystem(inputSystem);
        registerSystem(physicsSystem);
        registerSystem(collisionSystem);
        
        // Initialize all systems
        for (GameSystem system : systems) {
            system.initialize();
        }
        
        Logger.info("All systems initialized");
    }
    
    /**
     * Register a system
     */
    private void registerSystem(GameSystem system) {
        systems.add(system);
    }
    
    /**
     * Update all systems
     */
    public void update(float deltaTime) {
        for (GameSystem system : systems) {
            if (system.isEnabled()) {
                system.update(deltaTime);
            }
        }
    }
    
    /**
     * Shutdown all systems
     */
    public void shutdown() {
        for (GameSystem system : systems) {
            system.shutdown();
        }
        systems.clear();
        Logger.info("All systems shutdown");
    }
    
    // ========== SYSTEM ACCESS ==========
    
    /**
     * Get input system
     */
    public InputSystem getInputSystem() {
        return inputSystem;
    }
    
    /**
     * Get physics system
     */
    public PhysicsSystem getPhysicsSystem() {
        return physicsSystem;
    }
    
    /**
     * Get collision system
     */
    public CollisionSystem getCollisionSystem() {
        return collisionSystem;
    }
    
    /**
     * Get all systems
     */
    public List<GameSystem> getAllSystems() {
        return new ArrayList<>(systems);
    }
}