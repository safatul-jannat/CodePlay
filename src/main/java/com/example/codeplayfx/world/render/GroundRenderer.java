package com.example.codeplayfx.world.render;

import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.Ground;
import com.example.codeplayfx.world.entity.Ground.GroundType;
import com.example.codeplayfx.world.entity.StaticEntity;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class GroundRenderer extends StaticEntityRenderer {

    // Brick wall colors
    private static final Color BRICK_COLOR = Color.web("#B22222");
    private static final Color MORTAR_COLOR = Color.web("#8B7D6B");
    private static final Color BRICK_DARK = Color.web("#8B1A1A");
    private static final int BRICK_WIDTH = 40;   // Pixels per brick
    private static final int BRICK_HEIGHT = 20;  // Pixels per brick row
    private static final int MORTAR_SIZE = 2;    // Pixels for mortar


    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;
        if (!(entity instanceof Ground)) {
            super.render(entity, gc, coordinateSystem);
            return;
        }

        Ground ground = (Ground) entity;

        switch (ground.getGroundType()) {
            case WALL:
                renderBrickWall(ground, gc, coordinateSystem);
                break;
            case GROUND:
                renderGrassGround(ground, gc, coordinateSystem);
                break;
            case PLATFORM:
                renderPlatform(ground, gc, coordinateSystem);
                break;
            default:
                super.render(entity, gc, coordinateSystem);
                break;
        }
    }

    private void renderBrickWall(Ground ground, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(ground.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        double screenW = ground.getWidth() * gridScale;
        double screenH = ground.getHeight() * gridScale;
        float x = (float) screenPos.x;
        float y = (float) screenPos.y;
        float width = (float) screenW;
        float height = (float) screenH;

        gc.save();
        gc.beginPath();
        gc.rect(x, y, width, height);
        gc.clip();

        // Mortar background
        gc.setFill(MORTAR_COLOR);
        gc.fillRect(x, y, width, height);

        int rows = (int) Math.ceil(height / BRICK_HEIGHT) + 1;

        for (int row = 0; row < rows; row++) {
            boolean evenRow = (row % 2 == 0);

            // Start X: even rows align with left edge, odd rows shift left by half brick
            float startX = evenRow ? x : x - (BRICK_WIDTH / 2.0f);
            int cols = (int) Math.ceil((width + (evenRow ? 0 : BRICK_WIDTH / 2.0f)) / BRICK_WIDTH) + 1;

            for (int col = 0; col < cols; col++) {
                float brickX = startX + (col * BRICK_WIDTH);
                float brickY = y + (row * BRICK_HEIGHT);
                float brickW = BRICK_WIDTH - MORTAR_SIZE;
                float brickH = BRICK_HEIGHT - MORTAR_SIZE;

                if (brickX + brickW < x || brickX > x + width) continue;
                if (brickY + brickH < y || brickY > y + height) continue;

                float actualX = Math.max(x, brickX);
                float actualY = Math.max(y, brickY);
                float actualW = Math.min(brickX + brickW, x + width) - actualX;
                float actualH = Math.min(brickY + brickH, y + height) - actualY;

                if (actualW <= 0 || actualH <= 0) continue;

                gc.setFill(BRICK_COLOR);
                gc.fillRect(actualX, actualY, actualW, actualH);

                gc.setFill(BRICK_DARK);
                gc.fillRect(actualX, actualY, actualW, Math.min(3, actualH));

                gc.setFill(Color.rgb(0, 0, 0, 0.3));
                gc.fillRect(actualX + actualW - 2, actualY, 2, actualH);
            }
        }

        gc.restore();
    }

    private void renderGrassGround(Ground ground, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(ground.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        double screenW = ground.getWidth() * gridScale;
        double screenH = ground.getHeight() * gridScale;
        float x = (float) screenPos.x;
        float y = (float) screenPos.y;
        float width = (float) screenW;
        float height = (float) screenH;

        // Grass takes 25% of height (or minimum 20 pixels)
        float grassHeightPx = (float) Math.max(20, height * 0.25f);

        // Colors
        Color soilMain = Color.web("#6B4226");
        Color soilDark = Color.web("#4A2E1A");
        Color grassMain = Color.web("#3C8D40");
        Color grassDark = Color.web("#2D6A30");
        Color grassTip = Color.web("#4CAF50");

        // Draw soil (bottom part)
        gc.setFill(soilMain);
        gc.fillRect(x, y + grassHeightPx, width, height - grassHeightPx);

        // Draw stable soil texture (grid pattern, not random)
        gc.setFill(soilDark);
        for (int row = 0; row < (height - grassHeightPx) / 8; row++) {
            for (int col = 0; col < width / 10; col++) {
                float spotX = x + (col * 10) + (row % 2 == 0 ? 5 : 0);
                float spotY = y + grassHeightPx + (row * 8);
                if (spotX < x + width && spotY < y + height) {
                    gc.fillRect(spotX, spotY, 2, 2);
                }
            }
        }

        // Draw grass base
        gc.setFill(grassDark);
        gc.fillRect(x, y, width, grassHeightPx);

        gc.setFill(grassMain);
        gc.fillRect(x, y, width, grassHeightPx - 3);

        gc.setFill(grassTip);
        gc.fillRect(x, y + grassHeightPx - 4, width, 2);

        // Draw stable grass blades (based on position, not random)
        gc.setStroke(grassDark);
        gc.setLineWidth(1.5);
        for (int i = 0; i < width / 4; i++) {
            float bladeX = x + (i * 4);
            float bladeHeight = 4 + ((i * 3) % 6);  // Pattern based on i

            gc.strokeLine(bladeX, y + grassHeightPx - 2,
                    bladeX - 3, y + grassHeightPx - bladeHeight);
            gc.strokeLine(bladeX, y + grassHeightPx - 2,
                    bladeX + 3, y + grassHeightPx - bladeHeight);
            gc.strokeLine(bladeX, y + grassHeightPx - 2,
                    bladeX, y + grassHeightPx - bladeHeight - 2);
        }

        // Shadow at soil line
        gc.setFill(Color.rgb(0, 0, 0, 0.15));
        gc.fillRect(x, y + grassHeightPx - 2, width, 4);


    }
    private void renderPlatform(Ground ground, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        Vector2 screenPos = coordinateSystem.toScreen(ground.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        double screenW = ground.getWidth() * gridScale;
        double screenH = ground.getHeight() * gridScale;
        float x = (float) screenPos.x;
        float y = (float) screenPos.y;
        float width = (float) screenW;
        float height = (float) screenH;

        // Wooden platform colors
        Color platformColor = Color.web("#CD853F");     // Peru (wood)
        Color platformDark = Color.web("#8B5A2B");      // Dark wood
        Color platformLight = Color.web("#DEB887");     // Light wood highlight

        // Main body
        gc.setFill(platformColor);
        gc.fillRect(x, y, width, height);

        // Top highlight (thin bright line)
        gc.setFill(platformLight);
        gc.fillRect(x, y, width, 2);

        // Bottom shadow (thin dark line)
        gc.setFill(platformDark);
        gc.fillRect(x, y + height - 2, width, 2);

        // Wood grain lines (horizontal)
        gc.setStroke(platformDark);
        gc.setLineWidth(0.5);
        for (int i = 1; i < height / 4; i++) {
            float lineY = y + (i * 4);
            if (lineY < y + height - 2) {
                gc.strokeLine(x + 2, lineY, x + width - 2, lineY);
            }
        }


    }
}