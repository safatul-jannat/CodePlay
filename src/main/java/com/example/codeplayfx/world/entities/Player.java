// Player.java
package com.example.codeplayfx.world.entities;

import com.example.codeplayfx.world.items.Weapon;

public abstract class Player extends Entity {
    private Weapon weapon;
    private boolean isRunning = false;
    private double normalSpeed = 2.0;
    private String playerName = "Player"; // ADDED: Name field

    public Player() {
        super();
        this.speed = normalSpeed;
    }

    // ADDED: Getter and setter for player name
    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public void run() {
        if (!isRunning) {
            isRunning = true;
            speed = normalSpeed * 1.5;
            System.out.println("Player is running...");
        }
    }

    public void walk() {
        if (isRunning) {
            isRunning = false;
            speed = normalSpeed;
            System.out.println("Player is walking...");
        }
    }

    public void jump() {
        System.out.println("Player is jumping...");
    }

    public void onHand(Weapon weapon) {
        this.weapon = weapon;
        System.out.println("Player equipped: " + weapon.getName());
    }

    public void attack() {
        if (weapon != null) {
            weapon.use();
        } else {
            System.out.println("No weapon equipped! Punching instead...");
        }
    }

    public Weapon getWeapon() { return weapon; }
    public void setWeapon(Weapon weapon) { this.weapon = weapon; }
}