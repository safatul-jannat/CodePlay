package com.example.codeplayfx.engine.core;

import com.example.codeplayfx.engine.runtime.SimpleGameRunner;
import com.example.codeplayfx.engine.render.GameRenderer;
import com.example.codeplayfx.engine.render.GridRenderer;
import com.example.codeplayfx.world.GameWorld;
import javafx.animation.AnimationTimer;
import javafx.scene.canvas.Canvas;

public class GameEngine {
    private Canvas canvas;
    private GameRenderer gameRenderer;
    private GridRenderer gridRenderer;
    private GameWorld world;
    private GridConfig gridConfig;
    private AnimationTimer gameLoop;

    private boolean isRunning = false;
    private boolean isPaused = false;
    private String currentCode = "";

    public GameEngine(GridConfig gridConfig) {
        this.gridConfig = gridConfig;
        this.gridRenderer = new GridRenderer(gridConfig);
        this.world = GameWorld.getInstance();
        System.out.println("GameEngine initialized with grid");
    }

    public void setCanvas(Canvas canvas) {
        this.canvas = canvas;
        this.gameRenderer = new GameRenderer(canvas);
        this.gameRenderer.setWorld(world);
        setupGameLoop();
    }

    private void setupGameLoop() {
        gameLoop = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (!isRunning || isPaused) return;

                if (lastUpdate == 0) {
                    lastUpdate = now;
                    return;
                }

                double deltaTime = (now - lastUpdate) / 1_000_000_000.0;
                lastUpdate = now;

                // Update game world
                world.updateAll();

                // Render everything
                render();
            }
        };
    }

    private void render() {
        if (canvas == null) return;

        // Render game
        if (gameRenderer != null) {
            gameRenderer.render();
        }

        // Render grid on top
        if (gridRenderer != null) {
            gridRenderer.render(
                    canvas.getGraphicsContext2D(),
                    canvas.getWidth(),
                    canvas.getHeight()
            );
        }
    }

    public void start(String code) {
        System.out.println("GameEngine.start() called");
        this.currentCode = code;

        if (!isRunning) {
            // Clear previous objects
            world.clear();

            // Compile and run user code
            boolean success = SimpleGameRunner.runUserGame(code, this);

            if (success) {
                // Start game loop
                gameLoop.start();
                isRunning = true;
                isPaused = false;
                System.out.println("Game started successfully");
            } else {
                System.err.println("Failed to start game - compilation error");
            }
        } else {
            // Already running - restart with new code
            stop();
            start(code);
        }
    }

    public void pause() {
        if (isRunning && !isPaused) {
            isPaused = true;
            System.out.println("Game paused");
        }
    }

    public void resume() {
        if (isRunning && isPaused) {
            isPaused = false;
            System.out.println("Game resumed");
        }
    }

    public void stop() {
        if (isRunning) {
            gameLoop.stop();
            world.clear();
            isRunning = false;
            isPaused = false;
            System.out.println("Game stopped");
        }
    }

    // Getters
    public boolean isRunning() { return isRunning; }
    public boolean isPaused() { return isPaused; }
    public GameWorld getWorld() { return world; }
    public GameRenderer getGameRenderer() { return gameRenderer; }
    public GridRenderer getGridRenderer() { return gridRenderer; }
    public GridConfig getGridConfig() { return gridConfig; }
}