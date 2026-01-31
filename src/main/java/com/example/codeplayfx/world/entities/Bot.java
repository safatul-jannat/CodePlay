package com.example.codeplayfx.world.entities;

public class Bot extends Player {

    public Bot() {
        super();
        this.setPlayerName("Bot");
        this.speed = 2.0;
    }

    public Bot(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Bot(double x, double y, String name) {
        this(x, y);
        this.setPlayerName(name);
    }

    @Override
    public void update() {
        super.update(); // Call parent update for movement
        // Bot-specific logic can go here
    }

    @Override
    public void render() {
        // Rendering handled by BotRenderer
        System.out.println("[Bot] " + getPlayerName() + " at (" + x + ", " + y + ")");
    }
}