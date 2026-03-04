package com.example.codeplayfx.ui.animator;

import javafx.animation.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Effect;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * WelcomeAnimator - Handles welcome screen specific animations
 * Extends BackgroundAnimator for common background elements
 */
public class WelcomeAnimator extends BackgroundAnimator {

    /* ── Content nodes ──────────────────────────────────────────────────── */
    private final VBox contentBox;
    private final Text badgeText;
    private final HBox titleBox;
    private final Text taglineText;
    private final HBox dividerBox;
    private final HBox buttonRow;

    /* ── Title letters ─────────────────────────────────────────────────── */
    private final Text[] letters;
    private AnimationTimer sweepTimer;

    /* ── Button elements ───────────────────────────────────────────────── */
    private final StackPane btnNewWrap, btnGalleryWrap, btnLibraryWrap, btnAboutWrap;
    private final Rectangle cardNew, cardGallery, cardLibrary, cardAbout;
    private final Map<StackPane, Animation> buttonAnimations = new HashMap<>();
    private final Map<StackPane, Timeline> cursorPulseAnimations = new HashMap<>();

    /* ── Tagline ───────────────────────────────────────────────────────── */
    private static final String TAGLINE = "Write the logic. See the world come alive.";
    private int tagIdx = 0;
    private Timeline taglineTimeline;

    /* ── Button actions ────────────────────────────────────────────────── */
    private final Runnable onNewAction;
    private final Runnable onGalleryAction;
    private final Runnable onLibraryAction;
    private final Runnable onAboutAction;

    public WelcomeAnimator(
            StackPane rootPane, Pane bgPane, Pane gridPane, Canvas particleCanvas, Pane orbPane,
            VBox contentBox, Text badgeText, HBox titleBox, Text taglineText, HBox dividerBox, HBox buttonRow,
            Text lC, Text lO, Text lD, Text lE, Text lP, Text lL, Text lA, Text lY,
            StackPane btnNewWrap, StackPane btnGalleryWrap, StackPane btnLibraryWrap, StackPane btnAboutWrap,
            Rectangle cardNew, Rectangle cardGallery, Rectangle cardLibrary, Rectangle cardAbout,
            Runnable onNewAction, Runnable onGalleryAction, Runnable onLibraryAction, Runnable onAboutAction) {

        super(rootPane, bgPane, gridPane, particleCanvas, orbPane);

        this.contentBox = contentBox;
        this.badgeText = badgeText;
        this.titleBox = titleBox;
        this.taglineText = taglineText;
        this.dividerBox = dividerBox;
        this.buttonRow = buttonRow;
        this.letters = new Text[]{lC, lO, lD, lE, lP, lL, lA, lY};
        this.btnNewWrap = btnNewWrap;
        this.btnGalleryWrap = btnGalleryWrap;
        this.btnLibraryWrap = btnLibraryWrap;
        this.btnAboutWrap = btnAboutWrap;
        this.cardNew = cardNew;
        this.cardGallery = cardGallery;
        this.cardLibrary = cardLibrary;
        this.cardAbout = cardAbout;
        this.onNewAction = onNewAction;
        this.onGalleryAction = onGalleryAction;
        this.onLibraryAction = onLibraryAction;
        this.onAboutAction = onAboutAction;
    }

    /**
     * Initialize all welcome screen animations
     */
    public void initializeWelcome() {
        // Initialize background first
        initializeBackground();

        // Welcome-specific animations
        animateBadge();
        animateTitle();
        animateTagline();
        animateDivider();
        animateButtons();
    }

    @Override
    public void shutdown() {
        super.shutdown();

        if (sweepTimer != null) sweepTimer.stop();
        if (taglineTimeline != null) taglineTimeline.stop();

        // Stop button animations
        for (Animation anim : buttonAnimations.values()) anim.stop();
        for (Timeline timeline : cursorPulseAnimations.values()) timeline.stop();

        buttonAnimations.clear();
        cursorPulseAnimations.clear();
    }

    /* ════════════════════════════════════════════════════════════════════════
       WELCOME-SPECIFIC ANIMATIONS
     ═══════════════════════════════════════════════════════════════════════ */

    private void animateBadge() {
        FadeTransition ft = new FadeTransition(Duration.seconds(1.0), badgeText);
        ft.setFromValue(0); ft.setToValue(1);
        ft.setDelay(Duration.seconds(0.3)); ft.play();
    }

    private void animateTitle() {
        for (int i = 0; i < letters.length; i++) {
            Text t = letters[i];
            t.setTranslateY(-40);
            long delay = 400 + i * 80L;

            TranslateTransition drop = new TranslateTransition(Duration.millis(600), t);
            drop.setToY(0); drop.setDelay(Duration.millis(delay));
            drop.setInterpolator(Interpolator.SPLINE(.2, .8, .3, 1)); drop.play();

            FadeTransition fade = new FadeTransition(Duration.millis(500), t);
            fade.setToValue(1); fade.setDelay(Duration.millis(delay)); fade.play();
        }
        new Timeline(new KeyFrame(Duration.seconds(1.6), e -> startSweep())).play();
    }

