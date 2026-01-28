package com.example.codeplayfx.engine.api;

import com.example.codeplayfx.world.GameWorld;
import com.example.codeplayfx.world.entities.Bot;
import com.example.codeplayfx.world.entities.Zombie;
import com.example.codeplayfx.world.base.GameObject;  // IMPORT ADDED
import java.util.List;

public class UserGameAPI {
    private GameWorld world;

    public UserGameAPI() {
        this.world = GameWorld.getInstance();
        System.out.println("UserGameAPI initialized");
    }

    // ===== ENTITY CREATION =====

    public Bot createBot(double x, double y, String name) {
        Bot bot = new Bot(x, y, name);
        // Note: Bot constructor automatically adds itself to GameWorld
        System.out.println("Created Bot: " + name + " at (" + x + ", " + y + ")");
        return bot;
    }

    public Bot createBot() {
        return createBot(100, 100, "DefaultBot");
    }

    public Zombie createZombie(double x, double y) {
        Zombie zombie = new Zombie(x, y);
        // Note: Zombie constructor automatically adds itself to GameWorld
        System.out.println("Created Zombie at (" + x + ", " + y + ")");
        return zombie;
    }

    public Zombie createZombie() {
        return createZombie(300, 100);
    }

    // ===== SIMPLE MOVEMENT HELPERS =====

    public void moveRight(Bot bot) {
        bot.moveRight();
        System.out.println("Bot " + getBotName(bot) + " moved right");
    }

    public void moveLeft(Bot bot) {
        bot.moveLeft();
        System.out.println("Bot " + getBotName(bot) + " moved left");
    }

    public void moveRight(Bot bot, double distance) {
        bot.moveRight(distance);
        System.out.println("Bot " + getBotName(bot) + " moved right by " + distance);
    }

    public void moveLeft(Bot bot, double distance) {
        bot.moveLeft(distance);
        System.out.println("Bot " + getBotName(bot) + " moved left by " + distance);
    }

    public void startMovingRight(Bot bot) {
        bot.startMovingRight();
        System.out.println("Bot " + getBotName(bot) + " started moving right");
    }

    public void startMovingLeft(Bot bot) {
        bot.startMovingLeft();
        System.out.println("Bot " + getBotName(bot) + " started moving left");
    }

    public void stopMoving(Bot bot) {
        bot.stopMoving();
        System.out.println("Bot " + getBotName(bot) + " stopped moving");
    }

    // Helper method to safely get bot name
    private String getBotName(Bot bot) {
        try {
            return bot.getPlayerName();
        } catch (Exception e) {
            return "Unknown";
        }
    }

    // ===== ZOMBIE CONTROLS =====

    public void setZombieSpeed(Zombie zombie, double speed) {
        zombie.setSpeed(speed);
        System.out.println("Zombie speed set to " + speed);
    }

    // ===== UTILITY METHODS =====

    public void log(String message) {
        System.out.println("[USER] " + message);
    }

    public void sleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (InterruptedException e) {
            // Ignore
        }
    }

    public void waitFor(long milliseconds) {
        sleep(milliseconds);
    }

    // ===== GAME WORLD ACCESS =====

    public GameWorld getWorld() {
        return world;
    }

    public void clearWorld() {
        world.clear();
        log("World cleared");
    }

    public void printWorldInfo() {
        int count = world.getObjectCount();
        log("World has " + count + " objects");

        List<GameObject> objects = world.getObjects();
        for (GameObject obj : objects) {
            String className = obj.getClass().getSimpleName();
            double x = obj.getX();  // This works now!
            double y = obj.getY();  // This works now!

            if (obj instanceof Bot) {
                Bot bot = (Bot) obj;
                String name = bot.getPlayerName();
                log("  - Bot: " + name + " at (" + Math.round(x) + ", " + Math.round(y) + ")");
            } else if (obj instanceof Zombie) {
                log("  - Zombie at (" + Math.round(x) + ", " + Math.round(y) + ")");
            } else {
                log("  - " + className + " at (" + Math.round(x) + ", " + Math.round(y) + ")");
            }
        }
    }

    // ===== GAME CONTROL =====

    public void pauseGame() {
        log("Game paused (placeholder)");
    }

    public void resumeGame() {
        log("Game resumed (placeholder)");
    }

    public void restartGame() {
        clearWorld();
        log("Game restarted");
    }

    // ===== BATCH OPERATIONS =====

    public List<Bot> createBots(int count, double startX, double startY, String baseName) {
        List<Bot> bots = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Bot bot = createBot(startX + (i * 50), startY, baseName + (i + 1));
            bots.add(bot);
        }
        return bots;
    }

    public List<Zombie> createZombies(int count, double startX, double startY) {
        List<Zombie> zombies = new java.util.ArrayList<>();
        for (int i = 0; i < count; i++) {
            Zombie zombie = createZombie(startX + (i * 60), startY);
            zombies.add(zombie);
        }
        return zombies;
    }
}