package com.example.codeplayfx.engine.system;

import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.physics.Vector2;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * InputSystem handles keyboard and mouse input.
 * 
 * Features:
 * - Track which keys are pressed/released
 * - Track mouse position and clicks
 * - Simple API: isKeyPressed(), getMousePosition(), etc.
 */
public class InputSystem extends GameSystem {
    
    // Keyboard state
    private Set<KeyCode> pressedKeys;
    private Set<KeyCode> justPressedKeys;
    private Set<KeyCode> justReleasedKeys;
    
    // String-based key mapping (for user convenience)
    private Map<String, KeyCode> keyMap;
    
    // Mouse state
    private Vector2 mousePosition;
    private Vector2 mouseWorldPosition;  // In grid coordinates
    private boolean mouseLeftPressed;
    private boolean mouseRightPressed;
    private boolean mouseLeftJustPressed;
    private boolean mouseRightJustPressed;
    
    // JavaFX scene (for input listening)
    private Scene scene;
    
    /**
     * Create input system
     */
    public InputSystem(GameWorld gameWorld) {
        super(gameWorld);
        
        this.pressedKeys = new HashSet<>();
        this.justPressedKeys = new HashSet<>();
        this.justReleasedKeys = new HashSet<>();
        this.keyMap = new HashMap<>();
        
        this.mousePosition = Vector2.ZERO;
        this.mouseWorldPosition = Vector2.ZERO;
        this.mouseLeftPressed = false;
        this.mouseRightPressed = false;
        this.mouseLeftJustPressed = false;
        this.mouseRightJustPressed = false;
        
        initializeKeyMap();
    }
    
    /**
     * Initialize key name mappings
     */
    private void initializeKeyMap() {
        // Arrow keys
        keyMap.put("UP", KeyCode.UP);
        keyMap.put("DOWN", KeyCode.DOWN);
        keyMap.put("LEFT", KeyCode.LEFT);
        keyMap.put("RIGHT", KeyCode.RIGHT);
        
        // WASD
        keyMap.put("W", KeyCode.W);
        keyMap.put("A", KeyCode.A);
        keyMap.put("S", KeyCode.S);
        keyMap.put("D", KeyCode.D);
        
        // Common keys
        keyMap.put("SPACE", KeyCode.SPACE);
        keyMap.put("ENTER", KeyCode.ENTER);
        keyMap.put("ESCAPE", KeyCode.ESCAPE);
        keyMap.put("SHIFT", KeyCode.SHIFT);
        keyMap.put("CTRL", KeyCode.CONTROL);
        keyMap.put("ALT", KeyCode.ALT);
        
        // Numbers
        for (int i = 0; i <= 9; i++) {
            keyMap.put(String.valueOf(i), KeyCode.getKeyCode(String.valueOf(i)));
        }
        
        // Letters
        for (char c = 'A'; c <= 'Z'; c++) {
            keyMap.put(String.valueOf(c), KeyCode.getKeyCode(String.valueOf(c)));
        }
    }
    
    @Override
    public void initialize() {
        Logger.info("InputSystem initialized");
    }
    
    /**
     * Attach input listeners to JavaFX scene
     */
    public void attachToScene(Scene scene) {
        this.scene = scene;

        scene.setOnKeyPressed(this::handleKeyPressed);
        scene.setOnKeyReleased(this::handleKeyReleased);

        scene.setOnMousePressed(this::handleMousePressed);
        scene.setOnMouseReleased(this::handleMouseReleased);
        scene.setOnMouseMoved(this::handleMouseMoved);
        scene.setOnMouseDragged(this::handleMouseDragged);

        Logger.info("InputSystem attached to scene");
    }
    
    @Override
    public void update(float deltaTime) {
        if (!enabled) return;
        
        // Clear "just pressed/released" states each frame
        justPressedKeys.clear();
        justReleasedKeys.clear();
        mouseLeftJustPressed = false;
        mouseRightJustPressed = false;
        if (scene != null && !scene.getWindow().isFocused()) {
            // Window lost focus - clear all keys
            pressedKeys.clear();
        }
    }
    
    // ========== KEYBOARD INPUT HANDLERS ==========
    
    private void handleKeyPressed(KeyEvent event) {
        KeyCode key = event.getCode();
        System.out.println(" InputSystem received key: " + key);

        // Track if this is a new press (wasn't already pressed)
        if (!pressedKeys.contains(key)) {
            justPressedKeys.add(key);
            System.out.println("   Added to justPressedKeys: " + key);

        }
        
        pressedKeys.add(key);
        System.out.println("   pressedKeys now contains: " + pressedKeys);

    }
    
