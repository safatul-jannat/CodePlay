package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.world.physics.Vector2;
import com.example.codeplayfx.scripting.runtime.UserGame;

/**
 * Physical entities have velocity, acceleration, and respond to physics.
 */
public abstract class PhysicalEntity extends Entity {

    // Physics properties
    protected Vector2 velocity;
    protected Vector2 acceleration;
    protected float mass;
    protected float friction;
    protected boolean useGravity;
    protected float gravityStrength;

    // Collision state
    protected boolean onGround;
    protected boolean onCeiling;
    protected boolean onWall;

    // Store old position for collision recovery
    protected float oldX;
    protected float oldY;

    // Physics constants
    protected static final float DEFAULT_GRAVITY = 0.5f;
    protected static final float MAX_VELOCITY = 20f;
    protected static final float DEFAULT_FRICTION = 0.85f;

    public PhysicalEntity() {
        this(0, 0);
    }

    public PhysicalEntity(float x, float y) {
        super(x, y);

        this.velocity = Vector2.ZERO;
        this.acceleration = Vector2.ZERO;
        this.mass = 1.0f;
        this.friction = DEFAULT_FRICTION;
        this.useGravity = true;
        this.gravityStrength = DEFAULT_GRAVITY;

        this.onGround = false;
        this.onCeiling = false;
        this.onWall = false;
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        System.out.println("=== PHYSICS UPDATE ===");
        System.out.println("Start position: (" + getX() + ", " + getY() + ")");
        System.out.println("Velocity: (" + velocity.x + ", " + velocity.y + ")");
        System.out.println("OnGround: " + onGround);

        // Save old position for collision recovery
        oldX = getX();
        oldY = getY();

        // Apply gravity (downward force - negative Y for bottom-left origin)
        if (useGravity) {
            velocity = new Vector2(velocity.x, velocity.y - gravityStrength);
            System.out.println("After gravity: velocity.y = " + velocity.y);
        }

        // Apply acceleration to velocity
        velocity = velocity.add(acceleration);

        // Clamp velocity
        float speed = velocity.magnitude();
        if (speed > MAX_VELOCITY) {
            velocity = velocity.normalized().multiply(MAX_VELOCITY);
        }

        // Reset collision states
        onGround = false;
        onCeiling = false;
        onWall = false;

        // Try horizontal movement first
        float moveX = velocity.x;
        if (Math.abs(moveX) > 0.001f) {
            System.out.println("Moving horizontally: " + moveX);
            setPosition(oldX + moveX, oldY);

            // Check for horizontal collisions - will be handled by CollisionSystem
        }

        // Try vertical movement
        float moveY = velocity.y;
        if (Math.abs(moveY) > 0.001f) {
            System.out.println("Moving vertically: " + moveY);
            setPosition(getX(), oldY + moveY);

            // Check for vertical collisions - will be handled by CollisionSystem
        }

        // Apply friction when on ground
        if (onGround) {
            velocity = new Vector2(velocity.x * friction, velocity.y);
            System.out.println("On ground, friction applied: velocity.x = " + velocity.x);
        }

        // Reset acceleration
        acceleration = Vector2.ZERO;

        System.out.println("End position: (" + getX() + ", " + getY() + ")");
        System.out.println("End velocity: (" + velocity.x + ", " + velocity.y + ")");
        System.out.println("End onGround: " + onGround);
        System.out.println("======================");

        // Update components
        super.update(deltaTime);
    }

    /**
     * Apply a force to this entity
     */
    public void applyForce(Vector2 force) {
        Vector2 accel = force.divide(mass);
        acceleration = acceleration.add(accel);
    }

    public void applyForce(float fx, float fy) {
        applyForce(new Vector2(fx, fy));
    }

    /**
     * Jump method for subclasses to use
     */
    protected void jump(float force) {
        if (onGround) {
            velocity = new Vector2(velocity.x, force);
            onGround = false;
            System.out.println("JUMP! velocity.y=" + force);
        } else {
            System.out.println("Can't jump - not on ground");
        }
    }

    // ========== GETTERS/SETTERS ==========

    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float vx, float vy) {
        this.velocity = new Vector2(vx, vy);
    }

    public void addVelocity(Vector2 deltaVelocity) {
        this.velocity = velocity.add(deltaVelocity);
    }

    public void addVelocity(float dvx, float dvy) {
        addVelocity(new Vector2(dvx, dvy));
    }

    public void stop() {
        this.velocity = Vector2.ZERO;
        this.acceleration = Vector2.ZERO;
    }

    public boolean isMoving() {
        return velocity.magnitudeSquared() > 0.01f;
    }

    public Vector2 getVelocity() {
        return velocity;
    }

    public float getSpeed() {
        return velocity.magnitude();
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = Math.max(0.1f, mass);
    }

    public float getFriction() {
        return friction;
    }

    public void setFriction(float friction) {
        this.friction = Math.max(0f, Math.min(1f, friction));
    }

    public boolean isUsingGravity() {
        return useGravity;
    }

    public void setUseGravity(boolean useGravity) {
        this.useGravity = useGravity;
    }

    public void setGravity(float gravityValue) {
        this.gravityStrength = gravityValue;
    }

    public float getGravity() {
        return gravityStrength;
    }

    // ========== COLLISION STATE GETTERS ==========

    public boolean isOnGround() {
        return onGround;
    }

    public boolean isOnCeiling() {
        return onCeiling;
    }

    public boolean isOnWall() {
        return onWall;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    @Override
    public String toString() {
        return String.format("%s[vel=(%.2f,%.2f), speed=%.2f, ground=%b]",
                super.toString(), velocity.x, velocity.y, getSpeed(), onGround);
    }
}