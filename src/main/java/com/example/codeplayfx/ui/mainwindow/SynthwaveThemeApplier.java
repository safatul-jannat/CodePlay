package com.example.codeplayfx.ui.mainwindow;

import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import org.fxmisc.richtext.CodeArea;

public class SynthwaveThemeApplier {

    private static final String THEME_CSS = "/com/example/codeplayfx/css/synthwave-theme.css";

    public static void apply(
            Scene scene,
            Label logoLabel,
            CodeArea codeEditor,
            Pane gameCanvas,
            Label statusLabel,
            Button runButton,
            Button pauseButton,
            Button stopButton,
            Button saveButton
    ) {
        applyStylesheets(scene);

        if (logoLabel != null) {
            AnimationManager.startLogoAnimation(logoLabel);
        }

        if (codeEditor != null) {


                   // AnimationManager.startCursorPulse(codeEditor);


            codeEditor.selectedTextProperty().addListener((obs, oldSel, newSel) -> {
                if (newSel != null && !newSel.isEmpty()) {
                    AnimationManager.selectionGlow(codeEditor);
                }else {
                    // Remove glow when selection is cleared
                    AnimationManager.stopAnimation(codeEditor);
                }
            });
        }

        if (gameCanvas != null) {
            DropShadow subtleGlow = new DropShadow();
            subtleGlow.setColor(Color.web("#03edf9", 0.15));
            subtleGlow.setRadius(8);
            gameCanvas.setEffect(subtleGlow);
        }

        if (statusLabel != null) {
            statusLabel.setText("Ready");
        }

        wireButtonAnimations(runButton, pauseButton, stopButton, saveButton);
    }

    private static void wireButtonAnimations(
            Button runButton, Button pauseButton,
            Button stopButton, Button saveButton
    ) {
        if (runButton != null) {
            runButton.setOnMouseEntered(e ->
                AnimationManager.flashGlow(runButton, AnimationManager.CYAN_BRIGHT)
            );
        }
        if (pauseButton != null) {
            pauseButton.setOnMouseEntered(e ->
                AnimationManager.flashGlow(pauseButton, AnimationManager.YELLOW_GOLD)
            );
        }
        if (stopButton != null) {
            stopButton.setOnMouseEntered(e ->
                AnimationManager.flashGlow(stopButton, AnimationManager.PINK_HOT)
            );
        }
        if (saveButton != null) {
            saveButton.setOnMouseEntered(e ->
                AnimationManager.flashGlow(saveButton, AnimationManager.CYAN_BRIGHT)
            );
        }
    }

    private static void applyStylesheets(Scene scene) {
        try {
            var url = SynthwaveThemeApplier.class.getResource(THEME_CSS);
            if (url != null) {
                scene.getStylesheets().add(url.toExternalForm());
                System.out.println(" Synthwave theme loaded");
            } else {
                System.err.println(" CSS not found: " + THEME_CSS);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}