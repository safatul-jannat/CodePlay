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
import javafx.geometry.Rectangle2D;

import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all game entities.
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
     */
    public Entity(float x, float y) {
        this.id = IDGenerator.generateID();
        this.name = getClass().getSimpleName() + "_" + id;
        this.active = true;
        this.visible = false;

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
     */
    public void update(float deltaTime) {
        if (!active) return;

        for (Component component : components.values()) {
            if (component.isActive()) {
                component.update(deltaTime);
            }
        }
    }

    /**
     * Called when entity is destroyed
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

    public float getX() {
        return transform.getX();
    }

    public float getY() {
        return transform.getY();
    }

    public Vector2 getPosition() {
        return transform.getPosition();
    }

    public void setPosition(float x, float y) {
        transform.setPosition(x, y);
    }

    public void setPosition(Vector2 position) {
        transform.setPosition(position);
    }

    public void move(float dx, float dy) {
        transform.translate(dx, dy);
    }

    public Vector2 getScreenPosition() {
        if (coordinateSystem != null) {
            // For rendering, convert top-left to screen coordinates
            float worldX = getX();
            float worldY = getY();  // This is top-left Y
            float worldBottomY = worldY - getHeight();  // Convert to bottom-left for coordinate system
            Vector2 bottomLeftPos = new Vector2(worldX, worldBottomY);
            return coordinateSystem.toScreen(bottomLeftPos);
        }
        return getPosition();
    }

    // ========== COLLISION DETECTION ==========

    public void addCollider(float width, float height) {
        this.collider = new ColliderComponent(width, height);
        addComponent(collider);
    }

    public void addCollider(float width, float height, Vector2 offset) {
        this.collider = new ColliderComponent(width, height, offset);
        addComponent(collider);
    }

    public ColliderComponent getCollider() {
        return collider;
    }

    public boolean hasCollider() {
        return collider != null && collider.isActive();
    }

    /**
     * Check if this entity is colliding with another entity
     * SINGLE VERSION - REMOVED DUPLICATE
     */
    public boolean isColliding(Entity other) {
        if (other == null || !other.isActive() || !this.isActive()) {
            return false;
        }

        if (!this.hasCollider() || !other.hasCollider()) {
            return false;
        }

        return this.getAdjustedBounds().intersects(other.getAdjustedBounds());

    }

    public boolean containsPoint(Vector2 point) {
        if (!hasCollider()) return false;
        return collider.containsPoint(point, getPosition());
    }

    public boolean containsPoint(float x, float y) {
        return containsPoint(new Vector2(x, y));
    }

    // ========== RENDERING ==========

    public void setRenderer(Renderer renderer) {
        this.renderer = renderer;
    }

    public Renderer getRenderer() {
        return renderer;
    }

    public RenderComponent getRenderComponent() {
        if (renderComponent == null) {
            renderComponent = new RenderComponent();
            addComponent(renderComponent);
        }
        return renderComponent;
    }

    public boolean hasRenderComponent() {
        return renderComponent != null && renderComponent.isActive();
    }

    // ========== COMPONENT SYSTEM ==========

    public <T extends Component> void addComponent(T component) {
        components.put(component.getClass(), component);
    }

    @SuppressWarnings("unchecked")
    public <T extends Component> T getComponent(Class<T> componentType) {
        return (T) components.get(componentType);
    }

    public boolean hasComponent(Class<? extends Component> componentType) {
        return components.containsKey(componentType);
    }

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

    public float getWidth() {
        if (renderComponent != null) {
            return renderComponent.getWidth();
        }
        return 1.0f;
    }

    public float getHeight() {
        if (renderComponent != null) {
            return renderComponent.getHeight();
        }
        return 1.0f;
    }

    /**
     * Get bounds adjusted for collision detection
     * Converts from rendering coordinate (top-left) to collision coordinate (bottom-left)
     */
    public Rectangle2D getAdjustedBounds() {
        float x = getX();
        float y = getY() - getHeight();  // Convert top-left to bottom-left
        return new Rectangle2D(x, y, getWidth(), getHeight());
    }
    @Override
    public String toString() {
        return String.format("%s[id=%d, pos=(%.1f,%.1f), active=%b, visible=%b]",
                name, id, getX(), getY(), active, visible);
    }

    public boolean isCollectible() { return false; }
    public void collect() {} // no-op by default
}