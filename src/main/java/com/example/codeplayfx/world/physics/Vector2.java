package com.example.codeplayfx.world.physics;

import com.example.codeplayfx.util.MathUtil;

/**
 * 2D Vector class for positions, velocities, and directions.
 * Immutable by default - operations return new vectors.
 */
public class Vector2 {

    public final float x;
    public final float y;

    // Common vector constants
    public static final Vector2 ZERO = new Vector2(0, 0);
    public static final Vector2 ONE = new Vector2(1, 1);
    public static final Vector2 UP = new Vector2(0, -1);
    public static final Vector2 DOWN = new Vector2(0, 1);
    public static final Vector2 LEFT = new Vector2(-1, 0);
    public static final Vector2 RIGHT = new Vector2(1, 0);

    /**
     * Create a new vector
     */
    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Add two vectors
     */
    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    /**
     * Subtract two vectors
     */
    public Vector2 subtract(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    /**
     * Multiply vector by scalar
     */
    public Vector2 multiply(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    /**
     * Divide vector by scalar
     */
    public Vector2 divide(float scalar) {
        if (MathUtil.approximately(scalar, 0)) {
            throw new ArithmeticException("Cannot divide vector by zero");
        }
        return new Vector2(this.x / scalar, this.y / scalar);
    }

    /**
     * Calculate magnitude (length) of the vector
     */
    public float magnitude() {
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Calculate squared magnitude (faster, no sqrt)
     */
    public float magnitudeSquared() {
        return x * x + y * y;
    }

    /**
     * Get normalized vector (unit vector in same direction)
     */
    public Vector2 normalized() {
        float mag = magnitude();
        if (MathUtil.approximately(mag, 0)) {
            return ZERO;
        }
        return divide(mag);
    }

    /**
     * Calculate dot product with another vector
     */
    public float dot(Vector2 other) {
        return this.x * other.x + this.y * other.y;
    }

    /**
     * Calculate distance to another vector
     */
    public float distanceTo(Vector2 other) {
        return subtract(other).magnitude();
    }

    /**
     * Linear interpolation towards another vector
     */
    public Vector2 lerp(Vector2 target, float t) {
        return new Vector2(
                MathUtil.lerp(this.x, target.x, t),
                MathUtil.lerp(this.y, target.y, t)
        );
    }

    /**
     * Check if this vector is approximately equal to another
     */
    public boolean approximately(Vector2 other) {
        return MathUtil.approximately(this.x, other.x) &&
                MathUtil.approximately(this.y, other.y);
    }

    @Override
    public String toString() {
        return String.format("Vector2(%.2f, %.2f)", x, y);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Vector2)) return false;
        Vector2 other = (Vector2) obj;
        return Float.compare(x, other.x) == 0 &&
                Float.compare(y, other.y) == 0;
    }

    @Override
    public int hashCode() {
        return Float.hashCode(x) * 31 + Float.hashCode(y);
    }
}
