// Item.java
package com.example.codeplayfx.world.items;

import com.example.codeplayfx.world.base.GameObject;

public abstract class Item extends GameObject {
    protected String name;
    protected boolean collectible;

    public Item() {
        super();
        this.collectible = true;
    }

    public Item(double x, double y, String name) {
        super();
        this.x = x;
        this.y = y;
        this.name = name;
        this.collectible = true;
    }

    @Override
    public abstract void update();

    @Override
    public abstract void render();

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public boolean isCollectible() { return collectible; }
    public void setCollectible(boolean collectible) { this.collectible = collectible; }
}