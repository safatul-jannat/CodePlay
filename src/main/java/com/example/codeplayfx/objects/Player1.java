package com.example.codeplayfx.objects;



import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class Player1 extends GameObject1 {
    public Player1(double x, double y, String id) {
        super(x, y, 50, 70, Color.BLUE, id);
    }

    @Override
    public void draw(GraphicsContext gc) {
        // Draw blue rectangle
        gc.setFill(color);
        gc.fillRect(x, y, width, height);

        // Draw player ID
        gc.setFill(Color.WHITE);
        gc.fillText(id, x + 5, y + 15);
    }
}
