package com.example.codeplayfx.world.entity;

import javafx.scene.paint.Color;
import com.example.codeplayfx.world.render.GroundRenderer;

/**
 * Ground - Horizontal platform that physical entities stand on.
 *
 * Extends StaticEntity (which handles collider, show(), onCollision(), etc.)
 *
 * Usage:```````````````````````````````````````````````````````````````````````````````
 *   Ground floor = new Ground(0, 0, 20, 1);
 *   floor.setColor(Color.DARKGRAY);
 *   floor.onCollision(player, () -> player.stop());  // ← works now!
 */
public class Ground extends StaticEntity {

    protected Color color;

    // ========== CONSTRUCTORS ==========

    /** 1×1 ground tile */
    public Ground(float x, float y) {
        this(x, y, 1.0f, 1.0f);
    }

    /** Ground with custom size */
    public Ground(float x, float y, float width, float height) {
        this(x, y, width, height, Color.DARKGRAY);
    }

    /** Ground with custom size and color */
    public Ground(float x, float y, float width, float height, Color color) {
        super(x, y, width, height);   // StaticEntity handles collider + render sizing
        this.color = color;

        // Appearance
        getRenderComponent().setFillColor(color);
        getRenderComponent().setStrokeColor(color.darker());
        getRenderComponent().setStrokeWidth(1.0f);

        setRenderer(new GroundRenderer());
        show();   // Auto-add to world
    }

    // ========== COLOR ==========

    public void setColor(Color color) {
        this.color = color;
        if (renderComponent != null) {
            renderComponent.setFillColor(color);
            renderComponent.setStrokeColor(color.darker());
        }
    }

    public Color getColor() { return color; }

    @Override
    public String toString() {
        return String.format("Ground[%s, size=%.1fx%.1f, color=%s]",
                super.toString(), width, height, color);
    }
}