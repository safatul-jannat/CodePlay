package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.physics.Vector2;
import com.example.codeplayfx.world.render.MessageBoardRenderer;
import javafx.scene.paint.Color;

/**
 * MessageBoard - Simple UI element for displaying messages.
 * Can be positioned anywhere in the world and styled.
 */
public class MessageBoard extends Entity {
    
    // Message content
    private String message = "";
    private boolean isShowing = false;
    private float showTimer = 0;
    private float showDuration = 0;
    
    // Visual properties
    private float width = 10.0f;      // Width in grid units
    private float height = 3.0f;       // Height in grid units
    private Color backgroundColor = Color.rgb(0, 0, 0, 0.8);
    private Color textColor = Color.WHITE;
    private int fontSize = 16;
    private float padding = 0.5f;      // Padding in grid units
    
    public MessageBoard(float x, float y) {
        super(x, y);
        
        // Make it visible by default when created
        this.visible = true;
        this.active = true;
        
        // Set up renderer
        this.renderer = new MessageBoardRenderer();
        
        // Name it
        this.name = "MessageBoard_" + id;
        
        // Default hidden until show() is called
        this.visible = false;
    }
    

    /**
     * Show a message for a specific duration (in seconds)
     */
    public void showFor(String text, float seconds) {
        this.message = text;
        this.isShowing = true;
        this.showTimer = 0;
        this.showDuration = Math.max(0.5f, seconds);
        this.visible = true;
        UserGame.getCurrentGame().addEntity(this);

        System.out.println("MessageBoard (" + seconds + "s): " + text);
    }
    
    /**
     * Show a permanent message (until hide() is called)
     */
    public void showMessage(String text) {
        this.message = text;
        this.isShowing = true;
        this.showTimer = 0;
        this.showDuration = -1;  // Permanent
        this.visible = true;
        UserGame.getCurrentGame().addEntity(this);

        System.out.println("MessageBoard (permanent): " + text);
    }
    
    /**
     * Hide the message board
     */
    public void hide() {
        this.isShowing = false;
        this.visible = false;
        this.message = "";
    }
    
    /**
     * Update message timer
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        
        if (isShowing && showDuration > 0) {
            showTimer += deltaTime;
            if (showTimer >= showDuration) {
                hide();
            }
        }
    }
    
    // ========== SIZE & POSITION ==========
    
    public void setSize(float width, float height) {
        this.width = Math.max(1.0f, width);
        this.height = Math.max(0.5f, height);
    }
    
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
    }
    
    // ========== STYLING ==========
    
    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
    }

    public void setBackgroundColor(String colorNameOrHex) {
        this.backgroundColor = parseColor(colorNameOrHex);
    }
    
    public void setTextColor(Color color) {
        this.textColor = color;
    }
    
    public void setTextColor(int r, int g, int b) {
        this.textColor = Color.rgb(r, g, b);
    }
    
    public void setTextColor(String colorNameOrHex) {
        this.textColor = parseColor(colorNameOrHex);
    }
    
    public void setFontSize(int size) {
        this.fontSize = Math.max(8, Math.min(72, size));
    }
    
    public void setPadding(float padding) {
        this.padding = Math.max(0, Math.min(2, padding));
    }
    
    // ========== GETTERS ==========
    
    public String getMessage() { return message; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public Color getBackgroundColor() { return backgroundColor; }
    public Color getTextColor() { return textColor; }
    public int getFontSize() { return fontSize; }
    public float getPadding() { return padding; }
    public boolean isShowing() { return isShowing; }
    
    // ========== HELPER ==========
    
    private Color parseColor(String name) {
        switch (name.toUpperCase()) {
            case "RED": return Color.rgb(255, 0, 0, 0.8);
            case "GREEN": return Color.rgb(0, 255, 0, 0.8);
            case "BLUE": return Color.rgb(0, 0, 255, 0.8);
            case "YELLOW": return Color.rgb(255, 255, 0, 0.8);
            case "BLACK": return Color.rgb(0, 0, 0, 0.8);
            case "WHITE": return Color.rgb(255, 255, 255, 0.8);
            default: return Color.web(name, 0.8);
        }
    }
    
    @Override
    public void show() {
        // Don't auto-show - requires explicit showMessage() call
    }
}