package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.render.CollectibleRenderer;
import com.example.codeplayfx.world.render.ShapeType;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Collectible extends Entity {

    public enum Type {
        COIN,
        GEM,
        POWERUP
    }

    // ========== NO STATIC STATE ==========
    // Previously static fields caused two-classloader bug:
    // dynamic compiler loaded Collectible in a child classloader,
    // so reset() cleared one copy while spawnSequence() wrote to another.
    // Solution: store sequence in UserGame instance instead.

    private static final Random random = new Random(); // Random is stateless, safe to keep static

    // ========== INSTANCE FIELDS ==========

    private Type type;
    private int pointValue;
    private boolean collected = false;

    // ========== CONSTRUCTORS ==========

    public Collectible(float x, float y, Type type) {
        super(x, y);
        this.type = type;

        switch (type) {
            case COIN:    this.pointValue = 10; break;
            case GEM:     this.pointValue = 50; break;
            case POWERUP: this.pointValue = 0;  break;
            default:      this.pointValue = 10;
        }

        setupAppearance();
        setupCollider();
        this.renderer = new CollectibleRenderer();
    }

    public Collectible(float x, float y) {
        this(x, y, Type.COIN);
    }

    // ========== APPEARANCE ==========

    private void setupAppearance() {
        RenderComponent render = getRenderComponent();
        render.setWidth(0.7f);
        render.setHeight(0.7f);

        switch (type) {
            case COIN:
                render.setFillColor(Color.GOLD);
                render.setStrokeColor(Color.DARKORANGE);
                render.setStrokeWidth(2);
                render.setShapeType(ShapeType.CIRCLE);
                break;
            case GEM:
                render.setFillColor(Color.CYAN);
                render.setStrokeColor(Color.DEEPSKYBLUE);
                render.setStrokeWidth(2);
                render.setShapeType(ShapeType.CIRCLE);
                break;
            case POWERUP:
                render.setFillColor(Color.MAGENTA);
                render.setStrokeColor(Color.PURPLE);
                render.setStrokeWidth(2);
                render.setShapeType(ShapeType.ROUNDED_RECTANGLE);
                break;
        }
    }

    private void setupCollider() {
        addCollider(1.0f, 1.0f);
        getCollider().setTrigger(true);
    }

    // ========== SPAWN METHODS ==========
    // These delegate to UserGame instance which lives in the engine classloader.
    // No static state = no classloader split bug.

    public static void spawnSequence(Collectible... collectibles) {
        if (collectibles == null || collectibles.length == 0) return;
        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            game.addToSequence(collectibles);
        }
    }

    public static void spawnRandom(int count) {
        spawnRandom(count, Type.COIN);
    }

    public static void spawnRandom(int count, Type type) {
        if (count <= 0) return;
        UserGame game = UserGame.getCurrentGame();
        if (game == null) return;

        int gridWidth  = 14;
        int gridHeight = 9;
        if (game.getWorld() != null) {
            gridWidth  = game.getWorld().getCoordinateSystem().getGridWidth()  - 2;
            gridHeight = game.getWorld().getCoordinateSystem().getGridHeight() - 2;
        }

        Collectible[] coins = new Collectible[count];
        for (int i = 0; i < count; i++) {
            float x = 1 + random.nextInt(Math.max(1, gridWidth  - 1));
            float y = 1 + random.nextInt(Math.max(1, gridHeight - 1));
            coins[i] = new Collectible(x, y, type);
        }
        game.addToSequence(coins);
    }

    public static int getScore() {
        UserGame game = UserGame.getCurrentGame();
        return game != null ? game.getCollectibleScore() : 0;
    }

    public static int getRemainingCount() {
        UserGame game = UserGame.getCurrentGame();
        return game != null ? game.getCollectibleRemaining() : 0;
    }

    // ========== LIFECYCLE ==========

    private void showNow() {
        if (visible) return;
        super.show();
        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            game.addEntity(this);
        }
    }

    @Override
    public void show() {
        showNow();
    }

    // ========== COLLECTION ==========

    public final void collect() {
        if (collected) return;
        collected = true;
        hide();
        setActive(false);

        UserGame game = UserGame.getCurrentGame();
        if (game != null) {
            game.onCollectibleCollected(this);
        }
    }

    // ========== CUSTOMIZATION ==========

    public void setColor(Color fill, Color stroke) {
        getRenderComponent().setFillColor(fill);
        getRenderComponent().setStrokeColor(stroke);
    }

    public void setColor(String fillHex) {
        getRenderComponent().setFillColor(Color.web(fillHex));
    }

    public void setSize(float size) {
        getRenderComponent().setWidth(size);
        getRenderComponent().setHeight(size);
        if (hasCollider()) {
            getCollider().setSize(size, size);
        }
    }

    public void setPointValue(int points) {
        this.pointValue = points;
    }

    // ========== GETTERS ==========

    public int getPointValue()   { return pointValue; }
    public Type getType()        { return type; }
    public boolean isCollected() { return collected; }
    @Override
    public boolean isCollectible() { return true; }
}