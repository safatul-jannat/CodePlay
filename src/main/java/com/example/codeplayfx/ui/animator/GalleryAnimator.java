package com.example.codeplayfx.ui.animator;

import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;

/**
 * GalleryAnimator - Handles gallery screen specific animations
 * Extends BackgroundAnimator for common background elements
 */
public class GalleryAnimator extends BackgroundAnimator {

    // Navigation buttons
    private final Button backButton;
    private final Button homeButton;
    private final Button mainWindowButton;
    private final Button libraryButton;
    private final Button aboutButton;
    
    // Gallery specific components
    private final ListView<?> projectListView;
    private final TextArea codePreviewArea;
    private final TextField searchField;
    private final Button loadButton;
    private final Button deleteButton;
    private final Button exportButton;
    private final Button importButton;
    private final Button refreshButton;
    private final Label projectInfoLabel;
    private final Label statusLabel;

    public GalleryAnimator(
            StackPane rootPane, Pane bgPane, Pane gridPane, Canvas particleCanvas, Pane orbPane,
            Button backButton, Button homeButton, Button mainWindowButton, 
            Button libraryButton, Button aboutButton,
            ListView<?> projectListView, TextArea codePreviewArea, TextField searchField,
            Button loadButton, Button deleteButton, Button exportButton,
            Button importButton, Button refreshButton, Label projectInfoLabel, Label statusLabel) {
        
        super(rootPane, bgPane, gridPane, particleCanvas, orbPane);
        
        // Navigation buttons
        this.backButton = backButton;
        this.homeButton = homeButton;
        this.mainWindowButton = mainWindowButton;
        this.libraryButton = libraryButton;
        this.aboutButton = aboutButton;
        
        // Gallery components
        this.projectListView = projectListView;
        this.codePreviewArea = codePreviewArea;
        this.searchField = searchField;
        this.loadButton = loadButton;
        this.deleteButton = deleteButton;
        this.exportButton = exportButton;
        this.importButton = importButton;
        this.refreshButton = refreshButton;
        this.projectInfoLabel = projectInfoLabel;
        this.statusLabel = statusLabel;
    }

    /**
     * Initialize gallery screen animations
     */
    public void initializeGallery() {
        // Initialize background first
        initializeBackground();
        
        // Load CSS styles
        loadStyles();

        // Setup gallery components with animations
        setupGalleryComponents();
        setupTransparentButtons();
    }

    @Override
    public void shutdown() {
        super.shutdown();
        // Any gallery-specific cleanup
    }

    private void loadStyles() {
        // Load the CSS file
        String cssPath = "/com/example/codeplayfx/css/screen-styles.css";
        try {
            rootPane.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
            System.out.println("✅ Gallery CSS loaded");
        } catch (Exception e) {
            System.err.println("❌ Could not load CSS: " + cssPath);
        }
    }

    private void setupGalleryComponents() {
        // Apply CSS classes to gallery components
        if (projectListView != null) {
            projectListView.getStyleClass().add("gallery-list");
        }
        
        if (codePreviewArea != null) {
            codePreviewArea.getStyleClass().add("gallery-preview");
        }
        
        if (searchField != null) {
            searchField.getStyleClass().add("gallery-search");
        }
        
        if (projectInfoLabel != null) {
            projectInfoLabel.getStyleClass().add("gallery-info-label");
        }
        
        if (statusLabel != null) {
            statusLabel.getStyleClass().add("gallery-status-label");
        }
        
        // Style action buttons
        if (loadButton != null) {
            loadButton.getStyleClass().add("gallery-primary-button");
        }
        
        if (deleteButton != null) {
            deleteButton.getStyleClass().add("gallery-danger-button");
        }
        
        if (exportButton != null) {
            exportButton.getStyleClass().add("gallery-secondary-button");
        }
        
        if (importButton != null) {
            importButton.getStyleClass().add("gallery-action-button");
        }
        
        if (refreshButton != null) {
            refreshButton.getStyleClass().add("gallery-action-button");
        }
    }

    private void setupTransparentButtons() {
        // Apply transparent button style to navigation buttons
        Button[] navButtons = {backButton, homeButton, mainWindowButton, libraryButton, aboutButton};

        for (Button btn : navButtons) {
            if (btn != null) {
                btn.getStyleClass().add("transparent-button");
            }
        }
    }

    /**
     * Update status label with animation
     */
    public void updateStatus(String message) {
        if (statusLabel != null) {
            statusLabel.setText(message);
            // You could add a fade animation here if desired
        }
    }

    /**
     * Highlight a component briefly (for visual feedback)
     */
    public void highlightComponent(javafx.scene.Node component) {
        if (component == null) return;
        
        String originalStyle = component.getStyle();
        component.setStyle(originalStyle + "; -fx-effect: dropshadow(gaussian, #ff6ec7, 20, 0.8, 0, 0);");
        
        javafx.animation.PauseTransition pause = new javafx.animation.PauseTransition(javafx.util.Duration.millis(300));
        pause.setOnFinished(e -> component.setStyle(originalStyle));
        pause.play();
    }
}