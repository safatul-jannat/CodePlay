// GameObject.java
package com.example.codeplayfx.world.base;

public abstract class GameObject {
    protected double x, y;
    protected boolean active;

    public GameObject() {
        this.active = true;
    }

    public abstract void update();
    public abstract void render();

    public double getX() { return x; }
    public double getY() { return y; }

    public void setPosition(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}