package com.example.codeplayfx.scripting.runtime;

import com.example.codeplayfx.world.entity.Collectible;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.engine.system.InputSystem;
import com.example.codeplayfx.engine.system.PhysicsSystem;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.world.entity.KillZone;

import java.util.ArrayList;
import java.util.List;

/**
 * UserGame - Abstract base class for user-created games.
 *
 * Also owns the collectible sequence — because UserGame lives in the
 * engine classloader, not the dynamic user classloader. This avoids
 * the two-classloader bug where static fields in Collectible would
 * be split across two copies of the class.
 */
public abstract class UserGame {

    protected GameWorld world;
    protected InputSystem input;
    private GameEngine engine;
    private static UserGame currentGame;

    // ========== COLLECTIBLE SEQUENCE ==========
    private List<Collectible> collectibleSequence = new ArrayList<>();
    private int collectibleIndex = 0;
    private int collectibleScore = 0;

    // ========== LIFECYCLE ==========

    public void start() {}

    public abstract void execute();

    // ========== ENGINE INJECTION ==========

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

    public GameWorld getWorld() {
        return world;
    }

    public void addEntity(Entity e) {
        if (world != null) {
            world.addEntity(e);
        }
    }

    // ========== COLLECTIBLE MANAGEMENT ==========

    public void addToSequence(Collectible... collectibles) {
        for (Collectible c : collectibles) {
            collectibleSequence.add(c);
        }
        if (collectibleIndex == 0 && collectibleSequence.size() == collectibles.length) {
            showCurrentCollectible();
        }
    }

    public void onCollectibleCollected(Collectible c) {
        collectibleScore += c.getPointValue();
        collectibleIndex++;
        showCurrentCollectible();
    }

    private void showCurrentCollectible() {
        if (collectibleIndex >= collectibleSequence.size()) return;
        Collectible next = collectibleSequence.get(collectibleIndex);
        if (!next.isCollected()) {
            next.show();
        }
    }

    public void resetCollectibles() {
        collectibleSequence = new ArrayList<>();
        collectibleIndex = 0;
        collectibleScore = 0;
    }

    public int getCollectibleScore()     { return collectibleScore; }
    public int getCollectibleRemaining() { return collectibleSequence.size() - collectibleIndex; }

    // ========== BACKGROUND ==========

    protected void setBackground(String filename) {
        if (engine != null) engine.setBackground(filename);
    }

    protected void clearBackground() {
        if (engine != null) engine.clearBackground();
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