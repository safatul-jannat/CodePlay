package com.example.codeplayfx.scripting.runtime;

import com.example.codeplayfx.world.entity.BlockPlayer;
import com.example.codeplayfx.world.entity.Enemy;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.engine.system.InputSystem;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.engine.system.PhysicsSystem;

/**
 * UserGame - Abstract base class for user-created games.
 * 
 * Users extend this class and implement execute() method.
 * 
 * Example user code:
 * <pre>
 * public class MyGame extends UserGame {
 *     
 *     BlockPlayer player;
 *     
 *     public void execute() {
 *         // Called once on startup
 *         if (player == null) {
 *             player = new BlockPlayer(5, 5);
 *             player.show();
 *         }
 *         
 *         // Called every frame
 *         if (isKeyPressed("RIGHT")) {
 *             player.moveRight();
 *         }
 *     }
 * }
 * </pre>
 */
public abstract class UserGame {
    
    // Engine references (injected by runtime)
    public GameWorld world;
    protected InputSystem input;
    private static UserGame currentGame;


    /**
     * Execute method - called every frame
     * Users implement their game logic here
     */
    public abstract void execute();
    
    /**
     * Set world reference (called by engine)
     */
    public void setWorld(GameWorld world) {

        this.world = world;
        currentGame = this;
    }
    public static UserGame getCurrentGame() {
        return currentGame;
    }

    public void addEntity(Entity e) {
        if (world != null) {
            world.addEntity(e);
        }
    }


    /**
     * Set input system reference (called by engine)
     */
    public void setInputSystem(InputSystem input) {
        this.input = input;
    }
    
    // ========== HELPER METHODS (for user convenience) ==========
    
    /**
     * Check if a key is pressed (shortcut)
     */
    protected boolean isKeyPressed(String key) {
        return input != null && input.isKeyPressed(key);
    }
    
    /**
     * Check if a key was just pressed (shortcut)
     */
    protected boolean isKeyJustPressed(String key) {
        return input != null && input.isKeyJustPressed(key);
    }
    
    /**
     * Get mouse world X position (shortcut)
     */
    protected float getMouseX() {
        return input != null ? input.getMouseWorldX() : 0;
    }
    
    /**
     * Get mouse world Y position (shortcut)
     */
    protected float getMouseY() {
        return input != null ? input.getMouseWorldY() : 0;
    }
    
    /**
     * Check if mouse left button is pressed (shortcut)
     */
    protected boolean isMousePressed() {
        return input != null && input.isMouseLeftPressed();
    }
    
    /**
     * Check if mouse left button was just clicked (shortcut)
     */
    protected boolean isMouseClicked() {
        return input != null && input.isMouseLeftJustPressed();
    }

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