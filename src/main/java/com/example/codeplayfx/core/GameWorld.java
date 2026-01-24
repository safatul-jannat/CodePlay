package com.example.codeplayfx.core;

import com.example.codeplayfx.objects.GameObject;
import javafx.scene.canvas.GraphicsContext;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameWorld {
    private final Map<String, GameObject> objectMap = new HashMap<>();
    private final List<GameObject> objectList = new ArrayList<>();

    public void addObject(GameObject obj) {
        if (objectMap.containsKey(obj.getId())) {
            throw new IllegalArgumentException("Object with id '" + obj.getId() + "' already exists");
        }
        objectMap.put(obj.getId(), obj);
        objectList.add(obj);
        System.out.println("✓ Added " + obj.getClass().getSimpleName() + ": " + obj.getId() +
                " at (" + obj.getX() + ", " + obj.getY() + ")");
    }

    public GameObject getObject(String id) {
        return objectMap.get(id);
    }

    public void removeObject(String id) {
        GameObject obj = objectMap.remove(id);
        if (obj != null) {
            objectList.remove(obj);
        }
    }

    public void clearAll() {
        objectMap.clear();
        objectList.clear();
        System.out.println("✓ Cleared all game objects");
    }

    public List<GameObject> getAllObjects() {
        return new ArrayList<>(objectList);
    }

    public void renderAll(GraphicsContext gc) {
        for (GameObject obj : objectList) {
            obj.draw(gc);
        }
    }

    public int getObjectCount() {
        return objectList.size();
    }
}