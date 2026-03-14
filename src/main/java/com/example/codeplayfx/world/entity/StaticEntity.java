package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.component.Component;

/**
 * StaticEntity - Base class for all immovable, solid world objects.
 *
 * Hierarchy:
 *   Entity
 *     └── StaticEntity        ← YOU ARE HERE
 *           ├── Ground        (horizontal platform)
 *           ├── Wall          (vertical barrier)
 *           └── Platform      (one-way passable platform)
 *
 * Features:
 * - No physics / no movement (static)
 * - Solid AABB collision by default
 * - Collision callback system: onCollision(entity, handler)
 * - Auto-registers itself to the world on show()
 */
public abstract class StaticEntity extends Entity {

    protected float width;
    protected float height;

    // --- Collision callback ---
    // Stored as a simple functional pair: the target entity + a Runnable action
    private Entity collisionTarget;
    private Runnable collisionHandler;

    // ========== CONSTRUCTOR ==========

    public StaticEntity(float x, float y, float width, float height) {
        super(x, y);
        this.width  = width;
        this.height = height;

        // Every static entity gets a solid collider by default
        addCollider(width, height);
        if (collider != null) {
            collider.setTrigger(false);
            collider.setOffset(0, 0);
        }

        // Size the render component to match
        getRenderComponent().setWidth(width);
        getRenderComponent().setHeight(height);
    }

    // ========== LIFECYCLE ==========

    /**
     * Static entities don't need physics updates —
     * only component updates (e.g. animations) and collision callbacks.
     */
    @Override
    public void update(float deltaTime) {
        if (!active) return;

        // Update components (e.g. animated tiles)
        for (Component component : components.values()) {
            if (component.isActive()) {
                component.update(deltaTime);
            }
        }

        // Fire collision callbacks
        if (collisionTarget != null && collisionHandler != null) {
            if (this.isColliding(collisionTarget)) {
                collisionHandler.run();
            }
        }
    }

    /**
     * Auto-add to world when shown.
     * Subclasses can override to customise, but should call super.show().
     */
    @Override
    public void show() {
        super.show();
        UserGame currentGame = UserGame.getCurrentGame();
        if (currentGame != null) {
            currentGame.addEntity(this);
        }
    }

    // ========== COLLISION CALLBACK API ==========

    /**
     * Register a callback that fires every frame while [other] overlaps this entity.
     *
     * Usage:
     *   g1.onCollision(p, () -> p.stop());
     *
     * Only one callback per static entity at a time (last call wins).
     */
    public void onCollision(Entity other, Runnable handler) {
        this.collisionTarget  = other;
        this.collisionHandler = handler;
    }

    /**
     * Remove the registered collision callback.
     */
    public void clearCollisionHandler() {
        this.collisionTarget  = null;
        this.collisionHandler = null;
    }

    // ========== SIZE API ==========

    @Override
    public float getWidth()  { return width; }

    @Override
    public float getHeight() { return height; }

    public void setSize(float width, float height) {
        this.width  = width;
        this.height = height;
        if (collider != null) {
            collider.setWidth(width);
            collider.setHeight(height);
        }
        if (renderComponent != null) {
            renderComponent.setWidth(width);
            renderComponent.setHeight(height);
        }
    }

    // ========== TRIGGER TOGGLE ==========

    /** Make this entity a trigger zone (entities pass through). */
    public void setAsTrigger() {
        if (collider != null) collider.setTrigger(true);
    }

    /** Make this entity solid again. */
    public void setAsSolid() {
        if (collider != null) collider.setTrigger(false);
    }

    public boolean isSolid() {
        return collider != null && !collider.isTrigger();
    }

    // ========== UTILITY ==========

    public boolean containsPoint(float x, float y) {
        return x >= getX() && x <= getX() + width &&
               y >= getY() && y <= getY() + height;
    }

    @Override
    public float getX() { return transform.getX(); }

    @Override
    public float getY() { return transform.getY(); }
}