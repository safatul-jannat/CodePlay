package com.example.codeplayfx.world.component;

/**
 * Base interface for all entity components.
 * Components add specific features to entities (rendering, collision, etc.)
 * following the Composition design pattern.
 */
public interface Component {

    /**
     * Update the component (called every frame)
     * @param deltaTime time since last frame in seconds
     */
    void update(float deltaTime);

    /**
     * Check if this component is currently active
     */
    boolean isActive();

    /**
     * Enable or disable this component
     */
    void setActive(boolean active);
}
