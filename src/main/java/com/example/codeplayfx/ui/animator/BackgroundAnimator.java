package com.example.codeplayfx.ui.animator;

import javafx.animation.*;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BackgroundAnimator - Base class for all screen animations
 * Handles common background elements: particles, grid, orbs
 */
public abstract class BackgroundAnimator {

    /* ── Root & layers ───────────────────────────────────────────────────── */
    protected final StackPane rootPane;
    protected final Pane bgPane;
    protected final Pane gridPane;
    protected final Canvas particleCanvas;
    protected final Pane orbPane;

    /* ── Particle system ─────────────────────────────────────────────────── */
    protected static final int PARTICLE_COUNT = 110;
    protected final float[] px = new float[PARTICLE_COUNT];
    protected final float[] py = new float[PARTICLE_COUNT];
    protected final float[] pvx = new float[PARTICLE_COUNT];
    protected final float[] pvy = new float[PARTICLE_COUNT];
    protected final float[] pr = new float[PARTICLE_COUNT];
    protected final float[] pa = new float[PARTICLE_COUNT];
    protected final Random rng = new Random();

    /* ── Animations to track ─────────────────────────────────────────────── */
    protected AnimationTimer particleTimer;
    protected List<Circle> orbs;
    protected boolean isInitialized = false;

    /* ════════════════════════════════════════════════════════════════════════ */
    public BackgroundAnimator(StackPane rootPane, Pane bgPane, Pane gridPane, 
                              Canvas particleCanvas, Pane orbPane) {
        this.rootPane = rootPane;
        this.bgPane = bgPane;
        this.gridPane = gridPane;
        this.particleCanvas = particleCanvas;
        this.orbPane = orbPane;
        
        setupResizeListeners();
    }

    /**
     * Initialize common background animations
     * Call this from subclass initialize()
     */
    public void initializeBackground() {
        if (isInitialized) return;
        
        buildGrid();
        buildOrbs();
        startParticles();
        isInitialized = true;
    }

    /**
     * Shutdown all animations - override in subclass to add cleanup
     */
    public void shutdown() {
        if (particleTimer != null) {
            particleTimer.stop();
        }
    }

    /* ════════════════════════════════════════════════════════════════════════
       RESIZE LISTENERS
     ═══════════════════════════════════════════════════════════════════════ */
    protected void setupResizeListeners() {
        rootPane.widthProperty().addListener((obs, oldVal, newVal) -> updateOnResize());
        rootPane.heightProperty().addListener((obs, oldVal, newVal) -> updateOnResize());
    }

    protected void updateOnResize() {
        rebuildGrid();
        repositionOrbs();
    }

    /* ════════════════════════════════════════════════════════════════════════
       GRID
     ═══════════════════════════════════════════════════════════════════════ */
    protected void buildGrid() {
        gridPane.getChildren().clear();

        double width = rootPane.getWidth() > 0 ? rootPane.getWidth() : 900;
        double height = rootPane.getHeight() > 0 ? rootPane.getHeight() : 600;

        int xLines = (int) Math.ceil(width / 25);
        int yLines = (int) Math.ceil(height / 25);

        for (int x = 0; x <= xLines; x++) {
            Line l = new Line(x * 25, 0, x * 25, height);
            l.setStroke(Color.web("#80a0ff", 0.6));
            l.setStrokeWidth(1.2);
            gridPane.getChildren().add(l);
        }
        for (int y = 0; y <= yLines; y++) {
            Line l = new Line(0, y * 25, width, y * 25);
            l.setStroke(Color.web("#80a0ff", 0.6));
            l.setStrokeWidth(1.2);
            gridPane.getChildren().add(l);
        }
    }

    protected void rebuildGrid() {
        buildGrid();
    }

