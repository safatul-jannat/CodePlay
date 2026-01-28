// StaticBackground.java
package com.example.codeplayfx.world.environment;

public class StaticBackground extends Background {

    public StaticBackground() {
        super();
    }

    public StaticBackground(String image) {
        super(image);
    }

    @Override
    public void render() {
        System.out.println("Rendering static background with image: " + image);
    }
}