package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.util.Logger;

/**
 * Living entities have health, can take damage, and can die.
 */
public abstract class LivingEntity extends PhysicalEntity {

    protected float health;
    protected float maxHealth;
    protected boolean alive;

    public LivingEntity() {
        this(0, 0, 100);
    }

    public LivingEntity(float x, float y) {
        this(x, y, 100);
    }

    public LivingEntity(float x, float y, float maxHealth) {
        super(x, y);
        this.maxHealth = maxHealth;
        this.health = maxHealth;
        this.alive = true;
    }

    @Override
    public void update(float deltaTime) {
        if (!active) return;

        if (alive && health <= 0) {
            die();
        }

        if (alive) {
            super.update(deltaTime);
        }
    }

    public void takeDamage(float amount) {
        if (!alive) return;

        health -= amount;
        health = Math.max(0, health);

        Logger.debug(name + " took " + amount + " damage. Health: " + health);

        onDamage(amount);

        if (health <= 0) {
            die();
        }
    }

    public void heal(float amount) {
        if (!alive) return;

        health += amount;
        health = Math.min(maxHealth, health);

        Logger.debug(name + " healed " + amount + ". Health: " + health);

        onHeal(amount);
    }

    public void setHealth(float health) {
        this.health = Math.max(0, Math.min(maxHealth, health));

        if (this.health <= 0 && alive) {
            die();
        }
    }

    protected void die() {
        if (!alive) return;

        alive = false;
        Logger.info(name + " died");

        onDeath();
    }

    public void revive() {
        alive = true;
        health = maxHealth;
        Logger.info(name + " revived");
    }

    public void revive(float health) {
        alive = true;
        this.health = Math.min(health, maxHealth);
        Logger.info(name + " revived with " + this.health + " health");
    }

    protected void onDamage(float amount) {}

    protected void onHeal(float amount) {}

    protected void onDeath() {
        hide();
    }

    public float getHealth() {
        return health;
    }

    public float getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(float maxHealth) {
        this.maxHealth = maxHealth;
        this.health = Math.min(this.health, maxHealth);
    }

    public boolean isAlive() {
        return alive;
    }

    public boolean isDead() {
        return !alive;
    }

    public float getHealthPercent() {
        return health / maxHealth;
    }

    public boolean isFullHealth() {
        return health >= maxHealth;
    }

    public boolean isLowHealth() {
        return getHealthPercent() < 0.25f;
    }

    @Override
    public String toString() {
        return String.format("%s[health=%.0f/%.0f, alive=%b]",
                super.toString(), health, maxHealth, alive);
    }
}