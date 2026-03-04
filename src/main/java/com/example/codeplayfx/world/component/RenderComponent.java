package com.example.codeplayfx.world.component;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.render.ShapeType;

/**
 * RenderComponent stores visual properties of an entity.
 * 
 * Properties:
 * - Shape type (rectangle, circle, image, etc.)
 * - Color (fill and stroke)
 * - Size (width and height in grid units)
 * - Image (for sprite-based entities)
 */
public class RenderComponent implements Component {
    
    // Shape and appearance
    private ShapeType shapeType;
    private Color fillColor;
    private Color strokeColor;
    private float strokeWidth;
    
    // Size (in grid units)
    private float width;
    private float height;
    
    // Image (for sprite rendering)
    private Image image;
    private String imagePath;
    
    // Rendering state
    private boolean active;
    private float opacity;  // 0.0 to 1.0
    
    /**
     * Create render component with default rectangle shape
     */
    public RenderComponent() {
        this(ShapeType.RECTANGLE, Color.LIGHTBLUE, 1.0f, 1.0f);
    }
    
    /**
     * Create render component with specific shape and color
     */
    public RenderComponent(ShapeType shapeType, Color fillColor, float width, float height) {
        this.shapeType = shapeType;
        this.fillColor = fillColor;
        this.strokeColor = Color.BLACK;
        this.strokeWidth = 1.0f;
        this.width = width;
        this.height = height;
        this.active = true;
        this.opacity = 1.0f;
        this.image = null;
        this.imagePath = null;
    }
    
    @Override
    public void update(float deltaTime) {
        // RenderComponent doesn't need per-frame updates
    }
    
    // ========== SHAPE ==========
    
    public ShapeType getShapeType() {
        return shapeType;
    }
    
    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
    }
    
    // ========== COLOR ==========
    
    public Color getFillColor() {
        return fillColor;
    }
    
    public void setFillColor(Color fillColor) {
        this.fillColor = fillColor;
    }
    
    /**
     * Set fill color using RGB values (0-255)
     */
    public void setFillColor(int r, int g, int b) {
        this.fillColor = Color.rgb(r, g, b);
    }
    
    /**
     * Set fill color using hex string (e.g., "#FF5733")
     */
    public void setFillColor(String hex) {
        this.fillColor = Color.web(hex);
    }
    
    public Color getStrokeColor() {
        return strokeColor;
    }
    
    public void setStrokeColor(Color strokeColor) {
        this.strokeColor = strokeColor;
    }
    
    public void setStrokeColor(int r, int g, int b) {
        this.strokeColor = Color.rgb(r, g, b);
    }
    
    public float getStrokeWidth() {
        return strokeWidth;
    }
    
    public void setStrokeWidth(float strokeWidth) {
        this.strokeWidth = Math.max(0, strokeWidth);
    }
    
    // ========== SIZE ==========
    
    public float getWidth() {
        return width;
    }
    
    public void setWidth(float width) {
        this.width = Math.max(0, width);
    }
    
    public float getHeight() {
        return height;
    }
    
    public void setHeight(float height) {
        this.height = Math.max(0, height);
    }
    
    public void setSize(float width, float height) {
        setWidth(width);
        setHeight(height);
    }
    
    // ========== IMAGE/SPRITE ==========
    
    public Image getImage() {
        return image;
    }
    
    public void setImage(Image image) {
        this.image = image;
        this.shapeType = ShapeType.IMAGE;
    }
    
    /**
     * Load image from file path
     */
    public void loadImage(String path) {
        try {
            this.image = new Image(path);
            this.imagePath = path;
            this.shapeType = ShapeType.IMAGE;
        } catch (Exception e) {
            System.err.println("Failed to load image: " + path);
            e.printStackTrace();
        }
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    // ========== OPACITY ==========
    
    public float getOpacity() {
        return opacity;
    }
    
    public void setOpacity(float opacity) {
        this.opacity = Math.max(0f, Math.min(1f, opacity));
    }
    
    // ========== ACTIVE STATE ==========
    
    @Override
    public boolean isActive() {
        return active;
    }
    
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
    
    @Override
    public String toString() {
        return String.format("Render[shape=%s, color=%s, size=%.1fx%.1f]",
            shapeType, fillColor, width, height);
    }
}