    /* ════════════════════════════════════════════════════════════════════════
       ORBS
     ═══════════════════════════════════════════════════════════════════════ */
    protected void buildOrbs() {
        orbPane.getChildren().clear();

        // { x%, y%, radius, rgb-hex, duration-sec }
        double[][] specs = {
                {0.12, 0.25, 160, 0x6633ffL, 9},
                {0.82, 0.18, 120, 0x0099ffL, 11},
                {0.65, 0.75, 140, 0xff3399L, 13},
                {0.25, 0.72, 100, 0x00ccccL, 8},
                {0.50, 0.45, 80, 0x9933ffL, 7},
        };

        orbs = new ArrayList<>();

        for (double[] s : specs) {
            long hex = (long) s[3];
            Color c = Color.rgb((int)((hex>>16)&0xff), (int)((hex>>8)&0xff), (int)(hex&0xff));
            
            RadialGradient rg = new RadialGradient(0, 0, 0.5, 0.5, 0.5, true, CycleMethod.NO_CYCLE,
                    new Stop(0, c.deriveColor(0, 1, 1, 0.32)),
                    new Stop(1, c.deriveColor(0, 1, 1, 0.00)));
            
            Circle orb = new Circle(s[2], rg);
            orb.getProperties().put("xPercent", s[0]);
            orb.getProperties().put("yPercent", s[1]);

            // Scale animation
            ScaleTransition sc = new ScaleTransition(Duration.seconds(s[4]), orb);
            sc.setFromX(.8); sc.setToX(1.25);
            sc.setFromY(.8); sc.setToY(1.25);
            sc.setAutoReverse(true); sc.setCycleCount(Animation.INDEFINITE); sc.play();

            // Drift animation
            TranslateTransition tt = new TranslateTransition(Duration.seconds(s[4] * 1.4), orb);
            tt.setByX(rng.nextDouble() * 60 - 30); 
            tt.setByY(rng.nextDouble() * 40 - 20);
            tt.setAutoReverse(true); tt.setCycleCount(Animation.INDEFINITE); tt.play();

            orbPane.getChildren().add(orb);
            orbs.add(orb);
        }

        repositionOrbs();
    }

    protected void repositionOrbs() {
        double width = rootPane.getWidth();
        double height = rootPane.getHeight();

        if (width <= 0 || height <= 0 || orbs == null) return;

        for (Circle orb : orbs) {
            double xPercent = (double) orb.getProperties().getOrDefault("xPercent", 0.5);
            double yPercent = (double) orb.getProperties().getOrDefault("yPercent", 0.5);
            orb.setLayoutX(xPercent * width);
            orb.setLayoutY(yPercent * height);
        }
    }

    /* ════════════════════════════════════════════════════════════════════════
       PARTICLES
     ═══════════════════════════════════════════════════════════════════════ */
    protected void startParticles() {
        double width = rootPane.getWidth() > 0 ? rootPane.getWidth() : 900;
        double height = rootPane.getHeight() > 0 ? rootPane.getHeight() : 600;

        for (int i = 0; i < PARTICLE_COUNT; i++) {
            px[i] = rng.nextFloat() * (float) width;
            py[i] = rng.nextFloat() * (float) height;
            pvx[i] = (rng.nextFloat() - .5f) * .6f;
            pvy[i] = (rng.nextFloat() - .5f) * .6f;
            pr[i] = rng.nextFloat() * 4.5f + 1.5f;
            pa[i] = rng.nextFloat() * .5f + .1f;
        }

        particleTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                double W = particleCanvas.getWidth();
                double H = particleCanvas.getHeight();

                if (W <= 0) W = 900;
                if (H <= 0) H = 600;

                GraphicsContext gc = particleCanvas.getGraphicsContext2D();
                gc.clearRect(0, 0, W, H);

                for (int i = 0; i < PARTICLE_COUNT; i++) {
                    px[i] += pvx[i];
                    py[i] += pvy[i];

                    if (px[i] < 0) px[i] = (float) W;
                    if (px[i] > W) px[i] = 0;
                    if (py[i] < 0) py[i] = (float) H;
                    if (py[i] > H) py[i] = 0;

                    // Draw connections
                    for (int j = i + 1; j < PARTICLE_COUNT; j++) {
                        float dx = px[i] - px[j], dy = py[i] - py[j];
                        float d = (float) Math.sqrt(dx * dx + dy * dy);
                        if (d < 88) {
                            gc.setStroke(Color.rgb(100, 160, 255, (1 - d / 88.0) * .3));
                            gc.setLineWidth(.8);
                            gc.strokeLine(px[i], py[i], px[j], py[j]);
                        }
                    }

                    gc.setFill(Color.rgb(140, 200, 255, pa[i]));
                    gc.fillOval(px[i] - pr[i], py[i] - pr[i], pr[i] * 2, pr[i] * 2);
                }
            }
        };
        particleTimer.start();
    }
}