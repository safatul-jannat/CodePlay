package com.example.codeplayfx.ui.controller;

import com.example.codeplayfx.ui.animator.AboutAnimator;
import com.example.codeplayfx.ui.screen.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.control.Hyperlink;
import java.awt.Desktop;
import java.net.URI;
import java.net.URL;
import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class AboutController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private Pane bgPane;
    @FXML private Pane gridPane;
    @FXML private Canvas particleCanvas;
    @FXML private Pane orbPane;
    @FXML private BorderPane contentBorderPane;

    // Navigation buttons
    @FXML private Button backButton;
    @FXML private Button homeButton;
    @FXML private Button mainWindowButton;
    @FXML private Button galleryButton;
    @FXML private Button libraryButton;

    // About content
    @FXML private TextArea appDescriptionArea;
    @FXML private Hyperlink githubLink;
    @FXML private Hyperlink twitterLink;
    @FXML private Hyperlink discordLink;
    private AboutAnimator animator;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind all background panes to root size
        bgPane.prefWidthProperty().bind(rootPane.widthProperty());
        bgPane.prefHeightProperty().bind(rootPane.heightProperty());
        gridPane.prefWidthProperty().bind(rootPane.widthProperty());
        gridPane.prefHeightProperty().bind(rootPane.heightProperty());
        orbPane.prefWidthProperty().bind(rootPane.widthProperty());
        orbPane.prefHeightProperty().bind(rootPane.heightProperty());
        particleCanvas.widthProperty().bind(rootPane.widthProperty());
        particleCanvas.heightProperty().bind(rootPane.heightProperty());

        // Create animator
        animator = new AboutAnimator(
                rootPane, bgPane, gridPane, particleCanvas, orbPane,
                backButton, homeButton, mainWindowButton, galleryButton, libraryButton,
                appDescriptionArea
        );

        animator.initializeAbout();
        loadAboutContent();
        setupHyperlinks();
    }

    private void loadAboutContent() {
        // Load detailed about text from file if exists, otherwise use defaults
        try {
            InputStream is = getClass().getResourceAsStream("/com/example/codeplayfx/files/about.txt");
            if (is != null) {
                String content = new String(is.readAllBytes());
                appDescriptionArea.setText(content);
            } else {
                // Default content already set in FXML
                System.out.println("No about.txt found, using default content");
            }
        } catch (Exception e) {
            System.err.println("Error loading about content: " + e.getMessage());
        }
    }

    public void cleanup() {
        if (animator != null) animator.shutdown();
    }

    private void setupHyperlinks() {
        if (githubLink != null) {
            githubLink.setOnAction(e -> {
                try { Desktop.getDesktop().browse(new URI("https://github.com/sha-fa-at/CodePlay")); }
                catch (Exception ex) { ex.printStackTrace(); }
            });
        }
        if (twitterLink != null) {
            twitterLink.setOnAction(e -> {
                try { Desktop.getDesktop().browse(new URI("https://twitter.com/codeplay")); }
                catch (Exception ex) { ex.printStackTrace(); }
            });
        }
        if (discordLink != null) {
            discordLink.setOnAction(e -> {
                try { Desktop.getDesktop().browse(new URI("https://discord.gg/codeplay")); }
                catch (Exception ex) { ex.printStackTrace(); }
            });
        }
    }
    // Navigation methods
    @FXML
    public void goBack(ActionEvent event) {
        System.out.println("About: Back button clicked");
        ScreenManager.goBack();
    }

    @FXML
    public void goHome(ActionEvent event) {
        System.out.println("About: Home button clicked");
        ScreenManager.goToWelcome();
    }

    @FXML
    public void goMainWindow(ActionEvent event) {
        System.out.println("About: Main Window button clicked");
        ScreenManager.goToMainWindow();
    }

    @FXML
    public void goGallery(ActionEvent event) {
        System.out.println("About: Gallery button clicked");
        ScreenManager.goToGallery();
    }

    @FXML
    public void goLibrary(ActionEvent event) {
        System.out.println("About: Library button clicked");
        ScreenManager.goToLibrary();
    }
}