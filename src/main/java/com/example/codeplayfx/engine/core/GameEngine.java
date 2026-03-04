package com.example.codeplayfx.engine.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.engine.system.SystemManager;
import com.example.codeplayfx.scripting.runtime.ScriptRuntime;
import com.example.codeplayfx.scripting.runtime.UserGame;

/**
 * GameEngine - The heart of CodePlay.
 */
public class GameEngine {

    // Core components
    private GameWorld gameWorld;
    private GraphicsContext graphicsContext;
    private EngineConfig config;
    private Time time;
    private SystemManager systemManager;
    private ScriptRuntime scriptRuntime;

    // Background image
    private Image backgroundImage = null;

    // Engine state
    private EngineState state;
    private AnimationTimer gameLoop;

    public GameEngine(GameWorld gameWorld, GraphicsContext graphicsContext) {
        this.gameWorld = gameWorld;
        this.graphicsContext = graphicsContext;
        this.config = new EngineConfig();
        this.time = new Time();
        this.state = EngineState.UNINITIALIZED;

        initialize();
    }

    private void initialize() {
        state = EngineState.INITIALIZING;
        Logger.info("Initializing GameEngine...");

        this.systemManager = new SystemManager(gameWorld);

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (state == EngineState.RUNNING) {
                    tick();
                }
            }
        };

        state = EngineState.READY;
        Logger.info("GameEngine initialized: " + config);
    }

    private void tick() {
        time.update();
        float deltaTime = time.getDeltaTime();

        systemManager.update(deltaTime);
        gameWorld.update(deltaTime);

        if (scriptRuntime != null) {
            scriptRuntime.update();
        }

        render();
    }

    private void render() {
        if (graphicsContext == null) return;

        double width  = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();

        // 1. Clear canvas
        graphicsContext.clearRect(0, 0, width, height);

        // 2. Draw background image (stretches to fill canvas, sits behind everything)
        if (backgroundImage != null) {
            graphicsContext.drawImage(backgroundImage, 0, 0, width, height);
        }

        // 3. Draw entities on top
        for (Entity entity : gameWorld.getVisibleEntities()) {
            if (entity.getRenderer() != null) {
                entity.getRenderer().render(entity, graphicsContext, gameWorld.getCoordinateSystem());
            } else {
                renderEntityFallback(entity);
            }
        }

        // 4. FPS overlay (always on top)
        if (config.isShowFPS()) {
            renderFPS();
        }
    }

    private void renderEntityFallback(Entity entity) {
        var screenPos = entity.getScreenPosition();
        double gridScale = gameWorld.getCoordinateSystem().getGridScale();

        graphicsContext.setFill(javafx.scene.paint.Color.GRAY);
        graphicsContext.fillRect(screenPos.x, screenPos.y, gridScale, gridScale);

        graphicsContext.setStroke(javafx.scene.paint.Color.DARKGRAY);
        graphicsContext.setLineWidth(1);
        graphicsContext.strokeRect(screenPos.x, screenPos.y, gridScale, gridScale);
    }

    private void renderFPS() {
        graphicsContext.setFill(javafx.scene.paint.Color.YELLOW);
        graphicsContext.fillText("FPS: " + time.getFPS(), 10, 20);
    }

    // ========== BACKGROUND ==========

    /**
     * Set background image by filename.
     * Looks in /assets/backgrounds/ and /assets/ folders.
     * Called by UserGame.setBackground(filename).
     */
    public void setBackground(String filename) {
        if (filename == null || filename.trim().isEmpty()) {
            this.backgroundImage = null;
            Logger.info("Background cleared");
            return;
        }

        // Try multiple resource paths so users don't need to specify full path
        String[] paths = {
                "/com/example/codeplayfx/resources/" + filename,  // This will work
                "/resources/" + filename,                          // Keep as fallback
                "/" + filename

        };

        for (String path : paths) {
            try {
                var stream = getClass().getResourceAsStream(path);
                if (stream != null) {
                    this.backgroundImage = new Image(stream);
                    Logger.info("Background loaded: " + path);
                    return;
                }
            } catch (Exception e) {
                // try next path
            }
        }

        // Last resort: try as a direct URL/file path
        try {
            this.backgroundImage = new Image(filename);
            Logger.info("Background loaded from path: " + filename);
        } catch (Exception e) {
            Logger.warning("Could not load background image: " + filename);
            this.backgroundImage = null;
        }
    }

    /**
     * Clear the background image.
     */
    public void clearBackground() {
        this.backgroundImage = null;
    }

    // ========== ENGINE CONTROL ==========

    public void start() {
        if (!state.canStart()) {
            Logger.warning("Cannot start engine in state: " + state);
            return;
        }

        Logger.info("Starting GameEngine...");
        state = EngineState.RUNNING;
        time.reset();
        gameWorld.start();
        gameLoop.start();
        Logger.info("GameEngine started");
    }

    public void pause() {
        if (!state.canPause()) {
            Logger.warning("Cannot pause engine in state: " + state);
            return;
        }

        Logger.info("Pausing GameEngine...");
        state = EngineState.PAUSED;
        gameWorld.pause();
        Logger.info("GameEngine paused");
    }

    public void resume() {
        if (state != EngineState.PAUSED) {
            Logger.warning("Cannot resume engine in state: " + state);
            return;
        }

        Logger.info("Resuming GameEngine...");
        state = EngineState.RUNNING;
        gameWorld.resume();
        time.update();
        Logger.info("GameEngine resumed");
    }

    public void stop() {
        if (!state.canStop()) {
            Logger.warning("Cannot stop engine in state: " + state);
            return;
        }

        Logger.info("Stopping GameEngine...");
        state = EngineState.STOPPING;
        gameLoop.stop();
        gameWorld.stop();
        state = EngineState.STOPPED;
        Logger.info("GameEngine stopped");
    }

    public void reset() {
        Logger.info("Resetting GameEngine...");
        boolean wasRunning = state == EngineState.RUNNING;

        if (state == EngineState.RUNNING) {
            stop();
        }

        gameWorld.clear();
        time.reset();
        this.backgroundImage = null; // clear background on reset

        if (wasRunning) {
            start();
        }

        Logger.info("GameEngine reset");
    }

    // ========== GETTERS/SETTERS ==========

    public GameWorld getGameWorld() { return gameWorld; }
    public EngineConfig getConfig() { return config; }
    public Time getTime() { return time; }
    public EngineState getState() { return state; }
    public boolean isRunning() { return state == EngineState.RUNNING; }
    public boolean isPaused() { return state == EngineState.PAUSED; }

    public void setGraphicsContext(GraphicsContext gc) {
        this.graphicsContext = gc;
    }

    public void attachInputToScene(Scene scene) {
        systemManager.getInputSystem().attachToScene(scene);
    }

    public void loadUserGame(UserGame userGame) {
        Logger.info("Loading user game: " + userGame.getClass().getSimpleName());
        this.scriptRuntime = new ScriptRuntime(userGame, this);
        this.scriptRuntime.initialize();
    }

    public ScriptRuntime getScriptRuntime() { return scriptRuntime; }
    public SystemManager getSystemManager() { return systemManager; }

    public void updateCanvasSize(double width, double height) {
        gameWorld.updateCanvasSize(width, height);

        int gridWidth  = gameWorld.getCoordinateSystem().getGridWidth();
        int gridHeight = gameWorld.getCoordinateSystem().getGridHeight();
        systemManager.getPhysicsSystem().setWorldBoundsFromGrid(gridWidth, gridHeight);
    }

    @Override
    public String toString() {
        return String.format("GameEngine[state=%s, entities=%d, fps=%d]",
                state, gameWorld.getEntityCount(), time.getFPS());
    }
}