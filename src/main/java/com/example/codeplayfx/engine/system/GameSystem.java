package com.example.codeplayfx.engine.system;

import com.example.codeplayfx.world.GameWorld;

/**
 * Abstract base class for all game systems.
 * Systems handle specific aspects of the game (input, physics, collision, etc.)
 * 
 * Each system:
 * - Initializes once
 * - Updates every frame
 * - Can be enabled/disabled
 */
public abstract class GameSystem {
    
    protected GameWorld gameWorld;
    protected boolean enabled;
    
    /**
     * Create a game system
     */
    public GameSystem(GameWorld gameWorld) {
        this.gameWorld = gameWorld;
        this.enabled = true;
    }
    
    /**
     * Initialize the system (called once at startup)
     */
    public abstract void initialize();
    
    /**
     * Update the system (called every frame)
     * @param deltaTime time since last frame in seconds
     */
    public abstract void update(float deltaTime);
    
    /**
     * Shutdown the system (cleanup)
     */
    public void shutdown() {
        // Override if cleanup needed
    }
    
    /**
     * Enable/disable the system
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    /**
     * Get the game world
     */
    protected GameWorld getGameWorld() {
        return gameWorld;
    }
}