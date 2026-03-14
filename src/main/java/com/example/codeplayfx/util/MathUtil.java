package com.example.codeplayfx.util;

/**
 * Math utilities for game calculations.
 * Provides common operations like clamping, lerping, and distance calculations.
 */
public class MathUtil {

    public static final float EPSILON = 0.0001f;

    /**
     * Clamp a value between min and max
     */
    public static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Clamp an integer value between min and max
     */
    public static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Linear interpolation between two values
     * @param a start value
     * @param b end value
     * @param t interpolation factor (0 to 1)
     */
    public static float lerp(float a, float b, float t) {
        return a + (b - a) * clamp(t, 0f, 1f);
    }

    /**
     * Check if two floats are approximately equal
     */
    public static boolean approximately(float a, float b) {
        return Math.abs(a - b) < EPSILON;
    }

    /**
     * Calculate distance between two points
     */
    public static float distance(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    /**
     * Calculate squared distance (faster, no sqrt)
     */
    public static float distanceSquared(float x1, float y1, float x2, float y2) {
        float dx = x2 - x1;
        float dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    /**
     * Map a value from one range to another
     */
    public static float map(float value, float inMin, float inMax, float outMin, float outMax) {
        return outMin + (outMax - outMin) * ((value - inMin) / (inMax - inMin));
    }

    /**
     * Convert degrees to radians
     */
    public static float toRadians(float degrees) {
        return (float) Math.toRadians(degrees);
    }

    /**
     * Convert radians to degrees
     */
    public static float toDegrees(float radians) {
        return (float) Math.toDegrees(radians);
    }
}
