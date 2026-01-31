package com.example.codeplayfx.world;

import com.example.codeplayfx.world.base.GameObject;
import com.example.codeplayfx.world.entities.Bot;
import com.example.codeplayfx.world.entities.Zombie;
import com.example.codeplayfx.world.environment.Background;
import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private static GameWorld instance;
    private List<GameObject> objects;
    private Background background;

    private GameWorld() {
        this.objects = new ArrayList<>();
        System.out.println("GameWorld created");
    }

    public static GameWorld getInstance() {
        if (instance == null) {
            instance = new GameWorld();
        }
        return instance;
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
        System.out.println("Added " + obj.getClass().getSimpleName() +
                " to world at (" + obj.getX() + ", " + obj.getY() + ")");
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    public void updateAll() {
        objects.removeIf(obj -> !obj.isActive());

        if (background != null) {
            background.update();
        }

        for (GameObject obj : objects) {
            obj.update();
        }
    }

    public void renderAll() {
        if (background != null) {
            background.render();
        }

        for (GameObject obj : objects) {
            obj.render();
        }
    }

    public int getObjectCount() {
        return objects.size();
    }

    public void clear() {
        objects.clear();
        System.out.println("GameWorld cleared");
    }

    // ===== HELPER METHODS =====

    // These methods are optional since GameObject auto-adds itself
    // But keep them for UserGameAPI to use if needed
    public Bot createBot(double x, double y, String name) {
        return new Bot(x, y, name);  // Constructor auto-adds to world
    }

    public Zombie createZombie(double x, double y) {
        return new Zombie(x, y);  // Constructor auto-adds to world
    }

    public Background getBackground() { return background; }
    public void setBackground(Background background) { this.background = background; }
    public List<GameObject> getObjects() { return new ArrayList<>(objects); }
}