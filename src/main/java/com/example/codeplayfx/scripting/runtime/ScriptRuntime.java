package com.example.codeplayfx.scripting.runtime;

import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.util.Logger;

/**
 * ScriptRuntime - Executes user games within the engine.
 * 
 * Responsibilities:
 * - Inject engine references into UserGame
 * - Call execute() every frame
 * - Handle runtime errors safely
 */
public class ScriptRuntime {
    
    private UserGame userGame;
    private GameEngine engine;
    private boolean initialized;
    
    /**
     * Create script runtime
     */
    public ScriptRuntime(UserGame userGame, GameEngine engine) {
        this.userGame = userGame;
        this.engine = engine;
        this.initialized = false;
    }
    
    /**
     * Initialize the user game
     */
    public void initialize() {
        if (initialized) return;
        
        Logger.info("Initializing user game: " + userGame.getClass().getSimpleName());
        
        // Inject engine references
        userGame.setWorld(engine.getGameWorld());
        userGame.setInputSystem(engine.getSystemManager().getInputSystem());
        
        initialized = true;
        Logger.info("User game initialized");
    }
    
    /**
     * Update user game (call execute() method)
     */
    public void update() {
        if (!initialized) {
            initialize();
        }
        
        try {
            // Call user's execute() method
            userGame.execute();
            
        } catch (Exception e) {
            Logger.error("User game runtime error", e);
            // Could pause engine or show error to user
            engine.pause();
            throw new RuntimeException("User game error: " + e.getMessage(), e);
        }
    }
    
    /**
     * Get the user game instance
     */
    public UserGame getUserGame() {
        return userGame;
    }
    
    /**
     * Check if initialized
     */
    public boolean isInitialized() {
        return initialized;
    }
}