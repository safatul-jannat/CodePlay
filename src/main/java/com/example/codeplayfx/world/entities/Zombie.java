// Zombie.java
package com.example.codeplayfx.world.entities;

public class Zombie extends Enemy {

    @Override
    public void update() {
        autoMove();
    }

    @Override
    public void render() {
        System.out.println("Rendering Zombie at (" + x + ", " + y + ")");
    }

    @Override
    public void autoMove() {
        System.out.println("Zombie is moving automatically...");
        moveLeft();
    }
}