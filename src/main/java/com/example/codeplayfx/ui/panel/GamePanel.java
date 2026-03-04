package com.example.codeplayfx.ui.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import com.example.codeplayfx.engine.core.GameEngine;
import com.example.codeplayfx.world.GameWorld;
import javafx.fxml.FXMLLoader;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

public class GamePanel extends UIPanel {

    private Canvas gridCanvas;     // Bottom layer for grid
    private Canvas gameCanvas;     // Top layer for game objects
    private MainWindowScreen mainWindow;
    private double gridSize = 50.0;  // CHANGED: Bigger grid (was 20.0)
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
        gc.setFill(Color.rgb(26, 27, 47));
        gc.fillRect(0, 0, width, height);

        // Draw neon grid lines
        gc.setStroke(Color.rgb(110, 255, 232, 0.3));
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

    public Pane getCanvasContainer() {
        return (Pane) root;  // root is the canvasContainer
    }

    public Label getCoordLabel() {
        return coordLabel;
    }
}