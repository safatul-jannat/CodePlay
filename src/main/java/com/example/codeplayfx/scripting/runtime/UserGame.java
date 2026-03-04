package com.example.codeplayfx.scripting.runtime;

import com.example.codeplayfx.world.entity.BlockPlayer;
import com.example.codeplayfx.world.entity.Enemy;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.engine.system.InputSystem;
import com.example.codeplayfx.engine.system.PhysicsSystem;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.engine.core.GameEngine;

/**
 * UserGame - Abstract base class for user-created games.
 *
 * Users extend this class and implement:
 *   start()   — called ONCE on startup   (create objects here)
 *   execute() — called EVERY FRAME       (input/movement/logic here)
 */
public abstract class UserGame {

    // Engine references (injected by runtime)
    protected GameWorld world;
    protected InputSystem input;
    private GameEngine engine;                     // needed for setBackground()
    private static UserGame currentGame;

    // ========== LIFECYCLE ==========

    /**
     * Called ONCE when the game starts.
     * Override this to create players, enemies, set background, etc.
     */
    public void start() {
        // Default: do nothing. Users override this.
    }

    /**
     * Called EVERY FRAME.
     * Users implement their per-frame game logic here.
     */
    public abstract void execute();

    // ========== ENGINE INJECTION (called by ScriptRuntime) ==========

    public void setWorld(GameWorld world) {
        this.world = world;
        currentGame = this;
    }

    public void setInputSystem(InputSystem input) {
        this.input = input;
    }

    public void setEngine(GameEngine engine) {
        this.engine = engine;
    }

    public static UserGame getCurrentGame() {
        return currentGame;
    }

    public void addEntity(Entity e) {
        if (world != null) {
            world.addEntity(e);
        }
    }

    // ========== BACKGROUND ==========

    /**
     * Set a background image for the canvas.
     * The image stretches to fill the canvas and sits behind all entities.
     *
     * Usage:  setBackground("sky.jpg");
     *         setBackground("forest.png");
     *
     * Place your image in:  src/main/resources/assets/backgrounds/
     */
    protected void setBackground(String filename) {
        if (engine != null) {
            engine.setBackground(filename);
        }
    }

    /**
     * Remove the background image.
     */
    protected void clearBackground() {
        if (engine != null) {
            engine.clearBackground();
        }
    }

    // ========== INPUT HELPERS ==========

    protected boolean isKeyPressed(String key) {
        return input != null && input.isKeyPressed(key);
    }

    protected boolean isKeyJustPressed(String key) {
        return input != null && input.isKeyJustPressed(key);
    }

    protected float getMouseX() {
        return input != null ? input.getMouseWorldX() : 0;
    }

    protected float getMouseY() {
        return input != null ? input.getMouseWorldY() : 0;
    }

    protected boolean isMousePressed() {
        return input != null && input.isMouseLeftPressed();
    }

    protected boolean isMouseClicked() {
        return input != null && input.isMouseLeftJustPressed();
    }

    // ========== GRAVITY HELPERS ==========

    protected void setGravity(boolean enabled) {
        if (world != null && world.getPhysicsSystem() != null) {
            world.getPhysicsSystem().setGlobalGravityEnabled(enabled);
        }
    }

    protected void setGravity(float gravityValue) {
        if (world != null && world.getPhysicsSystem() != null) {
            world.getPhysicsSystem().setGlobalGravity(gravityValue);
        }
    }
}