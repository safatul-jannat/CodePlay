package com.example.codeplayfx.ui.animator;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * LibraryAnimator - Handles library screen specific animations
 */
public class LibraryAnimator extends BackgroundAnimator {

    private final Button backButton;
    private final Button homeButton;
    private final Button mainWindowButton;
    private final Button galleryButton;
    private final Button aboutButton;

    public LibraryAnimator(
            StackPane rootPane, Pane bgPane, Pane gridPane, Canvas particleCanvas, Pane orbPane,
            Button backButton, Button homeButton, Button mainWindowButton,
            Button galleryButton, Button aboutButton) {

        super(rootPane, bgPane, gridPane, particleCanvas, orbPane);

        this.backButton = backButton;
        this.homeButton = homeButton;
        this.mainWindowButton = mainWindowButton;
        this.galleryButton = galleryButton;
        this.aboutButton = aboutButton;
    }

    /**
     * Initialize library screen animations
     */
    public void initializeLibrary() {
        // Initialize background first
        initializeBackground();

        // Load CSS styles
        loadStyles();

        // Setup transparent buttons
        setupTransparentButtons();
    }

    @Override
    public void shutdown() {
        super.shutdown();
    }

    private void loadStyles() {
        String cssPath = "/com/example/codeplayfx/css/screen-styles.css";
        try {
            rootPane.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
        } catch (Exception e) {
            System.err.println("Could not load CSS: " + cssPath);
            e.printStackTrace();
        }
    }

    private void setupTransparentButtons() {
        // Apply CSS class to all buttons
        Button[] buttons = {backButton, homeButton, mainWindowButton, galleryButton, aboutButton};

        for (Button btn : buttons) {
            btn.getStyleClass().add("transparent-button");
        }
    }
}