    private void startSweep() {
        final long[] t0 = {-1};
        sweepTimer = new AnimationTimer() {
            @Override public void handle(long now) {
                if (t0[0] < 0) t0[0] = now;
                double elapsed = (now - t0[0]) / 1_000_000_000.0;
                double t = (elapsed % 3.0) / 3.0;
                double sw = t < 0.5 ? t * 2 : (1 - t) * 2;

                for (int i = 0; i < letters.length; i++) {
                    if (letters[i] == null) continue;
                    double phase = (double) i / (letters.length - 1);
                    double intensity = Math.max(0, 1 - Math.abs(sw - phase) * 2.5);
                    double hue = (sw * 360 + i * 30) % 360;
                    Color vivid = Color.hsb(hue, 0.9, 1.0);
                    letters[i].setFill(Color.WHITE.interpolate(vivid, intensity * .85));

                    DropShadow ds = (DropShadow) letters[i].getEffect();
                    if (ds != null) {
                        ds.setColor(vivid.deriveColor(0, 1, 1, .7 + intensity * .3));
                        ds.setRadius(18 + intensity * 24);
                        ds.setOffsetY(2 + intensity * 4);
                    }
                }
            }
        };
        sweepTimer.start();
    }

    private void animateTagline() {
        new Timeline(new KeyFrame(Duration.seconds(2.0), e -> {
            taglineText.setOpacity(1);
            taglineTimeline = new Timeline(new KeyFrame(Duration.millis(38), ev -> {
                if (tagIdx <= TAGLINE.length())
                    taglineText.setText(TAGLINE.substring(0, tagIdx++));
            }));
            taglineTimeline.setCycleCount(Animation.INDEFINITE);
            taglineTimeline.play();
        })).play();
    }

    private void animateDivider() {
        FadeTransition ft = new FadeTransition(Duration.seconds(0.8), dividerBox);
        ft.setFromValue(0); ft.setToValue(1);
        ft.setDelay(Duration.seconds(2.2)); ft.play();
    }

