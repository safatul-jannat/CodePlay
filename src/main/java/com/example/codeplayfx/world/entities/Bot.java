// Bot.java
package com.example.codeplayfx.world.entities;

public class Bot extends Player {
    public enum BotType {
        EASY, MEDIUM, HARD
    }

    private BotType type;

    public Bot() {
        super();
        this.type = BotType.MEDIUM;
    }

    public Bot(double x, double y) {
        this();
        this.x = x;
        this.y = y;
    }

    public Bot(double x, double y, BotType type) {
        this(x, y);
        this.type = type;
    }

    @Override
    public void update() {
        switch(type) {
            case EASY:
                moveLeft();
                break;
            case MEDIUM:
                moveRight();
                break;
            case HARD:
                run();
                break;
        }
    }

    @Override
    public void render() {
        System.out.println("Rendering Bot at (" + x + ", " + y + ") with type: " + type);
    }

    public BotType getType() { return type; }
    public void setType(BotType type) { this.type = type; }
}