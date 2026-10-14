package com.example.codeplayfx.ui.controller;

import com.example.codeplayfx.ui.animator.LibraryAnimator;
import com.example.codeplayfx.ui.screen.ScreenManager;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.layout.ColumnConstraints;
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
                //loadDefaultObjects();
            }
        } catch (Exception e) {
            System.err.println(" Error loading objects: " + e.getMessage());
            //loadDefaultObjects();
        }
    }

    private void parseObjectsData(String content) {
        // Split by newlines first to find object boundaries
        String[] lines = content.split("\n");

        GameObject currentObject = null;
        boolean inTypes = false;
        boolean inMethods = false;

        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // Check for object name (starts with [ and ends with ])
            if (line.startsWith("[") && line.endsWith("]") && !line.contains("TYPES") && !line.contains("METHODS")) {
                String objectName = line.substring(1, line.length() - 1);
                currentObject = new GameObject(objectName);
                objectsMap.put(objectName, currentObject);
                inTypes = false;
                inMethods = false;
                System.out.println("Found object: " + objectName);
            }
            // Check for TYPES section
            else if (line.equals("[TYPES]")) {
                inTypes = true;
                inMethods = false;
                System.out.println("Entering TYPES for: " + (currentObject != null ? currentObject.name : "null"));
            }
            // Check for METHODS section
            else if (line.equals("[METHODS]")) {
                inTypes = false;
                inMethods = true;
                System.out.println("Entering METHODS for: " + (currentObject != null ? currentObject.name : "null"));
            }
            // Parse TYPE lines
            else if (inTypes && line.startsWith("TYPE |")) {
                String typeData = line.substring(7); // Remove "TYPE |"
                String[] parts = typeData.split("\\|");
                if (parts.length >= 3 && currentObject != null) {
                    String typeName = parts[0].trim();
                    String shortDesc = parts[1].trim();
                    String hexColor = parts.length >= 3 ? parts[2].trim() : "#ff6ec7";
                    currentObject.addType(new GameType(typeName, shortDesc, hexColor));
                    System.out.println("Added type: " + typeName);
                }
            }
            // Parse METHOD lines
            else if (inMethods && currentObject != null) {
                String[] parts = line.split("\\|");
                if (parts.length >= 6) {
                    String description = parts[1].trim().replace(";", "\n• ");
                    if (!description.startsWith("•")) description = "• " + description;

                    Method method = new Method(
                            parts[0].trim(),
                            description,
                            parts[2].trim(),
                            parts[3].trim(),
                            parts[4].trim(),
                            parts[5].trim()
                    );
                    currentObject.addMethod(method);
                    System.out.println("Added method: " + parts[0].trim());
                }
            }
        }

        System.out.println("Total objects loaded: " + objectsMap.size());
        for (String name : objectsMap.keySet()) {
            System.out.println("  - " + name + " (types: " + objectsMap.get(name).types.size() + ", methods: " + objectsMap.get(name).methods.size() + ")");
        }
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

        if (currentObject.types != null && !currentObject.types.isEmpty()) {

            Label typeLabel = new Label("🏷TYPES");
            typeLabel.setStyle("-fx-text-fill: #03edf9; -fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 5 0 0 0;");

            // FlowPane for type cards
            FlowPane typesFlow = new FlowPane();
            typesFlow.setHgap(15);
            typesFlow.setVgap(15);

            for (GameType type : currentObject.types) {
                Button typeCard = createTypeCard(type);
                typesFlow.getChildren().add(typeCard);
            }

// Add types section to container
            VBox typesBox = new VBox(10, typeLabel, typesFlow);
            cardsContainer.getChildren().add(typesBox);
            Region spacer = new Region();
            spacer.setPrefHeight(20);
            cardsContainer.getChildren().add(spacer);
        }

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
    private Button createTypeCard(GameType type) {
        VBox cardContent = new VBox(2);
        cardContent.setAlignment(javafx.geometry.Pos.CENTER);
        cardContent.setFillWidth(true);

        Label nameLabel = new Label(type.name);
        nameLabel.setStyle("-fx-text-fill: " + type.hexColor + "; -fx-font-weight: bold; -fx-font-size: 14px;");
        nameLabel.setWrapText(true);
        nameLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        nameLabel.setMaxWidth(120);  // Limit width for wrapping
        nameLabel.setAlignment(Pos.CENTER);
//        Label shortDescLabel = new Label(type.shortDescription.length() > 40 ?
//                type.shortDescription.substring(0, 37) + "..." : type.shortDescription);
//        shortDescLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 10px;");

        cardContent.getChildren().addAll(nameLabel);

        Button card = new Button();
        card.setGraphic(cardContent);
        card.setPrefSize(140, 70);
        card.setStyle(
                "-fx-background-color: rgba(40,30,60,0.8);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + type.hexColor + "80;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"+
                "-fx-wrap-text: true;" +  // Added wrap text
                        "-fx-text-alignment: center;"  // Center alignment
        );

        // Hover effect
        card.setOnMouseEntered(e -> card.setStyle(
                "-fx-background-color: rgba(86,5,181,0.8);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + type.hexColor + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        card.setOnMouseExited(e -> card.setStyle(
                "-fx-background-color: rgba(40,30,60,0.8);" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-color: " + type.hexColor + "80;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        card.setOnAction(e -> showTypeDetails(type));

        return card;
    }

    private void showTypeDetails(GameType type) {
        if (detailPopupStage != null && detailPopupStage.isShowing()) {
            detailPopupStage.close();
        }

        detailPopupStage = new Stage();
        detailPopupStage.initOwner(rootPane.getScene().getWindow());
        detailPopupStage.setTitle(type.name);
        detailPopupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        VBox popupContent = new VBox(15);
        popupContent.getStyleClass().add("library-popup");
        popupContent.setPrefSize(500, 250);

        Text typeName = new Text(type.name);
        typeName.getStyleClass().add("library-popup-title");
        typeName.setStyle("-fx-fill: " + type.hexColor + ";");

        VBox block = new VBox(10);
        block.setStyle(
                "-fx-background-color: rgba(20,30,50,0.5);" +
                        "-fx-background-radius: 12;" +
                        "-fx-border-color: " + type.hexColor + "50;" +
                        "-fx-border-radius: 12;" +
                        "-fx-border-width: 1;" +
                        "-fx-padding: 15;"
        );

        Label colorDot = new Label("● " + type.name);
        colorDot.setStyle("-fx-text-fill: " + type.hexColor + "; -fx-font-weight: bold; -fx-font-size: 14px;");

        Label descLabel = new Label(type.shortDescription);
        descLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 13px;");
        descLabel.setPrefSize(450, 250);
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(450);  // Allow wrapping
        descLabel.setMinHeight(Region.USE_COMPUTED_SIZE);
        descLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);

        block.getChildren().addAll(colorDot, descLabel);

        Button closeButton = new Button("CLOSE");
        closeButton.getStyleClass().add("library-popup-close-btn");
        closeButton.setOnAction(e -> detailPopupStage.close());

        HBox buttonBox = new HBox(closeButton);
        buttonBox.setAlignment(Pos.CENTER);

        popupContent.getChildren().addAll(typeName, block, buttonBox);

        Scene scene = new Scene(popupContent);
        scene.setFill(null);
        scene.getStylesheets().add(getClass().getResource(
                "/com/example/codeplayfx/css/screen-styles.css").toExternalForm());

        detailPopupStage.setScene(scene);
        detailPopupStage.show();
    }

    private Button createMethodCard(String baseName, List<Method> overloads) {
        Method primaryMethod = overloads.get(0);
        boolean hasOverloads = overloads.size() > 1;

        VBox cardContent = new VBox(2);
        cardContent.setAlignment(javafx.geometry.Pos.CENTER);

        Label nameLabel = new Label(baseName + "()");
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

        Label versionLabel = new Label("📝 VERSION " + versionNumber + ": " + method.fullName);
        versionLabel.setStyle("-fx-text-fill: #88aaff; -fx-font-weight: bold; -fx-font-size: 16px;");

        // PARAMETERS TABLE (GridPane)
        VBox paramsBox = new VBox(3);
        Label paramsTitle = new Label("🔧 PARAMETERS:");
        paramsTitle.setStyle("-fx-text-fill: #ff6ec7; -fx-font-weight: bold; -fx-font-size: 13px;");

        Node paramsNode;
        if (method.parameterList.isEmpty()) {
            Label noParams = new Label("No parameters required");
            noParams.setStyle("-fx-text-fill: #888888; -fx-font-size: 12px; -fx-padding: 5;");
            paramsNode = noParams;
        } else {
            GridPane paramGrid = new GridPane();
            paramGrid.setHgap(15);
            paramGrid.setVgap(8);
            paramGrid.setStyle(
                    "-fx-background-color: rgba(0,0,0,0.3);" +
                            "-fx-padding: 10;" +
                            "-fx-border-color: rgba(136,170,255,0.2);" +
                            "-fx-border-radius: 5;"
            );

            Label paramHeader = new Label("Parameter");
            paramHeader.setStyle("-fx-text-fill: #03edf9; -fx-font-weight: bold; -fx-font-size: 12px;");
            Label typeHeader = new Label("Type");
            typeHeader.setStyle("-fx-text-fill: #03edf9; -fx-font-weight: bold; -fx-font-size: 12px;");
            Label descHeader = new Label("Description");
            descHeader.setStyle("-fx-text-fill: #03edf9; -fx-font-weight: bold; -fx-font-size: 12px;");

            paramGrid.add(paramHeader, 0, 0);
            paramGrid.add(typeHeader, 1, 0);
            paramGrid.add(descHeader, 2, 0);

            int row = 1;
            for (Parameter param : method.parameterList) {
                Label nameLabel = new Label(param.name);
                nameLabel.setStyle("-fx-text-fill: #ffc864; -fx-font-size: 12px;");
                Label typeLabel = new Label(param.type);
                typeLabel.setStyle("-fx-text-fill: #88aaff; -fx-font-size: 12px;");
                Label descLabel = new Label(param.description);
                descLabel.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 11px;");
                descLabel.setWrapText(true);

                paramGrid.add(nameLabel, 0, row);
                paramGrid.add(typeLabel, 1, row);
                paramGrid.add(descLabel, 2, row);
                row++;
            }

            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPercentWidth(25);
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setPercentWidth(25);
            ColumnConstraints col3 = new ColumnConstraints();
            col3.setPercentWidth(50);
            paramGrid.getColumnConstraints().addAll(col1, col2, col3);

            paramsNode = paramGrid;
        }

        paramsBox.getChildren().addAll(paramsTitle, paramsNode);

        // RETURNS
        VBox returnsBox = new VBox(3);
        Label returnsTitle = new Label("📤 RETURNS:");
        returnsTitle.setStyle("-fx-text-fill: #03edf9; -fx-font-weight: bold; -fx-font-size: 13px;");
        TextArea returnsArea = new TextArea(method.returns);
        returnsArea.setEditable(false);
        returnsArea.setWrapText(true);
        returnsArea.setPrefHeight(30);
        returnsArea.getStyleClass().add("library-popup-textarea");
        returnsBox.getChildren().addAll(returnsTitle, returnsArea);

        // EXAMPLE
        VBox exampleBox = new VBox(3);
        Label exampleTitle = new Label("💻 EXAMPLE:");
        exampleTitle.setStyle("-fx-text-fill: #ffc864; -fx-font-weight: bold; -fx-font-size: 13px;");
        TextArea exampleArea = new TextArea(method.example);
        exampleArea.setEditable(false);
        exampleArea.setWrapText(true);
        exampleArea.setPrefHeight(50);
        exampleArea.getStyleClass().add("library-popup-textarea");
        exampleBox.getChildren().addAll(exampleTitle, exampleArea);

        // COPY BUTTON
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
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent content = new ClipboardContent();
            content.putString(method.example);
            clipboard.setContent(content);
            copyBtn.setText("✓ COPIED!");
            new Thread(() -> {
                try { Thread.sleep(1000); } catch (InterruptedException ex) {}
                Platform.runLater(() -> copyBtn.setText("📋 COPY EXAMPLE"));
            }).start();
        });

        HBox copyBox = new HBox(copyBtn);
        copyBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);

        Label descLabel = new Label(method.description);
        descLabel.setStyle("-fx-text-fill: #aaaaaa; -fx-font-size: 12px; -fx-padding: 0 0 4 0;");
        descLabel.setWrapText(true);
        block.getChildren().addAll(versionLabel, descLabel, paramsBox, returnsBox, exampleBox, copyBox);
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
        List<GameType> types = new ArrayList<>();  // ADD THIS

        GameObject(String name) {
            this.name = name;
        }

        void addMethod(Method method) {
            methods.add(method);
        }

        void addType(GameType type) {  // ADD THIS
            types.add(type);
        }
    }
    private static class Method {
        String fullName;
        String baseName;
        String description;
        String parameters;
        String returns;
        String example;
        String category;
        List<Parameter> parameterList = new ArrayList<>();  // ADD THIS

        Method(String fullName, String description, String parameters, String returns, String example, String category) {
            this.fullName = fullName;
            this.baseName = fullName.contains("(") ? fullName.substring(0, fullName.indexOf("(")) : fullName;
            this.description = description;
            this.parameters = parameters;
            this.returns = returns;
            this.example = example;
            this.category = category;
            parseParameters(parameters);  // ADD THIS
        }

        private void parseParameters(String params) {  // ADD THIS METHOD
            if (params.equals("none") || params.isEmpty()) return;

            String[] paramArray = params.split("\\s*,\\s*(?=[a-zA-Z])");
            for (String param : paramArray) {
                String[] parts = param.trim().split(":");
                if (parts.length >= 2) {
                    String name = parts[0].trim();
                    String type = parts[1].trim();
                    String description = parts.length >= 3 ? parts[2].trim() : "";
                    parameterList.add(new Parameter(name, type, description));
                }
            }
        }
    }
    private static class GameType {
        String name;
        String shortDescription;
        String hexColor;

        GameType(String name, String shortDescription, String hexColor) {
            this.name = name;
            this.shortDescription = shortDescription;
            this.hexColor = hexColor;
        }
    }

    private static class Parameter {
        String name;
        String type;
        String description;

        Parameter(String name, String type, String description) {
            this.name = name;
            this.type = type;
            this.description = description;
        }
    }
}