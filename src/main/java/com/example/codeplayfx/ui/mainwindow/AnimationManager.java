package com.example.codeplayfx.ui.mainwindow;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class AnimationManager {

    private static final Map<Node, Timeline> activeAnimations = new HashMap<>();
    private static final Map<Pane, AnimationTimer> activeTimers = new HashMap<>();

    public static final Color CYAN_BRIGHT = Color.web("#03edf9");
    public static final Color CYAN_CURSOR = Color.web("#00c3ff");
    public static final Color PINK_BRIGHT = Color.web("#fc28a8");
    public static final Color PINK_HOT = Color.web("#fc199a");
    public static final Color PURPLE_MID = Color.web("#9963ff");
    public static final Color YELLOW_GOLD = Color.web("#ffcc00");


    public static void startCursorPulse(Node editorNode) {
        stopAnimation(editorNode);
        DropShadow glow = new DropShadow();
        glow.setColor(CYAN_CURSOR);
        glow.setRadius(8);
        glow.setSpread(0.2);
        editorNode.setEffect(glow);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO,
                new KeyValue(glow.radiusProperty(), 8.0),
                new KeyValue(glow.spreadProperty(), 0.2)
            ),
            new KeyFrame(Duration.millis(500),
                new KeyValue(glow.radiusProperty(), 25.0),
                new KeyValue(glow.spreadProperty(), 0.4)
            ),
            new KeyFrame(Duration.millis(1000),
                new KeyValue(glow.radiusProperty(), 8.0),
                new KeyValue(glow.spreadProperty(), 0.2)
            )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        activeAnimations.put(editorNode, timeline);
    }

    public static void startLogoAnimation(Label logoLabel) {
        stopAnimation(logoLabel);
        DropShadow glow = new DropShadow();
        glow.setRadius(22);
        glow.setSpread(0.6);
        logoLabel.setEffect(glow);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.colorProperty(), CYAN_BRIGHT)),
            new KeyFrame(Duration.millis(500), new KeyValue(glow.colorProperty(), PINK_BRIGHT)),
            new KeyFrame(Duration.millis(1000), new KeyValue(glow.colorProperty(), CYAN_BRIGHT))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        activeAnimations.put(logoLabel, timeline);
    }

    public static void startNeonLineSweep(Node node) {
        stopAnimation(node);
        DropShadow glow = new DropShadow();
        glow.setRadius(8);
        glow.setSpread(0.4);
        node.setEffect(glow);

        Timeline timeline = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.colorProperty(), PINK_BRIGHT)),
            new KeyFrame(Duration.millis(500), new KeyValue(glow.colorProperty(), CYAN_BRIGHT)),
            new KeyFrame(Duration.millis(1000), new KeyValue(glow.colorProperty(), YELLOW_GOLD)),
            new KeyFrame(Duration.millis(1500), new KeyValue(glow.colorProperty(), CYAN_BRIGHT)),
            new KeyFrame(Duration.millis(2000), new KeyValue(glow.colorProperty(), PINK_BRIGHT))
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        activeAnimations.put(node, timeline);
    }

    public static void selectionGlow(Node editorNode) {
        stopAnimation(editorNode);
        DropShadow glow = new DropShadow();
        glow.setColor(Color.web("#6600ff", 0.33));
        glow.setRadius(30);
        glow.setSpread(0.15);
        editorNode.setEffect(glow);

//        FadeTransition fade = new FadeTransition(Duration.millis(600), editorNode);
//        fade.setFromValue(1.0);
//        fade.setToValue(1.0);
//        fade.setOnFinished(e -> startCursorPulse(editorNode));
//        fade.play();
    }

    public static void flashGlow(Node node, Color color) {
        DropShadow glow = new DropShadow();
        glow.setColor(color);
        glow.setRadius(0);
        node.setEffect(glow);

        Timeline t = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.radiusProperty(), 0.0)),
            new KeyFrame(Duration.millis(80), new KeyValue(glow.radiusProperty(), 32.0)),
            new KeyFrame(Duration.millis(250), new KeyValue(glow.radiusProperty(), 20.0)),
            new KeyFrame(Duration.millis(800), new KeyValue(glow.radiusProperty(), 0.0))
        );
        t.setCycleCount(1);
        t.setOnFinished(e -> node.setEffect(null));
        t.play();
    }

    public static void stopAnimation(Node node) {
        Timeline t = activeAnimations.remove(node);
        if (t != null) t.stop();
        node.setEffect(null);
    }
}