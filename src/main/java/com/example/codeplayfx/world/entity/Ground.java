package com.example.codeplayfx.world.entity;

import javafx.scene.paint.Color;
import com.example.codeplayfx.world.render.GroundRenderer;

/**
 * Ground - Solid platform that physical entities cannot pass through.
 *
 * Features:
 * - Full collision detection on all edges (left, right, top, bottom)
 * - Blocks physical entities completely (no passing through)
 * - Different visual types: GROUND, WALL, PLATFORM
 * - Can be used for floors, walls, ceilings, and solid obstacles
 *
 * Usage:
 *   Ground floor = new Ground(0, 0, 20, 1, GroundType.GROUND);
 *   floor.setColor(Color.DARKGRAY);
 *   floor.onCollision(player, () -> player.stop());  // ✅ Works with existing system
 *
 *   Ground wall = new Ground(10, 0, 1, 10, GroundType.WALL);
 *   wall.setColor(Color.GRAY);
 */
public class Ground extends StaticEntity {

    protected Color color;
    protected GroundType groundType;

    // Track which sides are currently colliding
    private boolean collidingLeft, collidingRight, collidingTop, collidingBottom;

    /**
     * Type of ground entity with different visual representations
     */
    public enum GroundType {
        GROUND,     // Horizontal ground/platform (darker top edge)
        WALL,       // Vertical wall (darker side edges)
        PLATFORM,   // Platform that can be jumped through from below
        SOLID       // Generic solid block
    }

    // ========== CONSTRUCTORS ==========

    /** 1×1 ground tile */
    public Ground(float x, float y) {
        this(x, y, 1.0f, 1.0f, GroundType.GROUND);
    }

    /** Ground with custom size */
    public Ground(float x, float y, float width, float height) {
        this(x, y, width, height, GroundType.GROUND);
    }

    /** Ground with custom size and type */
    public Ground(float x, float y, float width, float height, GroundType groundType) {
        this(x, y, width, height, groundType, Color.DARKGRAY);
    }

    /** Ground with custom size, type, and color */
    public Ground(float x, float y, float width, float height, GroundType groundType, Color color) {
        super(x, y, width, height);
        this.color = color;
        this.groundType = groundType;

        // Ensure collider is solid (not a trigger)
        if (collider != null) {
            collider.setTrigger(false);
        }

        // Setup appearance based on type
        updateAppearance();

        setRenderer(new GroundRenderer());
        show();   // Auto-add to world
    }

    // ========== APPEARANCE ==========

    private void updateAppearance() {
        if (renderComponent == null) return;

        renderComponent.setFillColor(color);
        renderComponent.setStrokeColor(color.darker());
        renderComponent.setStrokeWidth(1.0f);

        // Add visual cues based on type
        switch (groundType) {
            case GROUND:
                // Add subtle top highlight for ground
                renderComponent.setStrokeColor(color.brighter());
                break;
            case WALL:
                // Side highlighting for walls
                renderComponent.setStrokeColor(color.darker());
                break;
            case PLATFORM:
                // Lighter appearance for passable platforms
                renderComponent.setFillColor(color.brighter());
                renderComponent.setStrokeColor(color);
                break;
            case SOLID:
                // Standard solid appearance
                break;
        }
    }

    // ========== COLLISION RESOLUTION ==========

    /**
     * Resolve collision with a physical entity - called by CollisionSystem
     * @param entity The physical entity colliding with this ground
     * @return true if collision was resolved
     */
    public boolean resolveCollision(PhysicalEntity entity) {
        if (!isActive() || !entity.isActive()) return false;

        // Use adjusted bounds (top-left to bottom-left conversion)
        var myBounds = getAdjustedBounds();
        var entityBounds = entity.getAdjustedBounds();

        if (!myBounds.intersects(entityBounds)) return false;

        resetCollisionFlags();

        // Calculate overlap
        double overlapLeft = entityBounds.getMaxX() - myBounds.getMinX();
        double overlapRight = myBounds.getMaxX() - entityBounds.getMinX();
        double overlapTop = entityBounds.getMaxY() - myBounds.getMinY();
        double overlapBottom = myBounds.getMaxY() - entityBounds.getMinY();

        double minOverlap = Math.min(Math.min(overlapLeft, overlapRight),
                Math.min(overlapTop, overlapBottom));

        if (groundType == GroundType.PLATFORM) {
            if (minOverlap == overlapTop && entity.getVelocity().y >= 0) {
                resolveTopCollision(entity, overlapTop);
                return true;
            }
            return false;
        }

        if (minOverlap == overlapLeft) {
            resolveLeftCollision(entity, overlapLeft);
        } else if (minOverlap == overlapRight) {
            resolveRightCollision(entity, overlapRight);
        } else if (minOverlap == overlapTop) {
            resolveTopCollision(entity, overlapTop);
        } else if (minOverlap == overlapBottom) {
            resolveBottomCollision(entity, overlapBottom);
        }

        return true;
    }

    private void resolveLeftCollision(PhysicalEntity entity, double overlap) {
        entity.move(-(float)overlap, 0);
        entity.setVelocity(0, entity.getVelocity().y);
        collidingLeft = true;

        // Trigger the collision callback (from StaticEntity)
        // This will call the handler registered with onCollision()
        // Note: The callback system in StaticEntity already handles this in update()
    }

    private void resolveRightCollision(PhysicalEntity entity, double overlap) {
        entity.move((float)overlap, 0);
        entity.setVelocity(0, entity.getVelocity().y);
        collidingRight = true;
    }

    private void resolveTopCollision(PhysicalEntity entity, double overlap) {
        entity.move(0, -(float)overlap);
        entity.setVelocity(entity.getVelocity().x, 0);
        collidingTop = true;

        // Mark as on ground for BlockPlayer
        if (entity instanceof BlockPlayer) {
            ((BlockPlayer) entity).setOnGround(true);
        }
    }

    private void resolveBottomCollision(PhysicalEntity entity, double overlap) {
        entity.move(0, (float)overlap);
        entity.setVelocity(entity.getVelocity().x, 0);
        collidingBottom = true;
    }

    private void resetCollisionFlags() {
        collidingLeft = false;
        collidingRight = false;
        collidingTop = false;
        collidingBottom = false;
    }

    // ========== GETTERS ==========

    public GroundType getGroundType() { return groundType; }

    public boolean isCollidingLeft() { return collidingLeft; }
    public boolean isCollidingRight() { return collidingRight; }
    public boolean isCollidingTop() { return collidingTop; }
    public boolean isCollidingBottom() { return collidingBottom; }
    public boolean isCollidingAny() { return collidingLeft || collidingRight || collidingTop || collidingBottom; }

    // ========== SETTERS ==========

    public void setGroundType(GroundType groundType) {
        this.groundType = groundType;
        updateAppearance();
    }

    public void setColor(Color color) {
        this.color = color;
        if (renderComponent != null) {
            renderComponent.setFillColor(color);
            renderComponent.setStrokeColor(color.darker());
        }
    }

    public Color getColor() { return color; }

    // ========== UTILITY ==========

    @Override
    public String toString() {
        return String.format("Ground[%s, type=%s, size=%.1fx%.1f, color=%s]",
                super.toString(), groundType, width, height, color);
    }
}