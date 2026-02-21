package com.example.codeplayfx.engine.core;

/**
 * Configuration settings for the game engine.
 * Stores target FPS, physics settings, and other engine parameters.
 */
public class EngineConfig {
    
    // Frame rate settings
    private int targetFPS;
    private long targetFrameTimeNanos;
    
    // Physics settings
    private boolean enablePhysics;
    private int physicsUpdatesPerFrame;
    
    // Rendering settings
    private boolean enableVSync;
    
    // Debug settings
    private boolean debugMode;
    private boolean showFPS;
    
    /**
     * Create default engine configuration
     */
    public EngineConfig() {
        this.targetFPS = 60;
        this.targetFrameTimeNanos = 1_000_000_000L / targetFPS;
        this.enablePhysics = true;
        this.physicsUpdatesPerFrame = 1;
        this.enableVSync = false;
        this.debugMode = false;
        this.showFPS = false;
    }
    
    // ========== FRAME RATE ==========
    
    public int getTargetFPS() {
        return targetFPS;
    }
    
    public void setTargetFPS(int targetFPS) {
        this.targetFPS = Math.max(1, Math.min(240, targetFPS));
        this.targetFrameTimeNanos = 1_000_000_000L / this.targetFPS;
    }
    
    public long getTargetFrameTimeNanos() {
        return targetFrameTimeNanos;
    }
    
    // ========== PHYSICS ==========
    
    public boolean isPhysicsEnabled() {
        return enablePhysics;
    }
    
    public void setEnablePhysics(boolean enablePhysics) {
        this.enablePhysics = enablePhysics;
    }
    
    public int getPhysicsUpdatesPerFrame() {
        return physicsUpdatesPerFrame;
    }
    
    public void setPhysicsUpdatesPerFrame(int updates) {
        this.physicsUpdatesPerFrame = Math.max(1, updates);
    }
    
    // ========== RENDERING ==========
    
    public boolean isVSyncEnabled() {
        return enableVSync;
    }
    
    public void setEnableVSync(boolean enableVSync) {
        this.enableVSync = enableVSync;
    }
    
    // ========== DEBUG ==========
    
    public boolean isDebugMode() {
        return debugMode;
    }
    
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
    public boolean isShowFPS() {
        return showFPS;
    }
    
    public void setShowFPS(boolean showFPS) {
        this.showFPS = showFPS;
    }
    
    @Override
    public String toString() {
        return String.format("EngineConfig[fps=%d, physics=%b, vsync=%b, debug=%b]",
            targetFPS, enablePhysics, enableVSync, debugMode);
    }
}