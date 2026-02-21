package com.example.codeplayfx.engine.core;

/**
 * Time utility for tracking game time and delta time.
 * Helps maintain consistent frame rates and physics.
 */
public class Time {
    
    private long lastFrameTime;      // Time of last frame in nanoseconds
    private float deltaTime;         // Time since last frame in seconds
    private float fixedDeltaTime;    // Fixed time step for physics
    private int frameCount;          // Total frames rendered
    private float totalTime;         // Total time elapsed
    
    // FPS tracking
    private int fps;
    private int framesSinceLastSecond;
    private long lastFpsUpdate;
    
    /**
     * Create time tracker with target FPS
     */
    public Time() {
        this.lastFrameTime = System.nanoTime();
        this.deltaTime = 0f;
        this.fixedDeltaTime = 1f / 60f;  // 60 FPS
        this.frameCount = 0;
        this.totalTime = 0f;
        this.fps = 0;
        this.framesSinceLastSecond = 0;
        this.lastFpsUpdate = System.nanoTime();
    }
    
    /**
     * Update time (call once per frame)
     */
    public void update() {
        long currentTime = System.nanoTime();
        long elapsed = currentTime - lastFrameTime;
        
        // Convert nanoseconds to seconds
        deltaTime = elapsed / 1_000_000_000f;
        
        // Clamp delta time to prevent huge jumps
        deltaTime = Math.min(deltaTime, 0.1f);
        
        totalTime += deltaTime;
        frameCount++;
        
        // Update FPS
        framesSinceLastSecond++;
        if (currentTime - lastFpsUpdate >= 1_000_000_000L) {
            fps = framesSinceLastSecond;
            framesSinceLastSecond = 0;
            lastFpsUpdate = currentTime;
        }
        
        lastFrameTime = currentTime;
    }
    
    /**
     * Reset time tracking
     */
    public void reset() {
        lastFrameTime = System.nanoTime();
        deltaTime = 0f;
        frameCount = 0;
        totalTime = 0f;
        fps = 0;
        framesSinceLastSecond = 0;
        lastFpsUpdate = System.nanoTime();
    }
    
    // ========== GETTERS ==========
    
    /**
     * Get time since last frame in seconds
     */
    public float getDeltaTime() {
        return deltaTime;
    }
    
    /**
     * Get fixed delta time (for physics)
     */
    public float getFixedDeltaTime() {
        return fixedDeltaTime;
    }
    
    /**
     * Get total time elapsed since start
     */
    public float getTotalTime() {
        return totalTime;
    }
    
    /**
     * Get total frame count
     */
    public int getFrameCount() {
        return frameCount;
    }
    
    /**
     * Get current FPS
     */
    public int getFPS() {
        return fps;
    }
    
    /**
     * Set fixed delta time
     */
    public void setFixedDeltaTime(float fixedDeltaTime) {
        this.fixedDeltaTime = fixedDeltaTime;
    }
}