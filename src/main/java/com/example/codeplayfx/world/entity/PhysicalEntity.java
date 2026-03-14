package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.world.physics.Vector2;
import com.example.codeplayfx.scripting.runtime.UserGame;

public abstract class PhysicalEntity extends Entity {

    protected Vector2 velocity;
    protected Vector2 acceleration;
    protected float mass;
    protected float friction;
    protected boolean useGravity;
    protected float gravityStrength;

    protected boolean onGround;
    protected boolean onCeiling;
    protected boolean onWall;

    protected float oldX;
    protected float oldY;

    protected static final float DEFAULT_GRAVITY = 0.5f;
    protected static final float MAX_VELOCITY = 20f;
    protected static final float DEFAULT_FRICTION = 0.85f;

    public PhysicalEntity() { this(0, 0); }

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

        oldX = getX();
        oldY = getY();

        // Only apply gravity when NOT on ground (stops Y vibration)
        if (useGravity && !onGround) {
            velocity = new Vector2(velocity.x, velocity.y - gravityStrength);
        }

        velocity = velocity.add(acceleration);

        float speed = velocity.magnitude();
        if (speed > MAX_VELOCITY) {
            velocity = velocity.normalized().multiply(MAX_VELOCITY);
        }

        onGround = false;
        onCeiling = false;
        onWall = false;

        // Horizontal movement
        float moveX = velocity.x;
        if (Math.abs(moveX) > 0.001f) {
            setPosition(oldX + moveX, oldY);

            if (checkCollisionWithSolid()) {
                float overlapY = getVerticalOverlapWithAnySolid();
                if (overlapY > 0.05f) {
                    setPosition(oldX, oldY);
                    velocity = new Vector2(0, velocity.y);
                    onWall = true;
                }
            }
        }

        // Vertical movement with sub-stepping
        float moveY = velocity.y;
        if (Math.abs(moveY) > 0.001f) {
            int steps = Math.max(1, (int)(Math.abs(moveY) / (getHeight() * 0.5f)));
            float stepY = moveY / steps;

            for (int i = 0; i < steps; i++) {
                float prevY = getY();
                setPosition(getX(), getY() + stepY);

                if (checkCollisionWithSolid()) {
                    if (moveY < 0) {
                        float groundTop = findHighestSolidBelow();
                        if (groundTop > -Float.MAX_VALUE) {
                            setPosition(getX(), groundTop);
                        } else {
                            setPosition(getX(), prevY);
                        }
                        onGround = true;
                        velocity = new Vector2(velocity.x, 0);
                    } else {
                        setPosition(getX(), prevY);
                        onCeiling = true;
                        velocity = new Vector2(velocity.x, 0);
                    }
                    break;
                }
            }
        }

        if (onGround) {
            velocity = new Vector2(velocity.x * friction, velocity.y);
        }

        acceleration = Vector2.ZERO;
        super.update(deltaTime);
    }

    // THE KEY FIX: instanceof StaticEntity instead of instanceof Ground
    private boolean checkCollisionWithSolid() {
        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame == null || currentGame.getWorld() == null) return false;

        for (Entity entity : currentGame.getWorld().getAllEntities()) {
            if (!(entity instanceof StaticEntity)) continue;
            if (!entity.isActive()) continue;
            StaticEntity se = (StaticEntity) entity;
            if (se.hasCollider() && se.getCollider().isTrigger()) continue;
            if (this.isColliding(se)) return true;
        }
        return false;
    }

    private float getVerticalOverlapWithAnySolid() {
        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame == null || currentGame.getWorld() == null) return 0f;

        float maxOverlap = 0f;
        for (Entity entity : currentGame.getWorld().getAllEntities()) {
            if (!(entity instanceof StaticEntity)) continue;
            if (entity == this || !entity.isActive()) continue;
            StaticEntity se = (StaticEntity) entity;
            if (se.hasCollider() && se.getCollider().isTrigger()) continue;
            if (this.isColliding(se)) {
                float overlap = Math.min(getY() + getHeight(), se.getY() + se.getHeight())
                        - Math.max(getY(), se.getY());
                if (overlap > maxOverlap) maxOverlap = overlap;
            }
        }
        return maxOverlap;
    }

    private float findHighestSolidBelow() {
        float highest = -Float.MAX_VALUE;

        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame == null || currentGame.getWorld() == null) return highest;

        for (Entity entity : currentGame.getWorld().getAllEntities()) {
            if (!(entity instanceof StaticEntity)) continue;
            if (entity == this || !entity.isActive()) continue;

            StaticEntity se = (StaticEntity) entity;

            boolean xOverlap = (getX() + getWidth() > se.getX()) &&
                    (getX() < se.getX() + se.getWidth());
            if (!xOverlap) continue;

            float top = se.getY() + se.getHeight();
            if (top <= oldY + getHeight() && top > highest) {
                highest = top;
            }
        }
        return highest;
    }

    public void applyForce(Vector2 force) {
        acceleration = acceleration.add(force.divide(mass));
    }

    public void applyForce(float fx, float fy) { applyForce(new Vector2(fx, fy)); }

    protected void jump(float force) {
        if (onGround) {
            velocity = new Vector2(velocity.x, force);
            onGround = false;
        }
    }

    public void setVelocity(Vector2 velocity)     { this.velocity = velocity; }
    public void setVelocity(float vx, float vy)   { this.velocity = new Vector2(vx, vy); }
    public void addVelocity(Vector2 dv)           { this.velocity = velocity.add(dv); }
    public void addVelocity(float dvx, float dvy) { addVelocity(new Vector2(dvx, dvy)); }
    public void stop()                            { this.velocity = Vector2.ZERO; this.acceleration = Vector2.ZERO; }
    public boolean isMoving()                     { return velocity.magnitudeSquared() > 0.01f; }
    public Vector2 getVelocity()                  { return velocity; }
    public float getSpeed()                       { return velocity.magnitude(); }
    public float getMass()                        { return mass; }
    public void setMass(float mass)               { this.mass = Math.max(0.1f, mass); }
    public float getFriction()                    { return friction; }
    public void setFriction(float f)              { this.friction = Math.max(0f, Math.min(1f, f)); }
    public boolean isUsingGravity()               { return useGravity; }
    public void setUseGravity(boolean b)          { this.useGravity = b; }
    public void setGravity(float g)               { this.gravityStrength = g; }
    public float getGravity()                     { return gravityStrength; }
    public boolean isOnGround()                   { return onGround; }
    public boolean isOnCeiling()                  { return onCeiling; }
    public boolean isOnWall()                     { return onWall; }
    public void setOnGround(boolean b)            { this.onGround = b; }

    @Override
    public String toString() {
        return String.format("%s[vel=(%.2f,%.2f), ground=%b]",
                super.toString(), velocity.x, velocity.y, onGround);
    }
}