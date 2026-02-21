package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.util.Logger;

/**
 * Living entities have health, can take damage, and can die.
 * Examples: Players, enemies, NPCs
 * 
 * Adds life management on top of PhysicalEntity:
 * - Health system
 * - Damage/healing
 * - Alive/dead state
 */
public abstract class LivingEntity extends PhysicalEntity {
    
    protected float health;
    protected float maxHealth;
    protected boolean alive;
    
    /**
     * Create living entity at origin with default health
     */
    public LivingEntity() {
        this(0, 0, 100);
    }
    
    /**
     * Create living entity at position with default health
     */
    public LivingEntity(float x, float y) {
        this(x, y, 100);
    }
    
    /**
     * Create living entity with specific health
     */
    public LivingEntity(float x, float y, float maxHealth) {
        super(x, y);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.alive = true;
    }
    
    /**
     * Update living entity (checks for death)
     */
    @Override
    public void update(float deltaTime) {
        if (!active) return;
        
        // Check if entity should die
        if (alive && health <= 0) {
            die();
        }
        
        // Only update physics if alive
        if (alive) {
            super.update(deltaTime);
        }
    }
    
    /**
     * Take damage
     * @param amount damage to take
     */
    public void takeDamage(float amount) {
        if (!alive) return;
        
        health -= amount;
        health = Math.max(0, health); // Clamp to 0
        
        Logger.debug(name + " took " + amount + " damage. Health: " + health);
        
        // Trigger damage callback
        onDamage(amount);
        
        // Check for death
        if (health <= 0) {
            die();
        }
    }
    
    /**
     * Heal this entity
     * @param amount health to restore
     */
    public void heal(float amount) {
        if (!alive) return;
        
        health += amount;
        health = Math.min(maxHealth, health); // Clamp to max
        
        Logger.debug(name + " healed " + amount + ". Health: " + health);
        
        onHeal(amount);
    }
    
    /**
     * Set health directly
     */
    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(maxHealth, health));
        
        if (this.health <= 0 && alive) {
            die();
        }
    }
    
    /**
     * Called when entity dies
     * Override to add custom death behavior
     */
    protected void die() {
        if (!alive) return; // Already dead
        
        alive = false;
        Logger.info(name + " died");
        
        onDeath();
    }
    
    /**
     * Revive this entity with full health
     */
    public void revive() {
        alive = true;
        health = maxHealth;
        Logger.info(name + " revived");
    }
    
    /**
     * Revive with specific health amount
     */
    public void revive(float health) {
        alive = true;
        this.health = Math.min(health, maxHealth);
        Logger.info(name + " revived with " + this.health + " health");
    }
    
    // ========== CALLBACKS (Override in subclasses) ==========
    
    /**
     * Called when entity takes damage
     * Override to add effects (sound, particles, etc)
     */
    protected void onDamage(float amount) {
        // Default: do nothing
    }
    
    /**
     * Called when entity is healed
     * Override to add effects
     */
    protected void onHeal(float amount) {
        // Default: do nothing
    }
    
    /**
     * Called when entity dies
     * Override to add death effects
     */
    protected void onDeath() {
        // Default: hide the entity
        hide();
    }
    
    // ========== GETTERS ==========
    
    public float getHealth() {
        return health;
    }
    
    public float getMaxHealth() {
        return maxHealth;
    }
    
    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
        this.health = Math.min(this.health, maxHealth); // Adjust current health if needed
    }
    
    public boolean isAlive() {
        return alive;
    }
    
    public boolean isDead() {
        return !alive;
    }
    
    /**
     * Get health as percentage (0.0 to 1.0)
     */
    public float getHealthPercent() {
        return health / maxHealth;
    }
    
    /**
     * Check if health is full
     */
    public boolean isFullHealth() {
        return health >= maxHealth;
    }
    
    /**
     * Check if health is low (below 25%)
     */
    public boolean isLowHealth() {
        return getHealthPercent() < 0.25f;
    }
    
    @Override
    public String toString() {
        return String.format("%s[health=%.0f/%.0f, alive=%b]",
            super.toString(), health, maxHealth, alive);
    }
}