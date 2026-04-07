package com.example.codeplayfx.ui.mainwindow.panel;

import com.example.codeplayfx.ui.screen.MainWindowScreen;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.HBox;

public class TopNavigationPanel extends UIPanel {

    private MainWindowScreen mainWindow;
    private ToggleButton createButton;
    private ToggleButton galleryButton;
    private ToggleButton libraryButton;
    private ToggleButton aboutButton;
    private ToggleButton homeButton;
    private Label logoLabel;
    private ToggleGroup toggleGroup;

    public TopNavigationPanel(FXMLLoader loader, MainWindowScreen mainWindow) {
        this.mainWindow = mainWindow;

        createButton = (ToggleButton) loader.getNamespace().get("createButton");
        galleryButton = (ToggleButton) loader.getNamespace().get("galleryButton");
        libraryButton = (ToggleButton) loader.getNamespace().get("libraryButton");
        aboutButton = (ToggleButton) loader.getNamespace().get("aboutButton");
        homeButton = (ToggleButton) loader.getNamespace().get("home");

        HBox topNav = (HBox) loader.getNamespace().get("topNavigation");
        this.logoLabel = (Label) topNav.getChildren().get(0);
        logoLabel.setText("CodePlay"); // Make sure text is set
        logoLabel.getStyleClass().add("logo-label"); // Add style class
        this.root = topNav;
    }

    @Override
    public void initialize() {
        setupToggleGroup();
        setupButtonActions();
        createButton.setSelected(true); // Create is selected by default
    }

    private void setupToggleGroup() {
        toggleGroup = new ToggleGroup();
        createButton.setToggleGroup(toggleGroup);
        galleryButton.setToggleGroup(toggleGroup);
        libraryButton.setToggleGroup(toggleGroup);
        aboutButton.setToggleGroup(toggleGroup);
        homeButton.setToggleGroup(toggleGroup);
    }

    private void setupButtonActions() {

        createButton.setFocusTraversable(false);
        galleryButton.setFocusTraversable(false);
        libraryButton.setFocusTraversable(false);
        aboutButton.setFocusTraversable(false);
        homeButton.setFocusTraversable(false);

        createButton.setOnAction(e -> mainWindow.onCreateTabClicked());
        galleryButton.setOnAction(e -> mainWindow.onGalleryTabClicked());
        libraryButton.setOnAction(e -> mainWindow.onLibraryTabClicked());
        aboutButton.setOnAction(e -> mainWindow.onAboutTabClicked());
        homeButton.setOnAction(e -> mainWindow.onHomeTabClicked());
    }
    public Label getLogoLabel() {
        return logoLabel;
    }
}