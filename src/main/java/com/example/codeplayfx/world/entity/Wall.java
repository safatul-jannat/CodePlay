package com.example.codeplayfx.world.entity;

import javafx.scene.paint.Color;
import com.example.codeplayfx.world.render.GroundRenderer; // reuse same solid renderer

/**
 * Wall - Vertical solid barrier.
 *
 * Identical physics behaviour to Ground (both are StaticEntity),
 * differentiated by default size (thin & tall) and colour.
 *
 * Usage:
 *   Wall leftWall = new Wall(0, 0, 1, 10);
 *   leftWall.onCollision(player, () -> player.stopHorizontal());
 */
public class Wall extends StaticEntity {

    protected Color color;

    // ========== CONSTRUCTORS ==========

    /** 1-wide wall, 1 cell tall — resize with setSize() */
    public Wall(float x, float y) {
        this(x, y, 1.0f, 1.0f);
    }

    /** Wall with custom size */
    public Wall(float x, float y, float width, float height) {
        this(x, y, width, height, Color.SADDLEBROWN);
    }

    /** Wall with custom size and color */
    public Wall(float x, float y, float width, float height, Color color) {
        super(x, y, width, height);
        this.color = color;

        getRenderComponent().setFillColor(color);
        getRenderComponent().setStrokeColor(color.darker());
        getRenderComponent().setStrokeWidth(1.0f);

        setRenderer(new GroundRenderer()); // solid renderer works fine
        show();
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
        return String.format("Wall[%s, size=%.1fx%.1f]", super.toString(), width, height);
    }
}