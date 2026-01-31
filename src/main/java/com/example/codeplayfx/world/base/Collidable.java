// Collidable.java
package com.example.codeplayfx.world.base;

public interface Collidable {
    Rect getBounds();
    void onCollision(GameObject other);
}