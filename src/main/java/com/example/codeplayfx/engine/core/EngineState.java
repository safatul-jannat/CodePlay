package com.example.codeplayfx.engine.core;

/**
 * Represents the current state of the game engine.
 */
public enum EngineState {
    
    /**
     * Engine is not yet initialized
     */
    UNINITIALIZED,
    
    /**
     * Engine is initializing
     */
    INITIALIZING,
    
    /**
     * Engine is ready but not running
     */
    READY,
    
    /**
     * Engine is actively running the game loop
     */
    RUNNING,
    
    /**
     * Engine is paused (can be resumed)
     */
    PAUSED,
    
    /**
     * Engine is stopping
     */
    STOPPING,
    
    /**
     * Engine has been stopped
     */
    STOPPED,
    
    /**
     * Engine encountered an error
     */
    ERROR;
    
    /**
     * Check if engine can transition to running state
     */
    public boolean canStart() {
        return this == READY || this == PAUSED || this == STOPPED;
    }
    
    /**
     * Check if engine can be paused
     */
    public boolean canPause() {
        return this == RUNNING;
    }
    
    /**
     * Check if engine can be stopped
     */
    public boolean canStop() {
        return this == RUNNING || this == PAUSED;
    }
    
    /**
     * Check if engine is actively running
     */
    public boolean isRunning() {
        return this == RUNNING;
    }
}