// Enemy.java
package com.example.codeplayfx.world.entities;

public abstract class Enemy extends Entity {
    public Enemy() {
        super();
        this.speed = 1.5;
    }

    public abstract void autoMove();
}