package com.example.codeplayfx.engine.api;

import com.example.codeplayfx.world.GameWorld;

public class GameAPI {
    private GameWorld world;

    public GameAPI() {
        this.world = GameWorld.getInstance();
        System.out.println("GameAPI initialized");
    }

    // Simple API methods for demo
    public void log(String message) {
        System.out.println("[GAME] " + message);
    }

    public void showMessage(String message) {
        System.out.println("=== " + message + " ===");
    }


    public GameWorld getWorld() {
        return world;
    }

    public void clearWorld() {
        world.getObjects().clear();
        log("World cleared");
    }
}