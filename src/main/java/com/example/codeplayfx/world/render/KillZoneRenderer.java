package com.example.codeplayfx.world.render;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import com.example.codeplayfx.world.component.RenderComponent;
import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.KillZone;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;

public class KillZoneRenderer extends Renderer {

    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;

        Vector2 screenPos = coordinateSystem.toScreen(entity.getPosition());
        double gridScale = coordinateSystem.getGridScale();

        double width = gridScale;
        double height = gridScale;

        if (entity instanceof KillZone) {
            KillZone killZone = (KillZone) entity;
            width = killZone.getBlockWidth() * gridScale;
            height = killZone.getBlockHeight() * gridScale;
        }

        RenderComponent renderComp = entity.getComponent(RenderComponent.class);
        Color fillColor = (renderComp != null) ? renderComp.getFillColor() : Color.RED;

        // Draw the hazard
        gc.setFill(fillColor);
        gc.fillRect(screenPos.x, screenPos.y, width, height);

        // Draw border
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(1);
        gc.strokeRect(screenPos.x, screenPos.y, width, height);

        // Draw warning pattern (diagonal lines)
        gc.setStroke(Color.YELLOW);
        gc.setLineWidth(2);
        gc.strokeLine(screenPos.x, screenPos.y, screenPos.x + width, screenPos.y + height);
        gc.strokeLine(screenPos.x + width, screenPos.y, screenPos.x, screenPos.y + height);

        renderDebugInfo(entity, gc, coordinateSystem);
    }
}