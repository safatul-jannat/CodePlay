package com.example.codeplayfx.world.entities;

public class Zombie extends Enemy {

    public Zombie() {
        super();
        this.speed = 0.5; // Slower than bot
    }

    public Zombie(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    @Override
    public void update() {
        super.update(); // Call parent update for movement
        autoMove();
    }

    @Override
    public void render() {
        // Rendering handled by ZombieRenderer
        System.out.println("[Zombie] at (" + x + ", " + y + ")");
    }

    @Override
    public void autoMove() {
        // Simple AI: always move left slowly
        startMovingLeft();
    }
}