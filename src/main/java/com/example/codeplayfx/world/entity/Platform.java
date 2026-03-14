package com.example.codeplayfx.world.entity;

import javafx.scene.paint.Color;
import com.example.codeplayfx.world.render.GroundRenderer;

/**
 * Platform - A one-way platform: entities can jump UP through it,
 * but land ON TOP of it when falling down.
 *
 * Implemented as a trigger (isTrigger=true) so PhysicalEntity's
 * checkCollisionWithGround() skips it for lateral/upward passes.
 * The one-way logic is handled in PhysicalEntity.findHighestGroundBelow()
 * which only snaps the player onto a surface when moving downward (moveY < 0).
 *
 * Usage:
 *   Platform ledge = new Platform(5, 4, 4, 0.5f);
 *   ledge.onCollision(player, () -> System.out.println("landed!"));
 */
public class Platform extends StaticEntity {

    protected Color color;
    private boolean oneWay; // true = passable from below

    // ========== CONSTRUCTORS ==========

    public Platform(float x, float y, float width, float height) {
        this(x, y, width, height, Color.PERU, true);
    }

    public Platform(float x, float y, float width, float height, Color color) {
        this(x, y, width, height, color, true);
    }

    public Platform(float x, float y, float width, float height, Color color, boolean oneWay) {
        super(x, y, width, height);
        this.color  = color;
        this.oneWay = oneWay;

        // One-way → trigger so physics passes through laterally / upward
        if (oneWay) {
            setAsTrigger();
        }

        getRenderComponent().setFillColor(color);
        getRenderComponent().setStrokeColor(color.darker());
        getRenderComponent().setStrokeWidth(1.5f);

        setRenderer(new GroundRenderer());
        show();
    }

    // ========== ONE-WAY CONTROL ==========

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
        if (oneWay) setAsTrigger(); else setAsSolid();
    }

    public boolean isOneWay() { return oneWay; }

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
        return String.format("Platform[%s, size=%.1fx%.1f, oneWay=%b]",
                super.toString(), width, height, oneWay);
    }
}