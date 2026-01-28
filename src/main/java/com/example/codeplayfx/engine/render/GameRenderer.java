package com.example.codeplayfx.engine.render;

import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.base.GameObject;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GameRenderer {
    private Canvas canvas;
    private GraphicsContext gc;
    private GameWorld world;
    private RendererManager rendererManager;

    public GameRenderer(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        this.rendererManager = new RendererManager(gc);
    }

    public void setWorld(GameWorld world) {
        this.world = world;
    }

    public void render() {
        if (gc == null || canvas == null || world == null) return;

        // Clear canvas with dark background
        gc.setFill(Color.rgb(25, 25, 35));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Render all game objects
        for (GameObject obj : world.getObjects()) {
            rendererManager.render(obj);
        }

        // Simple HUD
        renderHUD();
    }

    private void renderHUD() {
        gc.setFill(Color.WHITE);

        // Title
        gc.fillText("Bot vs Zombie Demo", 10, 20);

        // Object count
        int objCount = world.getObjects().size();
        gc.fillText("Objects: " + objCount, canvas.getWidth() - 100, 20);
    }

    public Canvas getCanvas() { return canvas; }
}