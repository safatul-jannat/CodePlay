package com.example.codeplayfx.world.render;

import com.example.codeplayfx.world.entity.Entity;
import com.example.codeplayfx.world.entity.MessageBoard;
import com.example.codeplayfx.world.physics.CoordinateSystem;
import com.example.codeplayfx.world.physics.Vector2;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

/**
 * Renderer for MessageBoard entities
 */
public class MessageBoardRenderer extends Renderer {
    
    @Override
    public void render(Entity entity, GraphicsContext gc, CoordinateSystem coordinateSystem) {
        if (!entity.isVisible()) return;
        if (!(entity instanceof MessageBoard)) return;
        
        MessageBoard board = (MessageBoard) entity;
        if (!board.isShowing()) return;
        
        // Get screen position
        Vector2 screenPos = coordinateSystem.toScreen(board.getPosition());
        double gridScale = coordinateSystem.getGridScale();
        
        // Calculate dimensions in pixels
        double pixelWidth = board.getWidth() * gridScale;
        double pixelHeight = board.getHeight() * gridScale;
        double pixelPadding = board.getPadding() * gridScale;
        
        float x = (float) screenPos.x;
        float y = (float) screenPos.y;
        
        // Save graphics state
        gc.save();
        
        // Draw background
        gc.setFill(board.getBackgroundColor());
        gc.fillRoundRect(x, y, pixelWidth, pixelHeight, 10, 10);
        
        // Draw border
        gc.setStroke(Color.rgb(255, 255, 255, 0.3));
        gc.setLineWidth(2);
        gc.strokeRoundRect(x, y, pixelWidth, pixelHeight, 10, 10);
        
        // Draw text
        gc.setFill(board.getTextColor());
        gc.setFont(Font.font("Arial", board.getFontSize()));
        gc.setTextAlign(TextAlignment.CENTER);
        
        // Wrap text if needed
        String text = board.getMessage();
        double maxTextWidth = pixelWidth - (pixelPadding * 2);
        
        // Simple text wrapping
        String[] lines = wrapText(text, maxTextWidth, gc);
        
        // Calculate text position (centered)
        double lineHeight = board.getFontSize() + 4;
        double totalHeight = lines.length * lineHeight;
        double startY = y + (pixelHeight - totalHeight) / 2 + board.getFontSize();
        
        // Draw each line
        for (int i = 0; i < lines.length; i++) {
            double lineY = startY + (i * lineHeight);
            gc.fillText(lines[i], x + pixelWidth / 2, lineY);
        }
        
        // Restore graphics state
        gc.restore();
    }
    
    /**
     * Simple text wrapping
     */
    private String[] wrapText(String text, double maxWidth, GraphicsContext gc) {
        if (text == null || text.isEmpty()) {
            return new String[]{""};
        }
        
        String[] words = text.split(" ");
        java.util.ArrayList<String> lines = new java.util.ArrayList<>();
        StringBuilder currentLine = new StringBuilder();
        
        for (String word : words) {
            String testLine = currentLine.length() == 0 ? word : currentLine + " " + word;
            double textWidth = gc.getFont().getSize() * testLine.length() * 0.6; // Approximate width
            
            if (textWidth <= maxWidth) {
                if (currentLine.length() == 0) {
                    currentLine.append(word);
                } else {
                    currentLine.append(" ").append(word);
                }
            } else {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder(word);
                } else {
                    // Word is too long, force split
                    lines.add(word);
                    currentLine = new StringBuilder();
                }
            }
        }
        
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }
        
        return lines.toArray(new String[0]);
    }
}