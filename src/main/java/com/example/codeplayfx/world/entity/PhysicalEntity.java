package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.world.physics.Vector2;

/**
 * Physical entities have velocity, acceleration, and respond to physics.
 * Examples: Players, enemies, moving platforms, projectiles
 * 
 * Adds physics on top of base Entity:
 * - Velocity (movement per frame)
 * - Gravity (constant downward force)
 * - Friction (slowdown over time)
 */
public abstract class PhysicalEntity extends Entity {
    
    // Physics properties
    protected Vector2 velocity;           // Current velocity (grid units per frame)
    protected Vector2 acceleration;       // Forces applied this frame
    protected float mass;                 // Mass (affects physics)
    protected float friction;             // Friction coefficient (0-1)
    protected boolean useGravity;         // Should gravity affect this entity?
    protected float gravityStrength;

    // Physics constants
    protected static final float DEFAULT_GRAVITY = 0.5f;      // Downward acceleration
    protected static final float MAX_VELOCITY = 20f;   // Terminal velocity
    protected static final float DEFAULT_FRICTION = 0.85f;
    
    /**
     * Create physical entity at origin
     */
    public PhysicalEntity() {
        this(0, 0);
    }
    
    /**
     * Create physical entity at position
     */
    public PhysicalEntity(float x, float y) {
        super(x, y);
        
        this.velocity = Vector2.ZERO;
        this.acceleration = Vector2.ZERO;
        this.mass = 1.0f;
        this.friction = DEFAULT_FRICTION;
        this.useGravity = true;
        this.gravityStrength = DEFAULT_GRAVITY;
    }
    
    /**
     * Update physics (called every frame)
     */
    @Override
    public void update(float deltaTime) {
        if (!active) return;
        
        // Apply gravity
        if (useGravity) {
            applyForce(new Vector2(0, gravityStrength));
        }
        
        // Apply acceleration to velocity
        velocity = velocity.add(acceleration);
        
        // Clamp velocity to max speed
        float speed = velocity.magnitude();
        if (speed > MAX_VELOCITY) {
            velocity = velocity.normalized().multiply(MAX_VELOCITY);
        }
        
        // Apply friction
        velocity = velocity.multiply(friction);
        
        // Update position based on velocity
        transform.translate(velocity);
        
        // Reset acceleration for next frame
        acceleration = Vector2.ZERO;
        
        // Update components
        super.update(deltaTime);
    }
    
    /**
     * Apply a force to this entity
     * Force is added to acceleration this frame
     */
    public void applyForce(Vector2 force) {
        // F = ma → a = F/m
        Vector2 accel = force.divide(mass);
        acceleration = acceleration.add(accel);
    }
    
    /**
     * Apply force using x,y components
     */
    public void applyForce(float fx, float fy) {
        applyForce(new Vector2(fx, fy));
    }
    
    /**
     * Set velocity directly
     */
    public void setVelocity(Vector2 velocity) {
        this.velocity = velocity;
    }
    
    /**
     * Set velocity using components
     */
    public void setVelocity(float vx, float vy) {
        this.velocity = new Vector2(vx, vy);
    }
    
    /**
     * Add to current velocity
     */
    public void addVelocity(Vector2 deltaVelocity) {
        this.velocity = velocity.add(deltaVelocity);
    }
    
    public void addVelocity(float dvx, float dvy) {
        addVelocity(new Vector2(dvx, dvy));
    }
    
    /**
     * Stop all movement
     */
    public void stop() {
        this.velocity = Vector2.ZERO;
        this.acceleration = Vector2.ZERO;
    }
    
    /**
     * Check if entity is moving
     */
    public boolean isMoving() {
        return velocity.magnitudeSquared() > 0.01f; // Small threshold for floating point
    }
    
    // ========== GETTERS/SETTERS ==========
    
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
        this.mass = Math.max(0.1f, mass); // Prevent zero/negative mass
    }
    
    public float getFriction() {
        return friction;
    }
    
    public void setFriction(float friction) {
        this.friction = Math.max(0f, Math.min(1f, friction)); // Clamp 0-1
    }
    
    public boolean isUsingGravity() {
        return useGravity;
    }
    
    public void setUseGravity(boolean useGravity) {
        this.useGravity = useGravity;
    }

    //======GRAVITY CONTROL======
    public void setGravity(float gravityValue) {
        this.gravityStrength = gravityValue;
    }
    public float getGravity() {
        return gravityStrength;
    }

    @Override
    public String toString() {
        return String.format("%s[vel=(%.2f,%.2f), speed=%.2f]",
            super.toString(), velocity.x, velocity.y, getSpeed());
    }
}