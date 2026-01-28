// ScrollingBackground.java
package com.example.codeplayfx.world.environment;

public class ScrollingBackground extends Background {
    private double scrollSpeed;
    private double scrollPosition;

    public ScrollingBackground() {
        super();
        this.scrollSpeed = 1.0;
        this.scrollPosition = 0.0;
    }

    public ScrollingBackground(String image, double scrollSpeed) {
        super(image);
        this.scrollSpeed = scrollSpeed;
        this.scrollPosition = 0.0;
    }

    @Override
    public void update() {
        scrollPosition += scrollSpeed;
        System.out.println("Scrolling background position: " + scrollPosition);
    }

    @Override
    public void render() {
        System.out.println("Rendering scrolling background with image: " + image);
        System.out.println("Scroll position: " + scrollPosition + ", Speed: " + scrollSpeed);
    }

    public double getScrollSpeed() { return scrollSpeed; }
    public void setScrollSpeed(double scrollSpeed) { this.scrollSpeed = scrollSpeed; }
    public double getScrollPosition() { return scrollPosition; }
}