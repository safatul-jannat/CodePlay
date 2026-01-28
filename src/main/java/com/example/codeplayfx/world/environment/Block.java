// Block.java
package com.example.codeplayfx.world.environment;

import com.example.codeplayfx.world.base.GameObject;
import com.example.codeplayfx.world.base.Collidable;
import com.example.codeplayfx.world.base.Rect;

public class Block extends GameObject implements Collidable {
    protected String type;
    protected int width = 32;
    protected int height = 32;

    public Block() {
        super();
        this.type = "generic";
    }

    public Block(double x, double y, String type) {
        super();
        this.x = x;
        this.y = y;
        this.type = type;
    }

    @Override
    public void update() {}

    @Override
    public void render() {
        System.out.println("Rendering " + type + " block at (" + x + ", " + y + ")");
    }

    @Override
    public Rect getBounds() {
        return new Rect(x, y, width, height);
    }

    @Override
    public void onCollision(GameObject other) {
        System.out.println(type + " block collided with " + other.getClass().getSimpleName());
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
}