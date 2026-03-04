    package com.example.codeplayfx.user;

    // === IMPORT PLAYER CLASSES ===
    import com.example.codeplayfx.world.entity.BlockPlayer;
    import com.example.codeplayfx.world.GameWorld;
    import com.example.codeplayfx.visual.colorsystem.ColorFactory;
    import com.example.codeplayfx.visual.colorsystem.BlockStyle;
    import com.example.codeplayfx.world.base.Direction;
    import com.example.codeplayfx.world.entity.Entity;

    import javafx.scene.paint.Color;
    import java.util.List;
    import java.util.ArrayList;

    // === USER GAME CLASS ===
    public class UserGame {

        // Reference to game world (injected by runner)
        private GameWorld gameWorld;

        // === USER-DEFINED FIELDS ===
        

        // === USER HELPER METHODS ===
        

        // === CONSTRUCTOR (with GameWorld injection) ===
        public UserGame(GameWorld world) {
            this.gameWorld = world;
            System.out.println("UserGame initialized with world!");
        }

        // === HELPER: SLEEP METHOD FOR USERS ===
        private void wait(int milliseconds) {
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                System.err.println("Sleep interrupted: " + e.getMessage());
            }
        }

        // === MAIN EXECUTE METHOD ===
        public void execute() {
            System.out.println("=== EXECUTING USER GAME ===");

            try {
                // === USER CODE INJECTED HERE ===
                BlockPlayer redPlayer = new BlockPlayer(3, 3, "RED");
BlockPlayer bluePlayer = new BlockPlayer(4, 3, "BLUE");


redPlayer.moveRight(1);


if (redPlayer.isCollidingWith(bluePlayer)) {
    System.out.println("Players are colliding!");
    
    
    redPlayer.setColor("PURPLE");
    bluePlayer.setColor("ORANGE");
    
    
    redPlayer.moveLeft(0.5);
    bluePlayer.moveRight(0.5);
}

gameWorld.addObject(redPlayer);
gameWorld.addObject(bluePlayer);

            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
                e.printStackTrace();
            }

            System.out.println("=== USER GAME FINISHED ===");
        }
    }
