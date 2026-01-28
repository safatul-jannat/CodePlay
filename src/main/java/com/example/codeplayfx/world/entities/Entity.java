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

    // Movement state
    private boolean isMovingLeft = false;
    private boolean isMovingRight = false;
    private double moveTargetX = -1; // -1 means no target

    public Entity() {
        super();
        this.health = 100;
        this.speed = 1.0;
    }

    // ===== MOVEMENT IMPLEMENTATION =====

    @Override
    public void moveLeft() {
        moveLeft(10); // Default: move 10px
    }

    @Override
    public void moveRight() {
        moveRight(10); // Default: move 10px
    }

    @Override
    public void moveLeft(double distance) {
        x -= distance;
        System.out.println(getClass().getSimpleName() + " moved left " + distance + "px to x=" + x);
    }

    @Override
    public void moveRight(double distance) {
        x += distance;
        System.out.println(getClass().getSimpleName() + " moved right " + distance + "px to x=" + x);
    }

    @Override
    public void moveLeft(double startX, double endX) {
        this.x = startX;
        this.moveTargetX = endX;
        System.out.println(getClass().getSimpleName() + " will move from x=" + startX + " to x=" + endX);
    }

    @Override
    public void moveRight(double startX, double endX) {
        this.x = startX;
        this.moveTargetX = endX;
        System.out.println(getClass().getSimpleName() + " will move from x=" + startX + " to x=" + endX);
    }

    @Override
    public void startMovingLeft() {
        isMovingLeft = true;
        isMovingRight = false;
        System.out.println(getClass().getSimpleName() + " started moving left continuously");
    }

    @Override
    public void startMovingRight() {
        isMovingRight = true;
        isMovingLeft = false;
        System.out.println(getClass().getSimpleName() + " started moving right continuously");
    }

    @Override
    public void stopMoving() {
        isMovingLeft = false;
        isMovingRight = false;
        moveTargetX = -1;
        System.out.println(getClass().getSimpleName() + " stopped moving");
    }

    @Override
    public void update() {
        // Handle continuous movement
        if (isMovingLeft) {
            x -= speed;
        }
        if (isMovingRight) {
            x += speed;
        }

        // Handle target movement
        if (moveTargetX > 0) {
            if (x < moveTargetX) {
                x += speed;
                if (x >= moveTargetX) {
                    x = moveTargetX;
                    moveTargetX = -1;
                    System.out.println(getClass().getSimpleName() + " reached target x=" + x);
                }
            } else if (x > moveTargetX) {
                x -= speed;
                if (x <= moveTargetX) {
                    x = moveTargetX;
                    moveTargetX = -1;
                    System.out.println(getClass().getSimpleName() + " reached target x=" + x);
                }
            }
        }
    }

    @Override
    public Rect getBounds() {
        return new Rect(x, y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println(getClass().getSimpleName() + " collided with " + other.getClass().getSimpleName());
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