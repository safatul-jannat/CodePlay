package com.example.codeplayfx.scripting.runtime;

import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.util.Logger;

/**
 * ScriptRuntime - Executes user games within the engine.
 *
 * Calls start() once, then execute() every frame.
 */
public class ScriptRuntime {

    private UserGame userGame;
    private GameEngine engine;
    private boolean initialized;

    public ScriptRuntime(UserGame userGame, GameEngine engine) {
        this.userGame = userGame;
        this.engine = engine;
        this.initialized = false;
    }

    /**
     * Initialize — injects all engine references and calls start() once.
     */
    public void initialize() {
        if (initialized) return;

        Logger.info("Initializing user game: " + userGame.getClass().getSimpleName());

        // Inject all references
        userGame.setWorld(engine.getGameWorld());
        userGame.setInputSystem(engine.getSystemManager().getInputSystem());
        userGame.setEngine(engine);   // ← needed for setBackground()

        initialized = true;
        Logger.info("User game initialized");

        // Call start() exactly once
        try {
            userGame.start();
        } catch (Exception e) {
            Logger.error("User game start() error", e);
            engine.pause();
            throw new RuntimeException("User game start() error: " + e.getMessage(), e);
        }
    }

    /**
     * Called every frame — runs execute().
     */
    public void update() {
        if (!initialized) {
            initialize();
        }

        try {
            userGame.execute();
        } catch (Exception e) {
            Logger.error("User game runtime error", e);
            engine.pause();
            throw new RuntimeException("User game error: " + e.getMessage(), e);
        }
    }

    public UserGame getUserGame() { return userGame; }
    public boolean isInitialized() { return initialized; }
}