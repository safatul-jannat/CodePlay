// Rect.java
package com.example.codeplayfx.world.base;

public class Rect {
    public double x, y, width, height;

    public Rect() {
        this(0, 0, 0, 0);
    }

    public Rect(double x, double y, double width, double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public boolean intersects(Rect other) {
        return this.x < other.x + other.width &&
                this.x + this.width > other.x &&
                this.y < other.y + other.height &&
                this.y + this.height > other.y;
    }

    public boolean contains(double px, double py) {
        return px >= x && px <= x + width &&
                py >= y && py <= y + height;
    }

    @Override
    public String toString() {
        return String.format("Rect[x=%.1f, y=%.1f, w=%.1f, h=%.1f]", x, y, width, height);
    }
}