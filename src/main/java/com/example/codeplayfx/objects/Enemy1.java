package com.example.codeplayfx.objects;



import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Enemy1 extends GameObject1 {
    public Enemy1(double x, double y, String id) {
        super(x, y, 50, 50, Color.RED, id);
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Draw red rectangle
        gc.setFill(color);
        gc.fillRect(x, y, width, height);

        // Draw enemy ID
        gc.setFill(Color.WHITE);
        gc.fillText(id, x + 5, y + 15);
    }
}