    private void handleKeyReleased(KeyEvent event) {
        KeyCode key = event.getCode();
        pressedKeys.remove(key);
        justReleasedKeys.add(key);
    }
    
    // ========== MOUSE INPUT HANDLERS ==========
    
    private void handleMousePressed(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mouseLeftPressed = true;
            mouseLeftJustPressed = true;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            mouseRightPressed = true;
            mouseRightJustPressed = true;
        }
        updateMousePosition(event);
    }
    
    private void handleMouseReleased(MouseEvent event) {
        if (event.getButton() == MouseButton.PRIMARY) {
            mouseLeftPressed = false;
        } else if (event.getButton() == MouseButton.SECONDARY) {
            mouseRightPressed = false;
        }
        updateMousePosition(event);
    }
    
    private void handleMouseMoved(MouseEvent event) {
        updateMousePosition(event);
    }
    
    private void handleMouseDragged(MouseEvent event) {
        updateMousePosition(event);
    }
    
    private void updateMousePosition(MouseEvent event) {
        // Screen position
        mousePosition = new Vector2((float) event.getX(), (float) event.getY());
        
        // World position (grid coordinates)
        mouseWorldPosition = gameWorld.getCoordinateSystem().toWorld(mousePosition);
    }
    
    // ========== PUBLIC API - KEYBOARD ==========
    
    /**
     * Check if a key is currently pressed
     * @param keyName key name (e.g., "SPACE", "W", "UP", "1")
     */
    public boolean isKeyPressed(String keyName) {
        KeyCode key = keyMap.get(keyName.toUpperCase());
        if (key == null) return false;
        return pressedKeys.contains(key);
    }
    
    /**
     * Check if a key was just pressed this frame
     */
    public boolean isKeyJustPressed(String keyName) {
        KeyCode key = keyMap.get(keyName.toUpperCase());
        if (key == null) return false;
        return justPressedKeys.contains(key);
    }
    
    /**
     * Check if a key was just released this frame
     */
    public boolean isKeyJustReleased(String keyName) {
        KeyCode key = keyMap.get(keyName.toUpperCase());
        if (key == null) return false;
        return justReleasedKeys.contains(key);
    }
    
    /**
     * Check if a key is currently pressed (using KeyCode)
     */
    public boolean isKeyPressed(KeyCode key) {
        return pressedKeys.contains(key);
    }
    
    /**
     * Check if any key is pressed
     */
    public boolean isAnyKeyPressed() {
        return !pressedKeys.isEmpty();
    }
    
    // ========== PUBLIC API - MOUSE ==========
    
    /**
     * Get mouse position in screen coordinates (pixels)
     */
    public Vector2 getMousePosition() {
        return mousePosition;
    }
    
    /**
     * Get mouse position in world coordinates (grid)
     */
    public Vector2 getMouseWorldPosition() {
        return mouseWorldPosition;
    }
    
    /**
     * Check if left mouse button is pressed
     */
    public boolean isMouseLeftPressed() {
        return mouseLeftPressed;
    }
    
    /**
     * Check if right mouse button is pressed
     */
    public boolean isMouseRightPressed() {
        return mouseRightPressed;
    }
    
    /**
     * Check if left mouse button was just clicked this frame
     */
    public boolean isMouseLeftJustPressed() {
        return mouseLeftJustPressed;
    }
    
    /**
     * Check if right mouse button was just clicked this frame
     */
    public boolean isMouseRightJustPressed() {
        return mouseRightJustPressed;
    }
    
    /**
     * Get mouse X position (screen)
     */
    public float getMouseX() {
        return mousePosition.x;
    }
    
    /**
     * Get mouse Y position (screen)
     */
    public float getMouseY() {
        return mousePosition.y;
    }
    
    /**
     * Get mouse X position (world/grid)
     */
    public float getMouseWorldX() {
        return mouseWorldPosition.x;
    }
    
    /**
     * Get mouse Y position (world/grid)
     */
    public float getMouseWorldY() {
        return mouseWorldPosition.y;
    }
    
    @Override
    public void shutdown() {
        pressedKeys.clear();
        justPressedKeys.clear();
        justReleasedKeys.clear();
        Logger.info("InputSystem shutdown");
    }
}