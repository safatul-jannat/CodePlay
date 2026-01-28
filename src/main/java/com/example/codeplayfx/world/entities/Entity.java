// Entity.java
package com.example.codeplayfx.world.entities;

import com.example.codeplayfx.world.base.GameObject;
import com.example.codeplayfx.world.base.Movable;
import com.example.codeplayfx.world.base.Collidable;
import com.example.codeplayfx.world.base.Rect;

public abstract class Entity extends GameObject implements Movable, Collidable {
    protected double speed;
    protected int health;
    protected int width = 32;
    protected int height = 32;

    public Entity() {
        super();
        this.health = 100;
        this.speed = 1.0;
    }

    @Override
    public void moveLeft() {
        x -= speed;
    }

    @Override
    public void moveRight() {
        x += speed;
    }

    @Override
    public void moveUp() {
        y -= speed;
    }

    @Override
    public void moveDown() {
        y += speed;
    }

    @Override
    public Rect getBounds() {
        return new Rect(x, y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println("Collision detected with " + other.getClass().getSimpleName());
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            health = 0;
            active = false;
        }
    }

    public boolean isAlive() {
        return health > 0;
    }

    public double getSpeed() { return speed; }
    public void setSpeed(double speed) { this.speed = speed; }
    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }
}