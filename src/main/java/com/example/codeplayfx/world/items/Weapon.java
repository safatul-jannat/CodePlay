// Weapon.java
package com.example.codeplayfx.world.items;

public abstract class Weapon extends Item {
    protected int damage;

    public Weapon() {
        super();
        this.damage = 10;
        this.name = "Weapon";
    }

    public Weapon(String name, int damage) {
        super();
        this.name = name;
        this.damage = damage;
    }

    @Override
    public void update() {
        System.out.println(name + " weapon updating...");
    }

    @Override
    public abstract void render();

    public abstract void use();

    public int getDamage() { return damage; }
    public void setDamage(int damage) { this.damage = damage; }
}