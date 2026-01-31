// Background.java in environment package (should already exist)
package com.example.codeplayfx.world.environment;

import com.example.codeplayfx.world.base.GameObject;

public abstract class Background extends GameObject {
    protected String image;

    public Background() {
        super();
    }

    public Background(String image) {
        super();
        this.image = image;
    }

    @Override
    public void update() {
        // Background-specific update logic
    }

    @Override
    public abstract void render();

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }
}