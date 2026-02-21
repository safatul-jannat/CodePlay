package com.example.codeplayfx.world.physics;

/**
 * Manages coordinate system conversion between:
 * - User/World space: Bottom-left origin, grid-based (like math graphs)
 * - Screen space: Top-left origin, pixel-based (like JavaFX canvas)
 *
 * Works with dynamic canvas sizes - grid scale determines cell size.
 * Example: gridScale=50, canvas 800x600 → 16x12 grid cells
 */
public class CoordinateSystem {

    private double gridScale;         // Size of each grid cell in pixels (default 50)
    private double canvasWidth;       // Current canvas width in pixels
    private double canvasHeight;      // Current canvas height in pixels

    /**
     * Create a coordinate system with default grid scale
     */
    public CoordinateSystem() {
        this.gridScale = 50.0;  // Default: 50 pixels per grid cell (bigger than 20)
        this.canvasWidth = 800.0;
        this.canvasHeight = 600.0;
    }

    /**
     * Create a coordinate system with specific grid scale
     * @param gridScale size of each grid cell in pixels
     */
    public CoordinateSystem(double gridScale) {
        this.gridScale = gridScale;
        this.canvasWidth = 800.0;
        this.canvasHeight = 600.0;
    }

    /**
     * Update canvas dimensions (called when canvas is resized)
     */
    public void setCanvasSize(double width, double height) {
        this.canvasWidth = width;
        this.canvasHeight = height;
    }

    /**
     * Set grid scale (size of each cell in pixels)
     */
    public void setGridScale(double gridScale) {
        this.gridScale = gridScale;
    }

    /**
     * Convert user/world grid position to screen pixel position
     * User (0,0) is bottom-left
     * Screen (0,0) is top-left
     */
    public Vector2 toScreen(Vector2 worldPos) {
        double screenX = worldPos.x * gridScale;
        double screenY = canvasHeight - (worldPos.y * gridScale);
        return new Vector2((float) screenX, (float) screenY);
    }

    /**
     * Convert user/world grid coordinates to screen pixel position
     */
    public Vector2 toScreen(double worldX, double worldY) {
        return toScreen(new Vector2((float) worldX, (float) worldY));
    }

    /**
     * Convert screen pixel position to user/world grid position
     */
    public Vector2 toWorld(Vector2 screenPos) {
        double worldX = screenPos.x / gridScale;
        double worldY = (canvasHeight - screenPos.y) / gridScale;
        return new Vector2((float) worldX, (float) worldY);
    }

    /**
     * Convert screen pixel coordinates to user/world grid position
     */
    public Vector2 toWorld(double screenX, double screenY) {
        return toWorld(new Vector2((float) screenX, (float) screenY));
    }

    /**
     * Convert world X to screen X
     */
    public double worldToScreenX(double worldX) {
        return worldX * gridScale;
    }

    /**
     * Convert world Y to screen Y
     */
    public double worldToScreenY(double worldY) {
        return canvasHeight - (worldY * gridScale);
    }

    /**
     * Convert screen X to world X
     */
    public double screenToWorldX(double screenX) {
        return screenX / gridScale;
    }

    /**
     * Convert screen Y to world Y
     */
    public double screenToWorldY(double screenY) {
        return (canvasHeight - screenY) / gridScale;
    }

    /**
     * Get current grid width (number of cells that fit horizontally)
     */
    public int getGridWidth() {
        return (int) Math.ceil(canvasWidth / gridScale);
    }

    /**
     * Get current grid height (number of cells that fit vertically)
     */
    public int getGridHeight() {
        return (int) Math.ceil(canvasHeight / gridScale);
    }

    /**
     * Get grid scale (cell size in pixels)
     */
    public double getGridScale() {
        return gridScale;
    }

    /**
     * Get canvas width in pixels
     */
    public double getCanvasWidth() {
        return canvasWidth;
    }

    /**
     * Get canvas height in pixels
     */
    public double getCanvasHeight() {
        return canvasHeight;
    }

    /**
     * Check if world position is within visible grid bounds
     */
    public boolean isInBounds(Vector2 worldPos) {
        return worldPos.x >= 0 && worldPos.x < getGridWidth() &&
                worldPos.y >= 0 && worldPos.y < getGridHeight();
    }

    /**
     * Check if world coordinates are within visible grid bounds
     */
    public boolean isInBounds(double worldX, double worldY) {
        return worldX >= 0 && worldX < getGridWidth() &&
                worldY >= 0 && worldY < getGridHeight();
    }

    /**
     * Clamp world position to grid bounds
     */
    public Vector2 clampToBounds(Vector2 worldPos) {
        float clampedX = Math.max(0, Math.min(getGridWidth() - 1, worldPos.x));
        float clampedY = Math.max(0, Math.min(getGridHeight() - 1, worldPos.y));
        return new Vector2(clampedX, clampedY);
    }
}