    private void animateButtons() {
        record BtnInfo(StackPane wrap, Color accentColor, Runnable action) {}

        List<BtnInfo> btns = List.of(
                new BtnInfo(btnNewWrap, Color.web("#6644ff"), onNewAction),
                new BtnInfo(btnGalleryWrap, Color.web("#0099ff"), onGalleryAction),
                new BtnInfo(btnLibraryWrap, Color.web("#00ccaa"), onLibraryAction),
                new BtnInfo(btnAboutWrap, Color.web("#ff3399"), onAboutAction)
        );

        for (int i = 0; i < btns.size(); i++) {
            BtnInfo b = btns.get(i);
            long delay = 2500 + i * 130L;

            b.wrap().setTranslateY(50);
            b.wrap().setOpacity(0);

            FadeTransition ft = new FadeTransition(Duration.millis(600), b.wrap());
            ft.setToValue(1); ft.setDelay(Duration.millis(delay)); ft.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(600), b.wrap());
            tt.setToY(0); tt.setDelay(Duration.millis(delay));
            tt.setInterpolator(Interpolator.SPLINE(.2, .8, .3, 1)); tt.play();

            javafx.scene.transform.Rotate rx = new javafx.scene.transform.Rotate(-6, javafx.scene.transform.Rotate.X_AXIS);
            b.wrap().getTransforms().add(rx);

            TranslateTransition bob = new TranslateTransition(Duration.seconds(2.2 + rng.nextDouble()), b.wrap());
            bob.setByY(-5); bob.setAutoReverse(true);
            bob.setCycleCount(Animation.INDEFINITE);
            bob.setDelay(Duration.millis(delay + rng.nextInt(400)));
            bob.play();
            buttonAnimations.put(b.wrap(), bob);

            storeInitialButtonState(b.wrap(), b.accentColor());

            // Hover effects
            b.wrap().setOnMouseEntered(e -> {
                StackPane btn = (StackPane) e.getSource();
                stopButtonPulse(btn);

                ScaleTransition hoverScale = new ScaleTransition(Duration.millis(180), btn);
                hoverScale.setToX(1.07); hoverScale.setToY(1.07); hoverScale.play();

                TranslateTransition hoverTranslate = new TranslateTransition(Duration.millis(180), btn);
                hoverTranslate.setByY(-8); hoverTranslate.play();

                Color accent = (Color) ((Object[]) btn.getUserData())[0];
                startButtonPulse(btn, accent);
            });

            b.wrap().setOnMouseExited(e -> {
                StackPane btn = (StackPane) e.getSource();
                stopButtonPulse(btn);

                ScaleTransition exitScale = new ScaleTransition(Duration.millis(180), btn);
                exitScale.setToX(1.0); exitScale.setToY(1.0); exitScale.play();

                TranslateTransition exitTranslate = new TranslateTransition(Duration.millis(180), btn);
                exitTranslate.setByY(8); exitTranslate.play();
            });

            b.wrap().setOnMousePressed(e -> {
                StackPane btn = (StackPane) e.getSource();
                pauseButtonPulse(btn);

                ScaleTransition pressScale = new ScaleTransition(Duration.millis(90), btn);
                pressScale.setToX(.95); pressScale.setToY(.95); pressScale.play();
            });

            b.wrap().setOnMouseReleased(e -> {
                StackPane btn = (StackPane) e.getSource();
                resumeButtonPulse(btn);

                ScaleTransition releaseScale = new ScaleTransition(Duration.millis(90), btn);
                releaseScale.setToX(1.0); releaseScale.setToY(1.0); releaseScale.play();
            });

            b.wrap().setOnMouseClicked(e -> b.action().run());
        }
    }

    // Button helper methods
    private void storeInitialButtonState(StackPane button, Color accentColor) {
        Rectangle card = findCardRectangle(button);
        if (card != null) {
            Color originalStroke = (Color) card.getStroke();
            Effect originalEffect = card.getEffect();
            button.setUserData(new Object[]{accentColor, originalStroke, originalEffect});
        }
    }

    private Rectangle findCardRectangle(StackPane button) {
        for (javafx.scene.Node node : button.getChildren()) {
            if (node instanceof Rectangle && ((Rectangle) node).getStrokeWidth() > 0) {
                return (Rectangle) node;
            }
        }
        return null;
    }

    private void startButtonPulse(StackPane button, Color accentColor) {
        stopButtonPulse(button);
        Rectangle card = findCardRectangle(button);
        if (card == null) return;

        Timeline timeline = new Timeline(
                new KeyFrame(Duration.ZERO,
                        new KeyValue(card.strokeProperty(), accentColor.deriveColor(0, 1, 1, 0.65)),
                        new KeyValue(card.effectProperty(), createGlowEffect(accentColor, 25, 0.3))
                ),
                new KeyFrame(Duration.millis(400),
                        new KeyValue(card.strokeProperty(), accentColor.deriveColor(0.1, 1.2, 1.2, 0.9)),
                        new KeyValue(card.effectProperty(), createGlowEffect(accentColor, 38, 0.6))
                ),
                new KeyFrame(Duration.millis(800),
                        new KeyValue(card.strokeProperty(), accentColor.deriveColor(0, 1, 1, 0.65)),
                        new KeyValue(card.effectProperty(), createGlowEffect(accentColor, 25, 0.3))
                )
        );
        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
        cursorPulseAnimations.put(button, timeline);
    }

    private DropShadow createGlowEffect(Color color, double radius, double opacity) {
        DropShadow glow = new DropShadow();
        glow.setColor(color.deriveColor(0, 1, 1, opacity));
        glow.setRadius(radius);
        glow.setSpread(0.25);
        glow.setOffsetX(0);
        glow.setOffsetY(0);
        return glow;
    }

    private void stopButtonPulse(StackPane button) {
        Timeline timeline = cursorPulseAnimations.remove(button);
        if (timeline != null) timeline.stop();
        restoreOriginalCardAppearance(button);
    }

    private void restoreOriginalCardAppearance(StackPane button) {
        Rectangle card = findCardRectangle(button);
        if (card == null) return;

        Object[] data = (Object[]) button.getUserData();
        if (data == null || data.length < 3) return;

        Color accent = (Color) data[0];
        Color originalStroke = (Color) data[1];
        Effect originalEffect = (Effect) data[2];

        card.setStroke(originalStroke != null ? originalStroke : accent.deriveColor(0, 1, 1, 0.65));

        if (originalEffect != null) {
            card.setEffect(originalEffect);
        } else {
            DropShadow originalShadow = new DropShadow();
            originalShadow.setColor(accent.deriveColor(0, 1, 1, 0.35));
            originalShadow.setRadius(28);
            originalShadow.setOffsetX(0);
            originalShadow.setOffsetY(7);
            card.setEffect(originalShadow);
        }
    }

    private void pauseButtonPulse(StackPane button) {
        Timeline timeline = cursorPulseAnimations.get(button);
        if (timeline != null) timeline.pause();
    }

    private void resumeButtonPulse(StackPane button) {
        Timeline timeline = cursorPulseAnimations.get(button);
        if (timeline != null) timeline.play();
    }
}