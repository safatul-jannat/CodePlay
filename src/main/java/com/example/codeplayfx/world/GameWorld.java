// GameWorld.java (FIXED)
package com.example.codeplayfx.world;

import com.example.codeplayfx.world.base.GameObject;
import com.example.codeplayfx.world.environment.Background;  // Correct import
import java.util.ArrayList;
import java.util.List;

public class GameWorld {
    private List<GameObject> objects;
    private Background background;  // Uses environment.Background

    public GameWorld() {
        this.objects = new ArrayList<>();
    }

    public void addObject(GameObject obj) {
        objects.add(obj);
    }

    public void removeObject(GameObject obj) {
        objects.remove(obj);
    }

    public void updateAll() {
        objects.removeIf(obj -> !obj.isActive());

        // Update background if it exists
        if (background != null) {
            background.update();
        }

        // Update all active objects
        for (GameObject obj : objects) {
            obj.update();
        }
    }

    public void renderAll() {
        // Render background first
        if (background != null) {
            background.render();
        }

        // Render all objects
        for (GameObject obj : objects) {
            obj.render();
        }
    }

    public Background getBackground() { return background; }
    public void setBackground(Background background) { this.background = background; }
    public List<GameObject> getObjects() { return new ArrayList<>(objects); }
}