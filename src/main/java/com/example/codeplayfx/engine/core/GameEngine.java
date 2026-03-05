package com.example.codeplayfx.engine.core;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.Scene;
import com.example.codeplayfx.util.Logger;
import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.engine.system.SystemManager;
import com.example.codeplayfx.scripting.runtime.ScriptRuntime;
import com.example.codeplayfx.scripting.runtime.UserGame;

/**
 * GameEngine - The heart of CodePlay.
 *
 * Manages the game loop that:
 * 1. Updates all game entities
 * 2. Renders them to the screen
 * 3. Maintains consistent frame rate
 *
 * The user never sees this - they just write game logic.
 * The engine handles everything internally.
 */
public class GameEngine {

    // Core components
    private GameWorld gameWorld;
    private GraphicsContext graphicsContext;
    private EngineConfig config;
    private Time time;
    private SystemManager systemManager;
    private ScriptRuntime scriptRuntime;  // User game runtime

    // Engine state
    private EngineState state;
    private AnimationTimer gameLoop;

    /**
     * Create game engine with a game world and graphics context
     */
    public GameEngine(GameWorld gameWorld, GraphicsContext graphicsContext) {
        this.gameWorld = gameWorld;
        this.graphicsContext = graphicsContext;
        this.config = new EngineConfig();
        this.time = new Time();
        this.state = EngineState.UNINITIALIZED;

        initialize();
    }

    /**
     * Initialize the engine
     */
    private void initialize() {
        state = EngineState.INITIALIZING;
        Logger.info("Initializing GameEngine...");

        // Initialize system manager
        this.systemManager = new SystemManager(gameWorld);

        // Create the game loop using JavaFX AnimationTimer
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

    /**
     * Main game loop tick (called every frame)
     */
    private void tick() {
        // Update time
        time.update();
        float deltaTime = time.getDeltaTime();

        systemManager.getInputSystem().update(deltaTime);

        // Update game world (entities)
        gameWorld.update(deltaTime);

        // Execute user game logic
        if (scriptRuntime != null) {
            scriptRuntime.update();
        }
        gameWorld.update(deltaTime);

        // Render
        render();
    }

    /**
     * Render all visible entities
     */
    private void render() {
        if (graphicsContext == null) return;

        // Clear canvas
        double width = graphicsContext.getCanvas().getWidth();
        double height = graphicsContext.getCanvas().getHeight();
        graphicsContext.clearRect(0, 0, width, height);

        // Render all visible entities using their renderers
        for (Entity entity : gameWorld.getVisibleEntities()) {
            if (entity.getRenderer() != null) {
                // Use entity's custom renderer
                entity.getRenderer().render(entity, graphicsContext, gameWorld.getCoordinateSystem());
            } else {
                // Fallback to simple rendering
                renderEntityFallback(entity);
            }
        }

        // Show FPS if enabled
        if (config.isShowFPS()) {
            renderFPS();
        }
    }

    /**
     * Fallback rendering for entities without custom renderer
     */
    private void renderEntityFallback(Entity entity) {
        var screenPos = entity.getScreenPosition();
        double gridScale = gameWorld.getCoordinateSystem().getGridScale();

        // Simple gray square
        graphicsContext.setFill(javafx.scene.paint.Color.GRAY);
        graphicsContext.fillRect(screenPos.x, screenPos.y, gridScale, gridScale);

        graphicsContext.setStroke(javafx.scene.paint.Color.DARKGRAY);
        graphicsContext.setLineWidth(1);
        graphicsContext.strokeRect(screenPos.x, screenPos.y, gridScale, gridScale);
    }

    /**
     * Render FPS counter
     */
    private void renderFPS() {
        graphicsContext.setFill(javafx.scene.paint.Color.YELLOW);
        graphicsContext.fillText("FPS: " + time.getFPS(), 10, 20);
    }

    // ========== ENGINE CONTROL ==========

    /**
     * Start the game engine
     */
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

    /**
     * Pause the game engine
     */
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

    /**
     * Resume the game engine
     */
    public void resume() {
        if (state != EngineState.PAUSED) {
            Logger.warning("Cannot resume engine in state: " + state);
            return;
        }

        Logger.info("Resuming GameEngine...");
        state = EngineState.RUNNING;
        gameWorld.resume();
        time.update(); // Reset delta time to avoid jump
        Logger.info("GameEngine resumed");
    }

    /**
     * Stop the game engine
     */
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

    /**
     * Reset the game (clear world and restart)
     */
    public void reset() {
        Logger.info("Resetting GameEngine...");
        boolean wasRunning = state == EngineState.RUNNING;

        if (state == EngineState.RUNNING) {
            stop();
        }

        gameWorld.clear();
        time.reset();

        if (wasRunning) {
            start();
        }

        Logger.info("GameEngine reset");
    }

    // ========== GETTERS/SETTERS ==========

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public EngineConfig getConfig() {
        return config;
    }

    public Time getTime() {
        return time;
    }

    public EngineState getState() {
        return state;
    }

    public boolean isRunning() {
        return state == EngineState.RUNNING;
    }

    public boolean isPaused() {
        return state == EngineState.PAUSED;
    }

    public void setGraphicsContext(GraphicsContext gc) {
        this.graphicsContext = gc;
    }

    /**
     * Attach input system to JavaFX scene
     */
    public void attachInputToScene(Scene scene) {
        systemManager.getInputSystem().attachToScene(scene);
    }

    /**
     * Load and run a user game
     */
    public void loadUserGame(UserGame userGame) {
        Logger.info("Loading user game: " + userGame.getClass().getSimpleName());
        this.scriptRuntime = new ScriptRuntime(userGame, this);
        this.scriptRuntime.initialize();
    }

    /**
     * Get script runtime
     */
    public ScriptRuntime getScriptRuntime() {
        return scriptRuntime;
    }

    /**
     * Get system manager
     */
    public SystemManager getSystemManager() {
        return systemManager;
    }

    /**
     * Update canvas size (called when window resizes)
     */
    public void updateCanvasSize(double width, double height) {
        gameWorld.updateCanvasSize(width, height);

        // Update physics bounds to match canvas
        int gridWidth = gameWorld.getCoordinateSystem().getGridWidth();
        int gridHeight = gameWorld.getCoordinateSystem().getGridHeight();
        systemManager.getPhysicsSystem().setWorldBoundsFromGrid(gridWidth, gridHeight);
    }

    @Override
    public String toString() {
        return String.format("GameEngine[state=%s, entities=%d, fps=%d]",
                state, gameWorld.getEntityCount(), time.getFPS());
    }
}