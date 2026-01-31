package com.example.codeplayfx.engine.render;

import com.example.codeplayfx.world.base.GameObject;
import javafx.scene.canvas.GraphicsContext;
import java.util.*;

public class RendererManager {
    private List<BaseRenderer> renderers;
    private GraphicsContext gc;

    public RendererManager(GraphicsContext gc) {
        this.gc = gc;
        this.renderers = new ArrayList<>();
        initializeRenderers();
    }

    private void initializeRenderers() {
        // Add renderers in order
        renderers.add(new BotRenderer(gc));
        renderers.add(new ZombieRenderer(gc));
    }

    /**
     * Get appropriate renderer for an object
     */
    public BaseRenderer getRenderer(GameObject obj) {
        // Find first renderer that can render this object
        for (BaseRenderer renderer : renderers) {
            if (renderer.canRender(obj)) {
                return renderer;
            }
        }
        return null;
    }

    /**
     * Render a single object
     */
    public void render(GameObject obj) {
        BaseRenderer renderer = getRenderer(obj);
        if (renderer != null) {
            renderer.render(obj);
        } else {
            // Fallback rendering
            gc.setFill(javafx.scene.paint.Color.PURPLE);
            gc.fillRect(obj.getX(), obj.getY(), 30, 30);
            gc.setFill(javafx.scene.paint.Color.WHITE);
            gc.fillText(obj.getClass().getSimpleName(), obj.getX(), obj.getY() - 5);
        }
    }
}