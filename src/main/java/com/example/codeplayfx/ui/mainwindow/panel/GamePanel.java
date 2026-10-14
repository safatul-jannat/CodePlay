package com.example.codeplayfx.ui.mainwindow.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.world.GameWorld;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.application.Platform;

public class GamePanel extends UIPanel {

    private Canvas gridCanvas;     // Bottom layer for grid
    private Canvas gameCanvas;     // Top layer for game objects
    private MainWindowScreen mainWindow;
    private double gridSize = 40.0;  // CHANGED: Bigger grid (was 20.0)
    private Label coordLabel;
    private GameEngine gameEngine;
    private GameWorld gameWorld;

    public GamePanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        Pane container = (Pane) loader.getNamespace().get("canvasContainer");

        // Get coordinate label
        this.coordLabel = (Label) loader.getNamespace().get("coordLabel");



        // Create TWO canvases in layers
        gridCanvas = new Canvas(800, 600);
        gameCanvas = new Canvas(800, 600);

        gridCanvas.setFocusTraversable(true);

        gameCanvas.setFocusTraversable(true);

        gameCanvas.setOnMouseClicked(e -> gameCanvas.requestFocus());
        gameCanvas.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            System.out.println("GameCanvas got key: " + event.getCode());
            // Don't consume - let it go to scene
            // event.consume(); // DO NOT DO THIS
        });

        // Bind both to container size
        gridCanvas.widthProperty().bind(container.widthProperty());
        gridCanvas.heightProperty().bind(container.heightProperty());
        gameCanvas.widthProperty().bind(container.widthProperty());
        gameCanvas.heightProperty().bind(container.heightProperty());

        // Grid resize listener - updates both grid drawing and coordinate system
        gridCanvas.widthProperty().addListener((obs, oldVal, newVal) -> {
            drawGrid();
            updateCoordinateSystem();
        });
        gridCanvas.heightProperty().addListener((obs, oldVal, newVal) -> {
            drawGrid();
            updateCoordinateSystem();
        });

        // Create GameWorld with coordinate system
        gameWorld = new GameWorld();
        gameWorld.getCoordinateSystem().setGridScale(gridSize);
        gameWorld.getCoordinateSystem().setCanvasSize(800, 600);

        GraphicsContext gc = gameCanvas.getGraphicsContext2D();
        gameEngine = new GameEngine(gameWorld, gc);

        // Setup mouse tracking on game canvas (top layer)
        setupMouseTracking();

        // Add both to container (grid first, then game)
        container.getChildren().addAll(gridCanvas, gameCanvas);
        this.root = container;

        // Draw initial grid
        drawGrid();
        setupFocusHandlers();
    }
    private void setupFocusHandlers() {
        // Make both canvases request focus for gameCanvas
        gameCanvas.setOnMouseClicked(event -> {
            gameCanvas.requestFocus();
            event.consume();
        });

        gridCanvas.setOnMouseClicked(event -> {
            gameCanvas.requestFocus();
            event.consume();
        });

        // Force focus when game starts
        gameCanvas.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                System.out.println("Game canvas focused - keyboard input active");
            } else if (isGameRunning()) {
                // Re-request focus if game is running and we lose focus
                Platform.runLater(() -> gameCanvas.requestFocus());
            }
        });

        // Prevent scene-level shortcuts when game has focus
