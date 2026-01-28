package com.example.codeplayfx.engine.core;

import com.example.codeplayfx.engine.render.GridRenderer;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public class GameEngine {
    private Canvas canvas;
    private GraphicsContext gc;
    private GridConfig gridConfig;
    private GridRenderer gridRenderer;
    private AnimationTimer gameLoop;

    private boolean isRunning = false;
    private boolean isPaused = false;

    public GameEngine(GridConfig gridConfig) {
        this.gridConfig = gridConfig;
        this.gridRenderer = new GridRenderer(gridConfig);
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gc = canvas.getGraphicsContext2D();
        setupGameLoop();
    }

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (!isPaused) {
                    render();
                }
            }
        };
    }

    private void render() {
        if (gc == null || canvas == null) return;

        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gc.setFill(javafx.scene.paint.Color.rgb(30, 30, 30));
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        gridRenderer.render(gc, canvas.getWidth(), canvas.getHeight());
    }

    public void start(String code) {
        System.out.println("Game started with code");
        if (!isRunning) {
            gameLoop.start();
            isRunning = true;
        }
    }

    public void pause() {
        isPaused = true;
    }

    public void resume() {
        isPaused = false;
    }

    public void stop() {
        gameLoop.stop();
        isRunning = false;
        isPaused = false;

        if (gc != null && canvas != null) {
            gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
            gc.setFill(javafx.scene.paint.Color.BLACK);
            gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
        }
    }

    public GridRenderer getGridRenderer() {
        return gridRenderer;
    }

    public GridConfig getGridConfig() {
        return gridConfig;
    }
}