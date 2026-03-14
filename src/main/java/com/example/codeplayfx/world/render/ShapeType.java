package com.example.codeplayfx.world.render;

/**
 * Different shapes that can be rendered for entities.
 */
public enum ShapeType {
    /**
     * Rectangle (default for most entities)
     */
    RECTANGLE,
    
    /**
     * Circle/Oval
     */
    CIRCLE,
    
    /**
     * Rounded rectangle
     */
    ROUNDED_RECTANGLE,
    
    /**
     * Triangle
     */
    TRIANGLE,
    
    /**
     * Image/Sprite (loaded from file)
     */
    IMAGE
}