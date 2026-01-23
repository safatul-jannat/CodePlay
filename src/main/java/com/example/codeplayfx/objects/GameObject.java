package com.example.codeplayfx.objects;








import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public abstract class GameObject {
    protected double x;
    protected double y;
    protected double width;
    protected double height;
    protected Color color;
    protected String id;

    public GameObject(double x, double y, double width, double height, Color color, String id) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.color = color;
        this.id = id;
    }

    // ONLY this method - no update, no movement
    public abstract void draw(GraphicsContext gc);

    // Getters only - no setters for position
    public double getX() { return x; }
    public double getY() { return y; }
    public double getWidth() { return width; }
    public double getHeight() { return height; }
    public Color getColor() { return color; }
    public String getId() { return id; }
}