package com.example.codeplayfx.world;

import com.example.codeplayfx.util.Logger;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;

/**
 * Background - ONLY stores image data.
 * Rendering is handled by GamePanel/GameWorld.
 */
public class Background {

    private Image image;
    private String currentImagePath;
    private boolean visible = true;
    private Color tint = Color.WHITE;

    // No DEFAULT_BACKGROUND - grid is default, not an image

    public Background() {
        // Empty constructor - grid is default
        Logger.debug("Background object created (grid is default)");
    }

    /**
     * Set the background image
     */
    public void setImage(String imagePath) {
        this.currentImagePath = imagePath;

        try {
            // Try to load from resources
            String resourcePath = "/com/example/codeplayfx/resources/" + imagePath;
            java.net.URL imageUrl = getClass().getResource(resourcePath);

            if (imageUrl == null) {
                // Try alternative path
                imageUrl = getClass().getResource("/resources/" + imagePath);
            }

            if (imageUrl != null) {
                this.image = new Image(imageUrl.toExternalForm());
                Logger.info("Background image loaded: " + imagePath);
            } else {
                Logger.warning("Could not load background image: " + imagePath);
            }

        } catch (Exception e) {
            Logger.error("Failed to load background image: " + imagePath, e);
        }
    }

    /**
     * Get the image
     */
    public Image getImage() {
        return image;
    }

    /**
     * Set color tint
     */
    public void setColorTint(Color tint) {
        this.tint = tint;
    }

    /**
     * Get color tint
     */
    public Color getColorTint() {
        return tint;
    }

    /**
     * Show background
     */
    public void show() {
        this.visible = true;
    }

    /**
     * Hide background
     */
    public void hide() {
        this.visible = false;
    }

    /**
     * Check if visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Clear background data
     */
    public void clear() {
        this.image = null;
        this.currentImagePath = null;
        this.tint = Color.WHITE;
        this.visible = true;
    }
}
