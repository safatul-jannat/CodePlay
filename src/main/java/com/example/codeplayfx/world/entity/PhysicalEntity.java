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
        boolean wasOnGround = onGround;
        onGround = false;
        onCeiling = false;
        onWall = false;

        // Try horizontal movement first
        float moveX = velocity.x;
        if (Math.abs(moveX) > 0.001f) {
            System.out.println("Moving horizontally: " + moveX);
            setPosition(oldX + moveX, oldY);

            if (checkCollisionWithGround()) {
                System.out.println("HORIZONTAL COLLISION! Reverting X");
                setPosition(oldX, oldY);
                velocity = new Vector2(0, velocity.y);
                onWall = true;
            }
        }

        // Try vertical movement
        float moveY = velocity.y;
        if (Math.abs(moveY) > 0.001f) {
            System.out.println("Moving vertically: " + moveY);
            setPosition(getX(), oldY + moveY);

            if (checkCollisionWithGround()) {
                System.out.println("VERTICAL COLLISION!");

                if (moveY < 0) { // Moving down (falling)
                    // Find the exact position where we JUST touch the ground
                    float groundTop = findGroundTopBelow();
                    if (groundTop > -Float.MAX_VALUE) {
                        // Place player ON TOP of the ground
                        // Player's bottom should be at ground's top
                        setPosition(getX(), groundTop);
                        System.out.println("LANDED ON GROUND! Final position: " + getY());
                    } else {
                        setPosition(getX(), oldY);
                    }

                    onGround = true;
                    velocity = new Vector2(velocity.x, 0);
                }
                else if (moveY > 0) { // Moving up
                    setPosition(getX(), oldY);
                    onCeiling = true;
                    velocity = new Vector2(velocity.x, 0);
                    System.out.println("HIT CEILING!");
                }
            }
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
     * Find the top Y coordinate of the highest ground below this entity
     */
    private float findGroundTopBelow() {
        float highestGroundTop = -Float.MAX_VALUE;

        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame == null || currentGame.world == null) {
            return highestGroundTop;
        }

        for (Entity entity : currentGame.world.getAllEntities()) {
            if (entity instanceof Ground && entity != this && entity.isActive()) {
                Ground ground = (Ground) entity;

                // Check if player's X overlaps with ground's X range
                boolean xOverlap = (getX() + getWidth() > ground.getX()) &&
                        (getX() < ground.getX() + ground.getWidth());

                if (xOverlap) {
                    // Ground top is ground Y + ground height
                    float groundTop = ground.getY() + ground.getHeight();

                    // Check if this ground is below the player's old position
                    if (groundTop < oldY && groundTop > highestGroundTop) {
                        highestGroundTop = groundTop;
                    }
                }
            }
        }

        return highestGroundTop;
    }

    /**
     * Check collision with any ground entity
     */
    private boolean checkCollisionWithGround() {
        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame == null || currentGame.world == null) {
            System.out.println("No game world reference!");
            return false;
        }

        for (Entity entity : currentGame.world.getAllEntities()) {
            if (entity instanceof Ground && entity != this && entity.isActive()) {
                if (this.isColliding(entity)) {
                    return true;
                }
            }
        }

        return false;
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
            velocity = new Vector2(velocity.x, force); // Positive for upward
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