//        gameCanvas.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
//            if (gameCanvas.isFocused() && mainWindow.getControlPanel().isRunning()) {
//                // Consume space to prevent welcome screen navigation
//                if (event.getCode() == KeyCode.SPACE) {
//                    event.consume();
//                }
//            }
//        });
    }

    private boolean isGameRunning() {
        return mainWindow.getControlPanel() != null &&
                mainWindow.getControlPanel().isRunning();
    }

    @Override
    public void initialize() {
        System.out.println("GamePanel initialized with " + gridSize + "px grid cells");
        System.out.println("Grid dimensions: " +
                gameWorld.getCoordinateSystem().getGridWidth() + "x" +
                gameWorld.getCoordinateSystem().getGridHeight() + " cells");
    }

    private void setupMouseTracking() {
        // Track mouse on game canvas (top layer)
        gameCanvas.setOnMouseMoved(this::handleMouseMoved);
        gameCanvas.setOnMouseDragged(this::handleMouseMoved);

        gameCanvas.setOnMousePressed(e -> {
            // Use coordinate system for conversion
            double worldX = gameWorld.getCoordinateSystem().screenToWorldX(e.getX());
            double worldY = gameWorld.getCoordinateSystem().screenToWorldY(e.getY());

            mainWindow.getConsolePanel().log(
                    String.format("Clicked at grid (%.1f, %.1f)", worldX, worldY)
            );
        });
    }

    private void handleMouseMoved(MouseEvent event) {
        // Use coordinate system for conversion
        double worldX = gameWorld.getCoordinateSystem().screenToWorldX(event.getX());
        double worldY = gameWorld.getCoordinateSystem().screenToWorldY(event.getY());

        if (coordLabel != null) {
            coordLabel.setText(String.format("X: %.1f Y: %.1f", worldX, worldY));
        }
    }

    // Update the drawGrid() method for neon glow effect
    private void drawGrid() {
        GraphicsContext gc = gridCanvas.getGraphicsContext2D();
        double width = gridCanvas.getWidth();
        double height = gridCanvas.getHeight();

        // Clear with dark background
        gc.setFill(Color.rgb(7, 5, 10));
        gc.fillRect(0, 0, width, height);

        // Draw neon grid lines
        gc.setStroke(Color.rgb(137, 96, 247, 0.6));
        gc.setLineWidth(1);

        // Add glow effect
        gc.setEffect(new javafx.scene.effect.Glow(0.5));

        // Horizontal lines with neon effect
        for (double y = 0; y <= height; y += gridSize) {
            gc.strokeLine(0, height - y, width, height - y);
        }

        // Vertical lines
        for (double x = 0; x <= width; x += gridSize) {
            gc.strokeLine(x, 0, x, height);
        }

        // Draw neon origin marker
        gc.setEffect(new javafx.scene.effect.Glow(0.8));
        gc.setFill(Color.rgb(255, 122, 203));
        gc.fillOval(2, height - gridSize + 2, 10, 10);

        gc.setFill(Color.rgb(255, 255, 255));
        gc.fillText("(0,0)", 15, height - 5);
    }

    /**
     * Update coordinate system when canvas size changes
     */
    private void updateCoordinateSystem() {
        if (gameWorld != null) {
            gameWorld.getCoordinateSystem().setCanvasSize(
                    gridCanvas.getWidth(),
                    gridCanvas.getHeight()
            );
        }
    }

    // Public methods
    public void setGridSize(double gridSize) {
        this.gridSize = gridSize;
        if (gameWorld != null) {
            gameWorld.getCoordinateSystem().setGridScale(gridSize);
        }
        drawGrid();
    }

    public double getGridSize() {
        return gridSize;
    }

    public void refreshGrid() {
        drawGrid();
    }

    public void clearGameObjects() {
        gameCanvas.getGraphicsContext2D().clearRect(0, 0,
                gameCanvas.getWidth(), gameCanvas.getHeight());
    }

    public Canvas getGameCanvas() {
        return gameCanvas;
    }

    public Canvas getGridCanvas() {
        return gridCanvas;
    }

    public GameEngine getGameEngine() {
        return gameEngine;
    }

    public GameWorld getGameWorld() {
        return gameWorld;
    }

    public void startGame() {
        if (gameEngine != null) {
            gameEngine.start();
        }
    }

    public void stopGame() {
        if (gameEngine != null) {
            gameEngine.stop();
        }
    }

    /**
     * Request focus for the game canvas
     */
    public void requestGameFocus() {
        if (gameCanvas != null) {
            Platform.runLater(() -> {
                gameCanvas.requestFocus();
                // Prevent focus from leaving game canvas
                gameCanvas.getScene().getRoot().setFocusTraversable(false);
            });
        }
    }

    public void setGameFocusMode(boolean active) {
        if (gameCanvas != null) {
            Platform.runLater(() -> {
                if (active) {
                    gameCanvas.requestFocus();
                    gameCanvas.setFocusTraversable(true);
                }
                // Game canvas focus mode can be toggled
            });
        }
    }
    /**
     * Check if game canvas has focus
     */
    public boolean hasGameFocus() {
        return gameCanvas != null && gameCanvas.isFocused();
    }

    public Pane getCanvasContainer() {
        return (Pane) root;  // root is the canvasContainer
    }

    public Label getCoordLabel() {
        return coordLabel;
    }
}