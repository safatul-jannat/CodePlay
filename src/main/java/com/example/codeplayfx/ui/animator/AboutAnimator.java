package com.example.codeplayfx.ui.animator;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * AboutAnimator - Handles about screen specific animations
 */
public class AboutAnimator extends BackgroundAnimator {

    private final Button backButton;
    private final Button homeButton;
    private final Button mainWindowButton;
    private final Button galleryButton;
    private final Button libraryButton;
    private final TextArea appDescriptionArea;

    public AboutAnimator(
            StackPane rootPane, Pane bgPane, Pane gridPane, Canvas particleCanvas, Pane orbPane,
            Button backButton, Button homeButton, Button mainWindowButton, 
            Button galleryButton, Button libraryButton, TextArea appDescriptionArea) {
        
        super(rootPane, bgPane, gridPane, particleCanvas, orbPane);
        
        this.backButton = backButton;
        this.homeButton = homeButton;
        this.mainWindowButton = mainWindowButton;
        this.galleryButton = galleryButton;
        this.libraryButton = libraryButton;
        this.appDescriptionArea = appDescriptionArea;
    }

    /**
     * Initialize about screen animations
     */
    public void initializeAbout() {
        // Initialize background first
        initializeBackground();
        
        // Load CSS styles
        loadStyles();

        // Setup about content with fade-in
        setupAboutContent();
        
        // Setup transparent buttons with hover effects
        setupTransparentButtons();
        
        // Start entrance animation for all elements
        playEntranceAnimation();
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

    private void setupAboutContent() {
        // Apply CSS class to TextArea
        appDescriptionArea.getStyleClass().add("about-text-area");
        
        // Initially transparent for animation
        appDescriptionArea.setOpacity(0);
    }

    private void setupTransparentButtons() {
        // Apply CSS class to all buttons
        Button[] buttons = {backButton, homeButton, mainWindowButton, galleryButton, libraryButton};

        for (Button btn : buttons) {
            btn.getStyleClass().add("transparent-button");
            btn.setOpacity(1); // Start invisible for animation
        }
    }
    
    private void playEntranceAnimation() {
//        // Fade in buttons sequentially
//        SequentialTransition buttonSequence = new SequentialTransition();
//
//        Button[] buttons = {backButton, homeButton, mainWindowButton, galleryButton, libraryButton};
//
//        for (int i = 0; i < buttons.length; i++) {
//            Button btn = buttons[i];
//            FadeTransition fade = new FadeTransition(Duration.millis(300), btn);
//            fade.setFromValue(0);
//            fade.setToValue(1);
//            fade.setDelay(Duration.millis(i * 100)); // Staggered start
//            buttonSequence.getChildren().add(fade);
//        }
//
        // Fade in content area with slight delay
        FadeTransition contentFade = new FadeTransition(Duration.millis(800), appDescriptionArea);
        contentFade.setFromValue(0);
        contentFade.setToValue(1);
        contentFade.setDelay(Duration.millis(300));
        
        // Play all animations
        ParallelTransition entrance = new ParallelTransition(contentFade);
        entrance.play();
    }
}