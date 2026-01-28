// Sword.java
package com.example.codeplayfx.world.items;

public class Sword extends Weapon {
    private int durability;
    private int maxDurability;

    public Sword() {
        super();
        this.name = "Sword";
        this.damage = 25;
        this.durability = 100;
        this.maxDurability = 100;
    }

    public Sword(String name, int damage, int maxDurability) {
        super(name, damage);
        this.durability = maxDurability;
        this.maxDurability = maxDurability;
    }

    @Override
    public void update() {
        if (durability < 30) {
            System.out.println(name + " is about to break!");
        }
    }

    @Override
    public void render() {
        System.out.println("Rendering " + name + " at (" + x + ", " + y + ")");
        System.out.println("Damage: " + damage + ", Durability: " + durability + "/" + maxDurability);
    }

    @Override
    public void use() {
        if (durability > 0) {
            System.out.println(name + " slashes for " + damage + " damage!");
            durability -= 5;
            if (durability <= 0) {
                durability = 0;
                System.out.println(name + " broke!");
            }
        } else {
            System.out.println(name + " is broken and cannot be used!");
        }
    }

    public void repair() {
        durability = maxDurability;
        System.out.println(name + " has been repaired!");
    }

    public int getDurability() { return durability; }
    public int getMaxDurability() { return maxDurability; }
    public void setDurability(int durability) { this.durability = durability; }
}