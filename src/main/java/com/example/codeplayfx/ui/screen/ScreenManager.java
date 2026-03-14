package com.example.codeplayfx.ui.screen;

import javafx.scene.Scene;
import javafx.stage.Stage;

import java.util.Stack;

public class ScreenManager {
    private static Stage stage;
    private static Scene currentScene;

    // Navigation stack to preserve back navigation history
    private static final Stack<Screen> historyStack = new Stack<>();


    // Cached screens for state preservation
    private static Screen mainWindowScreen = null;
    private static Screen welcomeScreen = null;
    private static Screen libraryScreen = null;
    private static Screen galleryScreen = null;
    private static Screen aboutScreen = null;

    // Track current screen
    private static Screen currentScreen = null;

    public static void init(Stage primaryStage) {
        stage = primaryStage;
        System.out.println("ScreenManager initialized");
    }

    public static void show(Screen screen) {
        if (stage == null) {
            throw new IllegalStateException("ScreenManager not initialized");
        }

        System.out.println("Showing screen: " + screen.getClass().getSimpleName());

        // Update current screen reference
        currentScreen = screen;

        // Check if this screen already has its own scene
        if (screen.getScene() != null) {
            stage.setScene(screen.getScene());
        }

        else if (screen.getView() != null && screen.getView().getScene() != null) {
            stage.setScene(screen.getView().getScene());
            System.out.println("Using existing scene from view");
        }
        else {
            // Create new scene for screens without one
            Scene scene = new Scene(screen.getView(), 900, 600);
            stage.setScene(scene);
        }

        currentScene = stage.getScene();
        stage.show();
        stage.toFront();

        screen.onShow();
        System.out.println("Screen displayed");
    }

    /**
     * Navigate to a new screen, pushing current to history
     * Call this when moving FORWARD to a new screen
     */
    public static void pushAndShow(Screen screen) {
        // Push current screen to history before showing new one
        if (currentScreen != null) {
            historyStack.push(currentScreen);
            System.out.println("Pushed to history: " + currentScreen.getClass().getSimpleName());
        }
        show(screen);
    }

    /**
     * Go back to previous screen
     */
    public static void goBack() {
        if (!historyStack.isEmpty()) {
            Screen previousScreen = historyStack.pop();
            System.out.println("Going back to: " + previousScreen.getClass().getSimpleName());
            show(previousScreen);
        } else {
            System.out.println("No history, going to Welcome screen");
            goToWelcome();
        }
    }

    /**
     * Go to Welcome screen (first screen)
     */
    public static void goToWelcome() {
        if (welcomeScreen == null) {
            welcomeScreen = new WelcomeScreen(stage);
        }

        // Clear history when going to Welcome (start fresh)
        historyStack.clear();
        show(welcomeScreen);
    }

    /**
     * Go to MainWindow screen
     */
    public static void goToMainWindow() {
        if (mainWindowScreen == null) {
            mainWindowScreen = new MainWindowScreen(stage);
            // First time creation, push current to history
            if (currentScreen != null) {
                historyStack.push(currentScreen);
            }
            show(mainWindowScreen);
        } else {
            // Reusing existing MainWindow - this is a RETURN, not a forward navigation
            // So we DON'T push to history, just show it
            show(mainWindowScreen);
        }
    }

    /**
     * Special method for first-time MainWindow creation from Welcome
     */
    public static void createAndShowMainWindow() {
        if (mainWindowScreen == null) {
            mainWindowScreen = new MainWindowScreen(stage);
        }
        // Clear history when starting fresh from Welcome
        historyStack.clear();
        show(mainWindowScreen);
    }

    /**
     * Go to Library screen
     */
    public static void goToLibrary() {
        try {
            if (libraryScreen == null) {
                libraryScreen = new LibraryScreen(stage);
                // First time creation, push current to history
                if (currentScreen != null) {
                    historyStack.push(currentScreen);
                }
                show(libraryScreen);
            } else {
                // Reusing existing Library - this is a RETURN? Or new navigation?
                // For button clicks, it's forward navigation, so push current
                if (currentScreen != null && currentScreen != libraryScreen) {
                    historyStack.push(currentScreen);
                }
                show(libraryScreen);
            }
        } catch (Exception e) {
            System.err.println("Error navigating to Library: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Go to Gallery screen
     */
    public static void goToGallery() {
        try {
            if (galleryScreen == null) {
                galleryScreen = new GalleryScreen(stage);
                if (currentScreen != null) {
                    historyStack.push(currentScreen);
                }
                show(galleryScreen);
            } else {
                if (currentScreen != null && currentScreen != galleryScreen) {
                    historyStack.push(currentScreen);
                }
                show(galleryScreen);
            }
        } catch (Exception e) {
            System.err.println("Error navigating to Gallery: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Go to About screen
     */
    public static void goToAbout() {
        try {
            if (aboutScreen == null) {
                aboutScreen = new AboutScreen(stage);
                if (currentScreen != null) {
                    historyStack.push(currentScreen);
                }
                show(aboutScreen);
            } else {
                if (currentScreen != null && currentScreen != aboutScreen) {
                    historyStack.push(currentScreen);
                }
                show(aboutScreen);
            }
        } catch (Exception e) {
            System.err.println("Error navigating to About: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // Getters
    public static Screen getMainWindowScreen() { return mainWindowScreen; }
    public static Screen getWelcomeScreen() { return welcomeScreen; }
    public static Screen getLibraryScreen() { return libraryScreen; }
    public static Screen getGalleryScreen() { return galleryScreen; }
    public static Screen getAboutScreen() { return aboutScreen; }
    public static Screen getCurrentScreen() { return currentScreen; }
    public static Stage getStage() { return stage; }
    public static Scene getCurrentScene() { return currentScene; }

    /**
     * Clear all cached screens (useful for reset/testing)
     */
    public static void clearCache() {
        mainWindowScreen = null;
        welcomeScreen = null;
        libraryScreen = null;
        galleryScreen = null;
        aboutScreen = null;
        historyStack.clear();
        currentScreen = null;
        System.out.println("Screen cache cleared");
    }


}