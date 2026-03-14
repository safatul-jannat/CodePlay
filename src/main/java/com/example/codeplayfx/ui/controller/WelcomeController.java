package com.example.codeplayfx.ui.controller;

import com.example.codeplayfx.ui.animator.WelcomeAnimator;
import com.example.codeplayfx.ui.screen.ScreenManager;
import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class WelcomeController implements Initializable {

    @FXML
    private StackPane rootPane;

    /* ── Layers ──────────────────────────────────────────────────────────── */
    @FXML
    private Pane bgPane;
    @FXML
    private Pane gridPane;
    @FXML
    private Canvas particleCanvas;
    @FXML
    private Pane orbPane;

    /* ── Content ─────────────────────────────────────────────────────────── */
    @FXML
    private VBox contentBox;
    @FXML
    private Text badgeText;
    @FXML
    private HBox titleBox;
    @FXML
    private Text taglineText;
    @FXML
    private HBox dividerBox;
    @FXML
    private HBox buttonRow;

    /* ── Title letters ───────────────────────────────────────────────────── */
    @FXML
    private Text lC, lO, lD, lE, lP, lL, lA, lY;

    /* ── Button wrappers & cards ─────────────────────────────────────────── */
    @FXML
    private StackPane btnNewWrap, btnGalleryWrap, btnLibraryWrap, btnAboutWrap;
    @FXML
    private Rectangle cardNew, cardGallery, cardLibrary, cardAbout;

    private WelcomeAnimator animator;

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

        // Center content box
        contentBox.prefWidthProperty().bind(rootPane.widthProperty());
        contentBox.prefHeightProperty().bind(rootPane.heightProperty());

        // Create animator with all JavaFX nodes and button action callbacks
        animator = new WelcomeAnimator(
                rootPane,
                bgPane,
                gridPane,
                particleCanvas,
                orbPane,
                contentBox,
                badgeText,
                titleBox,
                taglineText,
                dividerBox,
                buttonRow,
                lC, lO, lD, lE, lP, lL, lA, lY,
                btnNewWrap, btnGalleryWrap, btnLibraryWrap, btnAboutWrap,
                cardNew, cardGallery, cardLibrary, cardAbout,
                () -> openNew(null),   // Wrap in lambda to match Runnable interface
                () -> openGallery(null),
                () -> openLibrary(null),
                () -> openAbout(null)
        );

        // Start animations
        animator.initializeWelcome();
    }

    /**
     * Clean up animations when controller is destroyed
     */
    public void cleanup() {
        if (animator != null) {
            animator.shutdown();
        }
    }

    @FXML
    public void openNew(ActionEvent event) {
        System.out.println("WelcomeController: New button clicked!");
        ScreenManager.createAndShowMainWindow(); // Special method for first time
    }
    @FXML
    public void openGallery(ActionEvent event) {
        System.out.println("Opening Gallery...");
        ScreenManager.goToGallery();
    }

    @FXML
    public void openLibrary(ActionEvent event) {
        System.out.println("Opening Library...");
        ScreenManager.goToLibrary();
    }

    @FXML
    public void openAbout(ActionEvent event) {
        System.out.println("Opening About...");
        ScreenManager.goToAbout();
    }

    private void switchToFXML(ActionEvent event, String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
            System.out.println("Switched to: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("Error switching to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void switchToFXML(StackPane rootPane, String fxmlPath) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource(fxmlPath)
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) rootPane.getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root, 900, 600));
            stage.show();
            System.out.println("Switched to: " + fxmlPath);
        } catch (Exception e) {
            System.err.println("Error switching to " + fxmlPath + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}