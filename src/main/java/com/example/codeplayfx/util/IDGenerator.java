package com.example.codeplayfx.util;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Generates unique IDs for game entities.
 * Thread-safe implementation using atomic counter.
 */
public class IDGenerator {

    private static final AtomicLong counter = new AtomicLong(0);

    /**
     * Generate a new unique ID
     * @return unique long ID
     */
    public static long generateID() {
        return counter.incrementAndGet();
    }

    /**
     * Reset the counter (useful for testing)
     */
    public static void reset() {
        counter.set(0);
    }

    /**
     * Get the current counter value without incrementing
     */
    public static long getCurrentCount() {
        return counter.get();
    }
}