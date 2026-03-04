package com.example.codeplayfx.ui.controller;

import com.example.codeplayfx.manager.GalleryManager;
import com.example.codeplayfx.model.Project;
import com.example.codeplayfx.ui.animator.GalleryAnimator;
import com.example.codeplayfx.ui.screen.MainWindowScreen;
import com.example.codeplayfx.ui.screen.ScreenManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class GalleryController {

    @FXML private StackPane rootPane;
    @FXML private Pane bgPane;
    @FXML private Pane gridPane;
    @FXML private Canvas particleCanvas;
    @FXML private Pane orbPane;

    @FXML private ListView<Project> projectListView;
    @FXML private TextArea codePreviewArea;
    @FXML private TextField searchField;
    @FXML private Label projectInfoLabel;
    @FXML private Label statusLabel;

    @FXML private Button loadButton;
    @FXML private Button deleteButton;
    @FXML private Button exportButton;
    @FXML private Button importButton;
    @FXML private Button refreshButton;

    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button mainWindowButton;
    @FXML private Button libraryButton;
    @FXML private Button aboutButton;

    private GalleryManager gallery = GalleryManager.getInstance();
    private GalleryAnimator animator;
    private Stage stage;

    @FXML
    public void initialize() {
        System.out.println("GalleryController initializing...");

        // Don't try to get stage here - scene might not be ready

        // Initialize animator with all components (animator doesn't need stage)
        animator = new GalleryAnimator(
                rootPane, bgPane, gridPane, particleCanvas, orbPane,
                backButton, homeButton, mainWindowButton, libraryButton, aboutButton,
                projectListView, codePreviewArea, searchField,
                loadButton, deleteButton, exportButton, importButton, refreshButton,
                projectInfoLabel, statusLabel
        );

        // Initialize gallery animations
        animator.initializeGallery();

        // Setup gallery functionality
        setupGallery();

        // Get stage AFTER scene is attached (use Platform.runLater)
        Platform.runLater(() -> {
            try {
                if (rootPane.getScene() != null) {
                    stage = (Stage) rootPane.getScene().getWindow();
                    System.out.println("✅ Stage obtained: " + stage);
                } else {
                    System.err.println("⚠️ Scene still null in Platform.runLater");
                }
            } catch (Exception e) {
                System.err.println("❌ Error getting stage: " + e.getMessage());
            }
        });

        System.out.println("✅ GalleryController initialized");
    }

    private void setupGallery() {
        // Show projects
        refreshProjects();

        // Custom cell display
        projectListView.setCellFactory(lv -> new ListCell<Project>() {
            @Override
            protected void updateItem(Project project, boolean empty) {
                super.updateItem(project, empty);
                if (empty || project == null) {
                    setText(null);
                } else {
                    setText(project.getName() + " (" + project.getFormattedDate() + ")");
                }
            }
        });

        // Preview on selection
        projectListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, selected) -> {
                    if (selected != null) {
                        codePreviewArea.setText(selected.getCode());
                        projectInfoLabel.setText("📌 " + selected.getName() + " | Modified: " + selected.getFormattedDate());
                        loadButton.setDisable(false);
                        deleteButton.setDisable(false);
                        exportButton.setDisable(false);
                    }
                }
        );

        // Search
        searchField.textProperty().addListener((obs, old, search) -> {
            if (search.isEmpty()) {
                projectListView.setItems(gallery.getProjects());
            } else {
                projectListView.setItems(
                        gallery.getProjects().filtered(p ->
                                p.getName().toLowerCase().contains(search.toLowerCase()))
                );
            }
        });

        // Load button
        loadButton.setOnAction(e -> {
            Project selected = projectListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                ScreenManager.goToMainWindow();

                MainWindowScreen mainScreen = (MainWindowScreen) ScreenManager.getMainWindowScreen();
                if (mainScreen != null) {
                    mainScreen.loadProject(selected);
                }
                System.out.println("Loading project: " + selected.getName());
            }
        });

        // Delete button
        deleteButton.setOnAction(e -> {
            Project selected = projectListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
                confirm.setTitle("Delete Project");
                confirm.setHeaderText("Delete '" + selected.getName() + "'?");
                confirm.setContentText("This cannot be undone!");

                if (confirm.showAndWait().get() == ButtonType.OK) {
                    gallery.deleteProject(selected);
                    refreshProjects();
                    if (animator != null) animator.updateStatus("Deleted: " + selected.getName());
                }
            }
        });

        // Export button - will use stage from Platform.runLater
        exportButton.setOnAction(e -> {
            Project selected = projectListView.getSelectionModel().getSelectedItem();
            if (selected != null) {
                // If stage is not yet available, use a fallback
                if (stage == null) {
                    System.err.println("⚠️ Stage not ready for export");
                    // Try to get it again
                    if (rootPane.getScene() != null) {
                        stage = (Stage) rootPane.getScene().getWindow();
                    } else {
                        return; // Can't export without stage
                    }
                }

                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Export Project");
                fileChooser.setInitialFileName(selected.getName() + ".java");
                fileChooser.getExtensionFilters().add(
                        new FileChooser.ExtensionFilter("Java Files", "*.java")
                );

                File file = fileChooser.showSaveDialog(stage);
                if (file != null) {
                    gallery.exportProject(selected, file);
                    if (animator != null) animator.updateStatus("Exported: " + file.getName());
                }
            }
        });

        // Import button
        importButton.setOnAction(e -> {
            // If stage is not yet available, use a fallback
            if (stage == null) {
                System.err.println("⚠️ Stage not ready for import");
                if (rootPane.getScene() != null) {
                    stage = (Stage) rootPane.getScene().getWindow();
                } else {
                    return; // Can't import without stage
                }
            }

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Import Java File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("Java Files", "*.java")
            );

            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                gallery.importProject(file);
                refreshProjects();
                if (animator != null) animator.updateStatus("Imported: " + file.getName());
            }
        });

        // Refresh button
        refreshButton.setOnAction(e -> refreshProjects());
    }

    // ========== NAVIGATION METHODS ==========
    @FXML
    public void goBack() {
        System.out.println("Gallery: Going back");
        ScreenManager.goBack();
    }

    @FXML
    public void goHome() {
        System.out.println("Gallery: Going home");
        ScreenManager.goToWelcome();
    }

    @FXML
    public void goMainWindow() {
        System.out.println("Gallery: Going to editor");
        ScreenManager.goToMainWindow();
    }

    @FXML
    public void goLibrary() {
        System.out.println("Gallery: Going to library");
        ScreenManager.goToLibrary();
    }

    @FXML
    public void goAbout() {
        System.out.println("Gallery: Going to about");
        ScreenManager.goToAbout();
    }

    private void refreshProjects() {
        projectListView.setItems(gallery.getProjects());
        codePreviewArea.clear();
        projectInfoLabel.setText("Select a project to preview");
        loadButton.setDisable(true);
        deleteButton.setDisable(true);
        exportButton.setDisable(true);
    }

    public void cleanup() {
        if (animator != null) {
            animator.shutdown();
        }
    }
}