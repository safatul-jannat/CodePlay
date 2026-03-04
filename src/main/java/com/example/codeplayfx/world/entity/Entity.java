package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.util.IDGenerator;
import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.component.Component;
import com.example.codeplayfx.world.component.TransformComponent;
import com.example.codeplayfx.world.component.ColliderComponent;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;
import com.example.codeplayfx.world.render.Renderer;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all game entities.
 * Provides core functionality: ID, transform, components, lifecycle.
 *
 * Design principles:
 * - Abstraction: Common behavior defined here
 * - Encapsulation: Internal state hidden from users
 * - Composition: Features added via components
 */
public abstract class Entity {

    // Core identity
    protected final long id;
    protected String name;
    protected boolean active;
    protected boolean visible;

    // Transform (every entity has position)
    protected TransformComponent transform;

    // Collider (optional - for collision detection)
    protected ColliderComponent collider;

    // Render component (optional - for custom appearance)
    protected RenderComponent renderComponent;

    // Renderer (handles drawing)
    protected Renderer renderer;

    // Component system
    protected Map<Class<? extends Component>, Component> components;

    // Reference to coordinate system (set by GameWorld)
    protected static CoordinateSystem coordinateSystem;

    /**
     * Create entity at origin
     */
    public Entity() {
        this(0, 0);
    }

    /**
     * Create entity at specific grid position
     * @param x grid X coordinate (bottom-left origin)
     * @param y grid Y coordinate (bottom-left origin)
     */
    public Entity(float x, float y) {
        this.id = IDGenerator.generateID();
        this.name = getClass().getSimpleName() + "_" + id;
        this.active = true;
        this.visible = false; // Hidden until show() is called

        // Initialize transform
        this.transform = new TransformComponent(x, y);

        // Initialize component storage
        this.components = new HashMap<>();
        addComponent(transform);

        Logger.debug("Created " + name + " at (" + x + ", " + y + ")");
    }

    /**
     * Set the coordinate system (called by GameWorld at startup)
     */
    public static void setCoordinateSystem(CoordinateSystem coords) {
        coordinateSystem = coords;
        Logger.info("Coordinate system set for all entities");
    }

    // ========== LIFECYCLE METHODS ==========

    /**
     * Update this entity (called every frame by engine)
     * Override this to add custom behavior
     */
    public void update(float deltaTime) {
        if (!active) return;

        // Update all components
        for (Component component : components.values()) {
            if (component.isActive()) {
                component.update(deltaTime);
            }
        }
    }

    /**
     * Called when entity is destroyed
     * Override to add cleanup logic
     */
    public void onDestroy() {
        Logger.debug("Destroying " + name);
    }

    // ========== USER-FACING METHODS ==========

    /**
     * Make this entity visible and active in the game
     */
    public void show() {
        this.visible = true;
        this.active = true;
        Logger.debug(name + " is now visible");
    }

    /**
     * Hide this entity (still exists, but not rendered)
     */
    public void hide() {
        this.visible = false;
        Logger.debug(name + " is now hidden");
    }

    /**
     * Destroy this entity (removes from game)
     */
    public void destroy() {
        this.active = false;
        this.visible = false;
        onDestroy();
    }

    // ========== POSITION METHODS ==========

    /**
     * Get current X position (grid coordinates)
     */
    public float getX() {
        return transform.getX();
    }

    /**
     * Get current Y position (grid coordinates)
     */
    public float getY() {
        return transform.getY();
    }

    /**
     * Get position as Vector2
     */
    public Vector2 getPosition() {
        return transform.getPosition();
    }

    /**
     * Set position in grid coordinates
     */
    public void setPosition(float x, float y) {
        transform.setPosition(x, y);
    }

    /**
     * Set position using Vector2
     */
    public void setPosition(Vector2 position) {
        transform.setPosition(position);
    }

    /**
     * Move entity by offset
     */
    public void move(float dx, float dy) {
        transform.translate(dx, dy);
    }

    /**
     * Get screen position (for rendering)
     * Converts grid coordinates to pixel coordinates
     */
    public Vector2 getScreenPosition() {
        if (coordinateSystem != null) {
            return coordinateSystem.toScreen(getPosition());
        }
        return getPosition(); // Fallback if no coordinate system
    }

    // ========== COLLISION DETECTION ==========

    /**
     * Add a collider to this entity
     */
    public void addCollider(float width, float height) {
        this.collider = new ColliderComponent(width, height);
        addComponent(collider);
    }

    /**
     * Add a collider with offset
     */
    public void addCollider(float width, float height, Vector2 offset) {
        this.collider = new ColliderComponent(width, height, offset);
        addComponent(collider);
    }

    /**
     * Get collider component
     */
    public ColliderComponent getCollider() {
        return collider;
    }

    /**
     * Check if this entity has a collider
     */
    public boolean hasCollider() {
        return collider != null && collider.isActive();
    }

    /**
     * Check if this entity is colliding with another entity
     * Uses rectangular AABB collision detection
     */
    public boolean isColliding(Entity other) {
        if (other == null || !other.isActive() || !this.isActive()) {
            return false;
        }

        // Both entities must have colliders
        if (!this.hasCollider() || !other.hasCollider()) {
            return false;
        }

        // Use collider components to check intersection
        return this.collider.intersects(
                other.collider,
                this.getPosition(),
                other.getPosition()
        );
    }

    /**
     * Check if a point is inside this entity's collider
     */
    public boolean containsPoint(Vector2 point) {
        if (!hasCollider()) return false;
        return collider.containsPoint(point, getPosition());
    }

    /**
     * Check if a point is inside this entity's collider
     */
    public boolean containsPoint(float x, float y) {
        return containsPoint(new Vector2(x, y));
    }

    // ========== RENDERING ==========

    /**
     * Set custom renderer for this entity
     */
    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    /**
     * Get the renderer for this entity
     */
    public Renderer getRenderer() {
        return renderer;
    }

    /**
     * Add or get render component
     */
    public RenderComponent getRenderComponent() {
        if (renderComponent == null) {
            renderComponent = new RenderComponent();
            addComponent(renderComponent);
        }
        return renderComponent;
    }

    /**
     * Check if entity has a render component
     */
    public boolean hasRenderComponent() {
        return renderComponent != null && renderComponent.isActive();
    }

    // ========== COMPONENT SYSTEM ==========

    /**
     * Add a component to this entity
     */
    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    /**
     * Get a component by type
     */
    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentType) {
        return (T) components.get(componentType);
    }

    /**
     * Check if entity has a specific component
     */
    public boolean hasComponent(Class<? extends Component> componentType) {
        return components.containsKey(componentType);
    }

    /**
     * Remove a component
     */
    public void removeComponent(Class<? extends Component> componentType) {
        components.remove(componentType);
    }

    // ========== GETTERS/SETTERS ==========

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public TransformComponent getTransform() {
        return transform;
    }

    @Override
    public String toString() {
        return String.format("%s[id=%d, pos=(%.1f,%.1f), active=%b, visible=%b]",
                name, id, getX(), getY(), active, visible);
    }
}