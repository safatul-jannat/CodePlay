package com.example.codeplayfx.ui.controller;

import com.example.codeplayfx.ui.animator.LibraryAnimator;
import com.example.codeplayfx.ui.screen.ScreenManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LibraryController implements Initializable {

    @FXML private StackPane rootPane;
    @FXML private Pane bgPane, gridPane, orbPane;
    @FXML private Canvas particleCanvas;
    @FXML private BorderPane contentBorderPane;

    // Navigation buttons
    @FXML private Button backButton, homeButton, mainWindowButton, galleryButton, aboutButton;

    // Library components
    @FXML private ListView<String> objectsListView;
    @FXML private Label selectedObjectLabel;
    @FXML private VBox cardsContainer;

    private LibraryAnimator animator;
    private Map<String, GameObject> objectsMap = new HashMap<>();
    private GameObject currentObject = null;

    // Method detail popup stage (reuse to avoid multiple)
    private Stage detailPopupStage = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Bind background panes
        bgPane.prefWidthProperty().bind(rootPane.widthProperty());
        bgPane.prefHeightProperty().bind(rootPane.heightProperty());
        gridPane.prefWidthProperty().bind(rootPane.widthProperty());
        gridPane.prefHeightProperty().bind(rootPane.heightProperty());
        orbPane.prefWidthProperty().bind(rootPane.widthProperty());
        orbPane.prefHeightProperty().bind(rootPane.heightProperty());
        particleCanvas.widthProperty().bind(rootPane.widthProperty());
        particleCanvas.heightProperty().bind(rootPane.heightProperty());

        // Create animator
        animator = new LibraryAnimator(
                rootPane, bgPane, gridPane, particleCanvas, orbPane,
                backButton, homeButton, mainWindowButton, galleryButton, aboutButton
        );

        animator.initializeLibrary();

        // Load objects data
        loadObjectsData();

        // Setup list view
        setupObjectsList();
    }

    private void loadObjectsData() {
        try {
            InputStream is = getClass().getResourceAsStream("/com/example/codeplayfx/files/objects.txt");
            if (is != null) {
                String content = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                parseObjectsData(content);
                System.out.println(" Objects loaded from objects.txt");
            } else {
                System.out.println(" objects.txt not found, using defaults");
                loadDefaultObjects();
            }
        } catch (Exception e) {
            System.err.println(" Error loading objects: " + e.getMessage());
            loadDefaultObjects();
        }
    }

    private void parseObjectsData(String content) {
        String[] sections = content.split("\\[");

        for (String section : sections) {
            if (section.trim().isEmpty()) continue;

            // Find object name
            int endOfName = section.indexOf("]");
            if (endOfName == -1) continue;

            String objectName = section.substring(0, endOfName).trim();
            String objectData = section.substring(endOfName + 1).trim();

            GameObject obj = new GameObject(objectName);

            // Parse methods
            String[] lines = objectData.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                // Format: methodName|description|parameters|returns|example|category
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    Method method = new Method(
                            parts[0].trim(),  // name
                            parts[1].trim(),  // description
                            parts[2].trim(),  // parameters
                            parts[3].trim(),  // returns
                            parts[4].trim(),  // example
                            parts[5].trim()   // category
                    );
                    obj.addMethod(method);
                }
            }

            objectsMap.put(objectName, obj);
        }
    }

    private void loadDefaultObjects() {
        // Player object with overloads example
        GameObject player = new GameObject("Player 👤");
        player.addMethod(new Method("jump()", "Makes player jump with default height", "none", "boolean", "player.jump();", "MOVEMENT"));
        player.addMethod(new Method("jump(height)", "Makes player jump with specified height", "height: int", "boolean", "player.jump(15);", "MOVEMENT"));
        player.addMethod(new Method("jump(height, sound)", "Makes player jump with height and sound", "height: int, sound: boolean", "boolean", "player.jump(15, true);", "MOVEMENT"));
        player.addMethod(new Method("moveLeft()", "Move player left", "speed: int = 5", "void", "player.moveLeft();", "MOVEMENT"));
        player.addMethod(new Method("moveRight()", "Move player right", "speed: int = 5", "void", "player.moveRight();", "MOVEMENT"));
        player.addMethod(new Method("applyGravity()", "Apply gravity to player", "force: double = 0.5", "void", "player.applyGravity();", "PHYSICS"));
        player.addMethod(new Method("checkCollision()", "Check collision with object", "obj: GameObject", "boolean", "player.checkCollision(ground);", "PHYSICS"));
        player.addMethod(new Method("onKeyPress()", "Handle key press", "key: String", "void", "player.onKeyPress(\"SPACE\");", "INPUT"));
        objectsMap.put("Player 👤", player);

        // Ground object
        GameObject ground = new GameObject("Ground 🌍");
        ground.addMethod(new Method("setSize()", "Set ground dimensions", "width: int, height: int", "void", "ground.setSize(800, 50);", "PROPERTIES"));
        ground.addMethod(new Method("addObstacle()", "Add obstacle to ground", "x: int, y: int", "void", "ground.addObstacle(200, 0);", "OBSTACLES"));
        objectsMap.put("Ground 🌍", ground);

        // Enemy object
        GameObject enemy = new GameObject("Enemy 👾");
        enemy.addMethod(new Method("chase()", "Chase player", "target: Player", "void", "enemy.chase(player);", "BEHAVIOR"));
        enemy.addMethod(new Method("attack()", "Attack player", "damage: int = 10", "void", "enemy.attack();", "COMBAT"));
        objectsMap.put("Enemy 👾", enemy);

        // Game object
        GameObject game = new GameObject("Game 🎮");
        game.addMethod(new Method("start()", "Start the game", "none", "void", "game.start();", "CONTROL"));
        game.addMethod(new Method("pause()", "Pause the game", "none", "void", "game.pause();", "CONTROL"));
        game.addMethod(new Method("gameOver()", "End the game", "none", "void", "game.gameOver();", "CONTROL"));
        objectsMap.put("Game 🎮", game);
    }

    private void setupObjectsList() {
        objectsListView.getItems().addAll(objectsMap.keySet());

        objectsListView.setCellFactory(lv -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setStyle("-fx-text-fill: white; -fx-font-size: 14px; -fx-padding: 8;");
                }
            }
        });

        objectsListView.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                showObjectMethods(newVal);
            }
        });
    }

    private void showObjectMethods(String objectName) {
        currentObject = objectsMap.get(objectName);
        if (currentObject == null) return;

        selectedObjectLabel.setText(objectName);
        cardsContainer.getChildren().clear();

        // Group methods by base name and category
        Map<String, Map<String, List<Method>>> methodsByCategoryAndBase = new HashMap<>();

        for (Method method : currentObject.methods) {
            methodsByCategoryAndBase
                    .computeIfAbsent(method.category, k -> new HashMap<>())
                    .computeIfAbsent(method.baseName, k -> new ArrayList<>())
                    .add(method);
        }

        // Create category sections
        for (Map.Entry<String, Map<String, List<Method>>> categoryEntry : methodsByCategoryAndBase.entrySet()) {
            String category = categoryEntry.getKey();

            // Category label
            Label categoryLabel = new Label(category);
            categoryLabel.setStyle("-fx-text-fill: #ff6ec7; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

            // FlowPane for method cards
            FlowPane cardsFlow = new FlowPane();
            cardsFlow.setHgap(15);
            cardsFlow.setVgap(15);

            // Create cards for each base method (grouping overloads)
            for (Map.Entry<String, List<Method>> methodEntry : categoryEntry.getValue().entrySet()) {
                Button methodCard = createMethodCard(methodEntry.getKey(), methodEntry.getValue());
                cardsFlow.getChildren().add(methodCard);
            }

            // Add to container
            VBox categoryBox = new VBox(10, categoryLabel, cardsFlow);
            cardsContainer.getChildren().add(categoryBox);
        }
    }

    private Button createMethodCard(String baseName, List<Method> overloads) {
        Method primaryMethod = overloads.get(0);
        boolean hasOverloads = overloads.size() > 1;

        VBox cardContent = new VBox(2);
        cardContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label nameLabel = new Label(
                baseName.startsWith("new ") ? baseName : baseName + "()"
        );
        nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 14px;");

        cardContent.getChildren().add(nameLabel);

        if (hasOverloads) {
            Label badgeLabel = new Label("+" + (overloads.size() - 1) + " versions");
            badgeLabel.setStyle(
                    "-fx-text-fill: #ff6ec7;" +
                            "-fx-font-size: 10px;" +
                            "-fx-background-color: rgba(255,110,199,0.2);" +
                            "-fx-background-radius: 10;" +
                            "-fx-padding: 2 6;"
            );
            cardContent.getChildren().add(badgeLabel);
        }

        Button card = new Button();
        card.setGraphic(cardContent);
        card.setPrefSize(140, 70);
        card.setStyle(
                "-fx-background-color: rgba(40,30,60,0.8);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(136,170,255,0.5);" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(86,5,181,0.8);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: #ff6ec7;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(40,30,60,0.8);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: rgba(136,170,255,0.5);" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        card.setOnAction(e -> showMethodDetails(overloads));

        return card;
    }

    private void showMethodDetails(List<Method> overloads) {
        // Close existing popup if open
        if (detailPopupStage != null && detailPopupStage.isShowing()) {
            detailPopupStage.close();
        }

        detailPopupStage = new Stage();
        detailPopupStage.initOwner(rootPane.getScene().getWindow());
        detailPopupStage.setTitle(overloads.get(0).baseName + "()");
        detailPopupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        // Main popup container
        VBox popupContent = new VBox(15);
        popupContent.getStyleClass().add("library-popup");
        popupContent.setPrefSize(550, 550);

        // Method name (base name without parameters)
        Text methodName = new Text(overloads.get(0).baseName + "()");
        methodName.getStyleClass().add("library-popup-title");

        // Scrollable content area for all overload blocks
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        VBox overloadsContainer = new VBox(20);
        overloadsContainer.setStyle("-fx-padding: 5;");

        // Create a block for each overload
        for (int i = 0; i < overloads.size(); i++) {
            Method method = overloads.get(i);
            VBox methodBlock = createMethodBlock(method, i + 1);
            overloadsContainer.getChildren().add(methodBlock);
        }

        scrollPane.setContent(overloadsContainer);

        // Close button
        Button closeButton = new Button("CLOSE");
        closeButton.getStyleClass().add("library-popup-close-btn");
        closeButton.setOnAction(e -> detailPopupStage.close());

        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(javafx.geometry.Pos.CENTER);
        buttonBox.setStyle("-fx-padding: 10 0 0 0;");

        popupContent.getChildren().addAll(methodName, scrollPane, buttonBox);

        // Scene with styles
        Scene scene = new Scene(popupContent);
        scene.setFill(null);
        String cssPath = "/com/example/codeplayfx/css/screen-styles.css";
        scene.getStylesheets().add(getClass().getResource(cssPath).toExternalForm());

        detailPopupStage.setScene(scene);
        detailPopupStage.show();
    }

    private VBox createMethodBlock(Method method, int versionNumber) {
        VBox block = new VBox(8);
        block.setStyle(
                "-fx-background-color: rgba(20,30,50,0.5);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: rgba(136,170,255,0.3);" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 15;"
        );

        // Version header
        Label versionLabel = new Label("📝 VERSION " + versionNumber + ": " + method.fullName);
        versionLabel.setStyle("-fx-text-fill: #88aaff; -fx-font-weight: bold; -fx-font-size: 16px;");

        // Parameters
        VBox paramsBox = new VBox(3);
        Label paramsTitle = new Label("🔧 PARAMETERS:");
        paramsTitle.setStyle("-fx-text-fill: #ff6ec7; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextArea paramsArea = new TextArea(formatParameters(method.parameters));
        paramsArea.setEditable(false);
        paramsArea.setWrapText(true);
        paramsArea.setPrefHeight(method.parameters.contains(",") ? 60 : 30);
        paramsArea.getStyleClass().add("library-popup-textarea");

        paramsBox.getChildren().addAll(paramsTitle, paramsArea);

        // Returns
        VBox returnsBox = new VBox(3);
        Label returnsTitle = new Label("📤 RETURNS:");
        returnsTitle.setStyle("-fx-text-fill: #03edf9; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextArea returnsArea = new TextArea(method.returns);
        returnsArea.setEditable(false);
        returnsArea.setWrapText(true);
        returnsArea.setPrefHeight(30);
        returnsArea.getStyleClass().add("library-popup-textarea");

        returnsBox.getChildren().addAll(returnsTitle, returnsArea);

        // Example
        VBox exampleBox = new VBox(3);
        Label exampleTitle = new Label("💻 EXAMPLE:");
        exampleTitle.setStyle("-fx-text-fill: #ffc864; -fx-font-weight: bold; -fx-font-size: 13px;");

        TextArea exampleArea = new TextArea(method.example);
        exampleArea.setEditable(false);
        exampleArea.setWrapText(true);
        exampleArea.setPrefHeight(50);
        exampleArea.getStyleClass().add("library-popup-textarea");

        exampleBox.getChildren().addAll(exampleTitle, exampleArea);

        // Copy button for this method
        Button copyBtn = new Button("📋 COPY EXAMPLE");
        copyBtn.setStyle(
                "-fx-background-color: rgba(3,237,249,0.1);" +
                        "-fx-text-fill: #03edf9;" +
                        "-fx-font-size: 11px;" +
                        "-fx-border-color: #03edf9;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 5 12;" +
                        "-fx-background-radius: 3;" +
                        "-fx-border-radius: 3;" +
                        "-fx-cursor: hand;"
        );
        copyBtn.setOnAction(e -> {
            javafx.scene.input.Clipboard clipboard = javafx.scene.input.Clipboard.getSystemClipboard();
            javafx.scene.input.ClipboardContent content = new javafx.scene.input.ClipboardContent();
            content.putString(method.example);
            clipboard.setContent(content);

            // Visual feedback
            copyBtn.setText("✓ COPIED!");
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                javafx.application.Platform.runLater(() -> copyBtn.setText("📋 COPY EXAMPLE"));
            }).start();
        });

        HBox copyBox = new HBox(copyBtn);
        copyBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        block.getChildren().addAll(versionLabel, paramsBox, returnsBox, exampleBox, copyBox);
        return block;
    }

    private String formatParameters(String params) {
        if (params.equals("none") || params.isEmpty()) {
            return "No parameters";
        }

        // Format parameter list nicely
        String[] paramList = params.split(",");
        StringBuilder formatted = new StringBuilder();
        for (String param : paramList) {
            formatted.append("• ").append(param.trim()).append("\n");
        }
        return formatted.toString();
    }

    public void cleanup() {
        if (animator != null) animator.shutdown();
        if (detailPopupStage != null && detailPopupStage.isShowing()) {
            detailPopupStage.close();
        }
    }

    // Navigation methods
    @FXML public void goBack(ActionEvent event) { ScreenManager.goBack(); }
    @FXML public void goHome(ActionEvent event) { ScreenManager.goToWelcome(); }
    @FXML public void goMainWindow(ActionEvent event) { ScreenManager.goToMainWindow(); }
    @FXML public void goGallery(ActionEvent event) { ScreenManager.goToGallery(); }
    @FXML public void goAbout(ActionEvent event) { ScreenManager.goToAbout(); }

    // Helper classes
    private static class GameObject {
        String name;
        List<Method> methods = new ArrayList<>();

        GameObject(String name) {
            this.name = name;
        }

        void addMethod(Method method) {
            methods.add(method);
        }
    }

    private static class Method {
        String fullName;      // "jump()", "jump(height)"
        String baseName;      // "jump"
        String description;
        String parameters;
        String returns;
        String example;
        String category;

        Method(String fullName, String description, String parameters, String returns, String example, String category) {
            this.fullName = fullName;
            this.baseName = fullName.contains("(") ?
                    fullName.substring(0, fullName.indexOf("(")) : fullName;
            this.description = description;
            this.parameters = parameters;
            this.returns = returns;
            this.example = example;
            this.category = category;
        }
    }
}