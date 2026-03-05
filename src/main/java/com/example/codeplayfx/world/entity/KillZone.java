package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.render.KillZoneRenderer;
import javafx.scene.paint.Color;

/**
 * KillZone - Hazardous area that damages the player on touch.
 */
public class KillZone extends PhysicalEntity {

    private float width;
    private float height;
    private int damageAmount;

    public KillZone(float x, float y, float width, float height, int damage) {
        super(x, y);

        this.width = width;
        this.height = height;
        this.damageAmount = damage;

        // Kill zones don't move
        setUseGravity(false);
        setVelocity(0, 0);

        // Add collider as TRIGGER
        addCollider(width, height);
        getCollider().setTrigger(true);

        // Set renderer
        this.renderer = new KillZoneRenderer();

        // Default appearance
        getRenderComponent().setSize(width, height);
        getRenderComponent().setFillColor(Color.RED);
        getRenderComponent().setStrokeColor(Color.DARKRED);
        getRenderComponent().setStrokeWidth(1);

        this.name = "KillZone_" + id;
    }

    @Override
    public void show() {
        super.show();
        UserGame userGame = UserGame.getCurrentGame();
        if (userGame != null) {
            userGame.addEntity(this);
        }
    }

    public void onTouch(BlockPlayer player) {
        if (player != null && player.isAlive()) {
            player.takeDamage(damageAmount);
            System.out.println("Player took " + damageAmount + " damage from " + name);
        }
    }

    public void setDamage(int damage) {
        this.damageAmount = damage;
    }

    public int getDamage() {
        return damageAmount;
    }

    public void setColor(String colorName) {
        if (colorName.equalsIgnoreCase("RED")) {
            getRenderComponent().setFillColor(Color.RED);
        } else if (colorName.equalsIgnoreCase("ORANGE")) {
            getRenderComponent().setFillColor(Color.ORANGE);
        } else if (colorName.equalsIgnoreCase("SILVER")) {
            getRenderComponent().setFillColor(Color.SILVER);
        } else if (colorName.equalsIgnoreCase("BLACK")) {
            getRenderComponent().setFillColor(Color.BLACK);
        }
    }


    public float getBlockWidth() {
        return width;
    }

    public float getBlockHeight() {
        return height;
    }
}