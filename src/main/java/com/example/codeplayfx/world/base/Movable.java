// Movable.java
package com.example.codeplayfx.world.base;

public interface Movable {
    void moveLeft();
    void moveRight();

    void moveLeft(double distance);
    void moveRight(double distance);

    // Overloaded: Move from start to end
    void moveLeft(double startX, double endX);
    void moveRight(double startX, double endX);

    // Move continuously
    void startMovingLeft();
    void startMovingRight();
    void stopMoving();

//    void moveUp();
//    void moveDown();
}