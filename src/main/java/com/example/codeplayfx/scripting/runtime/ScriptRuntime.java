package com.example.codeplayfx.scripting.runtime;

import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.util.Logger;

public class ScriptRuntime {

    private UserGame userGame;
    private GameEngine engine;
    private boolean initialized;
    private boolean startCalled;

    public ScriptRuntime(UserGame userGame, GameEngine engine) {
        this.userGame = userGame;
        this.engine = engine;
        this.initialized = false;
        this.startCalled = false;
    }

    public void initialize() {
        if (initialized) return;

        Logger.info("Initializing user game: " + userGame.getClass().getSimpleName());

        userGame.setWorld(engine.getGameWorld());
        userGame.setInputSystem(engine.getSystemManager().getInputSystem());
        userGame.setEngine(engine);

        initialized = true;
        Logger.info("User game initialized");
    }

    public void update() {
        if (!initialized) {
            initialize();
        }

        if (!startCalled) {
            startCalled = true;
            // Reset on the UserGame instance — same classloader, no split
            userGame.resetCollectibles();
            try {
                userGame.start();
            } catch (Exception e) {
                Logger.error("User game start() error", e);
                engine.pause();
                throw new RuntimeException("User game start() error: " + e.getMessage(), e);
            }
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