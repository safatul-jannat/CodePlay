package com.example.codeplayfx.world.entity;

import com.example.codeplayfx.scripting.runtime.UserGame;
import com.example.codeplayfx.world.physics.Vector2;
import com.example.codeplayfx.world.render.BlockPlayerRenderer;
import javafx.scene.paint.Color;

/**
 * BlockPlayer - The main character entity that users interact with.
 * Supports multiple game types through movement styles.
 */
public class BlockPlayer extends LivingEntity {

    // ========== CORE FIELDS ==========
    private float moveSpeed = 3;           // Base movement speed (backward compatibility)
    private float jumpPower = 8.0f;            // Jump force
    private float flyJumpDistance = 2.0f;      // Auto-jump height
    private boolean isAutoJumping = false;
    private float jumpStartY;
    private float jumpTargetY;
    private boolean jumpGoingUp = true;

    //  ADD THIS - Prevent multiple jump triggers
    private boolean jumpRequested = false;

    // ========== MODE-SPECIFIC SPEEDS ==========
    private float flySpeed = 3f;              // Speed for flying mode (gravity OFF)
    private float walkSpeed = 3f;              // Speed for gravity mode (gravity ON)
    private float acceleration = 15.0f;          // Acceleration rate for gravity mode

    // ========== MOVEMENT STYLE ==========
    private MovementStyle movementStyle = MovementStyle.DIRECT;

    public enum MovementStyle {
        DIRECT,      // Instant position changes (puzzle, grid-based)
        VELOCITY     // Physics-based with acceleration (platformer)
    }

    // ========== CONSTRUCTOR ==========
    public BlockPlayer(float x, float y) {
        super(x, y, 100);

        // Player-specific physics
        this.friction = 0.90f;
        this.mass = 1.0f;
        this.useGravity = true;

        // Add collider
        addCollider(1.0f, 1.0f);

        // Set up rendering
        this.renderer = new BlockPlayerRenderer();
        this.renderComponent = getRenderComponent();

        // Default appearance
        renderComponent.setFillColor(Color.LIGHTBLUE);
        renderComponent.setStrokeColor(Color.DARKBLUE);
        renderComponent.setStrokeWidth(2);
        renderComponent.setSize(1.0f, 1.0f);

        // Name it
        this.name = "Player_" + id;
    }

    public BlockPlayer(float x, float y, String colorName) {
        this(x, y);
        setColor(colorName);
    }

    @Override
    public void show() {
        super.show();
        UserGame.getCurrentGame().addEntity(this);
    }

    // ========== MOVEMENT STYLE CONTROL ==========

    public void setMovementStyle(MovementStyle style) {
        this.movementStyle = style;
    }

    public MovementStyle getMovementStyle() {
        return movementStyle;
    }

    // ========== MOVEMENT METHODS ==========

    public void moveRight() {
        if (!useGravity) {
            // FLYING MODE
            setVelocity(flySpeed, velocity.y);
        } else {
            // GRAVITY MODE
            if (movementStyle == MovementStyle.DIRECT) {
                setVelocity(walkSpeed, velocity.y);
            } else {
                // VELOCITY mode
                if (velocity.x < walkSpeed) {
                    addVelocity(acceleration * 0.016f, 0);
                    if (velocity.x > walkSpeed) {
                        setVelocity(walkSpeed, velocity.y);
                    }
                }
            }
        }
    }

    public void moveLeft() {
        if (!useGravity) {
            setVelocity(-flySpeed, velocity.y);
        } else {
            if (movementStyle == MovementStyle.DIRECT) {
                setVelocity(-walkSpeed, velocity.y);
            } else {
                if (velocity.x > -walkSpeed) {
                    addVelocity(-acceleration * 0.016f, 0);
                    if (velocity.x < -walkSpeed) {
                        setVelocity(-walkSpeed, velocity.y);
                    }
                }
            }
        }
    }

    public void moveDown() {
        if (!useGravity) {
            setVelocity(velocity.x, -flySpeed);
        } else {
            if (movementStyle == MovementStyle.DIRECT) {
                setVelocity(velocity.x, -walkSpeed);
            } else {
                if (velocity.y > -walkSpeed) {
                    addVelocity(0, -acceleration * 0.016f);
                    if (velocity.y < -walkSpeed) {
                        setVelocity(velocity.x, -walkSpeed);
                    }
                }
            }
        }
    }

    public void moveUp() {
        if (!useGravity) {
            setVelocity(velocity.x, flySpeed);
        } else {
            if (movementStyle == MovementStyle.DIRECT) {
                setVelocity(velocity.x, walkSpeed);
            } else {
                if (velocity.y < walkSpeed) {
                    addVelocity(0, acceleration * 0.016f);
                    if (velocity.y > walkSpeed) {
                        setVelocity(velocity.x, walkSpeed);
                    }
                }
            }
        }
    }

    //  FIXED JUMP METHOD
    public void jump() {
        System.out.println("jump() called - useGravity=" + useGravity +
                ", isOnGround=" + isOnGround() +
                ", isAutoJumping=" + isAutoJumping +
                ", jumpRequested=" + jumpRequested);

        if (useGravity) {
            if (movementStyle == MovementStyle.DIRECT) {
                // DIRECT mode: Auto up-down jump
                if (!isAutoJumping && !jumpRequested && isOnGround()) {
                    jumpRequested = true;
                    System.out.println("Jump requested for DIRECT mode");
                } else {
                    System.out.println("Cannot jump - DIRECT mode: " +
                            "autoJump=" + isAutoJumping +
                            ", requested=" + jumpRequested +
                            ", onGround=" + isOnGround());
                }
            } else {
                // VELOCITY mode: Physics-based jump
                if (isOnGround() && !isAutoJumping && !jumpRequested) {
                    super.jump(-jumpPower);  // Call parent jump method
                    isAutoJumping = true;
                    jumpRequested = true;
                    System.out.println("Physics jump executed with power: " + jumpPower);
                } else {
                    System.out.println("Cannot jump - VELOCITY mode: " +
                            "onGround=" + isOnGround() +
                            ", autoJump=" + isAutoJumping);
                }
            }
        } else {
            // FLYING MODE
            if (!isAutoJumping && !jumpRequested) {
                jumpRequested = true;
                System.out.println("Jump requested for FLYING mode");
            }
        }
    }
    // ADD THIS - Start the auto jump
    private void startAutoJump() {
        isAutoJumping = true;
        jumpStartY = getY();
        jumpTargetY = getY() + flyJumpDistance;
        jumpGoingUp = true;
        // jumpRequested already reset in update()
    }
    // ========== STOP METHODS ==========

    @Override
    public void stop() {
        super.stop();
        isAutoJumping = false;
        jumpRequested = false;
    }

    public void stopHorizontal() {
        setVelocity(0, velocity.y);
    }

    public void stopVertical() {
        setVelocity(velocity.x, 0);
    }

    // ========== UPDATE ==========

    @Override
    public void update(float deltaTime) {
        // Handle jump request
        if (jumpRequested && !isAutoJumping) {
            startAutoJump();
            jumpRequested = false; // CRITICAL: Reset immediately
        }

        // Handle auto-jump
        if (isAutoJumping) {
            handleAutoJump();
        }

        // Update physics
        super.update(deltaTime);

    }

    //  FIXED - Better auto-jump handling
    private void handleAutoJump() {
        float jumpSpeed = 0.3f;

        if (jumpGoingUp) {
            setPosition(getX(), getY() + jumpSpeed);

            if (getY() >= jumpTargetY) {
                setPosition(getX(), jumpTargetY);
                jumpGoingUp = false;
            }
        } else {
            setPosition(getX(), getY() - jumpSpeed);

            if (getY() <= jumpStartY) {
                setPosition(getX(), jumpStartY);
                isAutoJumping = false;

                //  Reset jump state completely
                jumpRequested = false;

                if (useGravity && movementStyle == MovementStyle.DIRECT) {
                    setOnGround(true);
                }
            }
        }
    }
    public boolean canJump() {
        if (useGravity && movementStyle == MovementStyle.VELOCITY) {
            return isOnGround() && !isAutoJumping && !jumpRequested;
        } else if (useGravity && movementStyle == MovementStyle.DIRECT) {
            return isOnGround() && !isAutoJumping && !jumpRequested;
        } else {
            return !isAutoJumping && !jumpRequested;
        }
    }

    // ========== STATE CHECKS ==========

    @Override
    public boolean isOnGround() {
        // If we're in auto-jump, we're not on ground
        if (isAutoJumping) return false;

        // Check parent's ground detection
        boolean onGround = super.isOnGround();

        // Additional check for DIRECT movement style
        if (movementStyle == MovementStyle.DIRECT && useGravity) {
            // For direct movement, check if Y position is at integer boundary
            float yPos = getY();
            float floorY = (float)Math.floor(yPos);
            onGround = Math.abs(yPos - floorY) < 0.5f;
            if (onGround && Math.abs(yPos - floorY) > 0.01f) {
                setPosition(getX(), floorY);
            }
        }

        return onGround;
    }

    public void setOnGround(boolean onGround) {
        super.setOnGround(onGround);

        //  FIXED - Reset auto-jump when landing
        if (onGround && useGravity && movementStyle == MovementStyle.VELOCITY) {
            isAutoJumping = false;
        }
    }

    public boolean isMovingHorizontally() {
        return Math.abs(velocity.x) > 0.1f;
    }

    public boolean isMovingVertically() {
        return Math.abs(velocity.y) > 0.1f;
    }

    public boolean isFalling() {
        return velocity.y > 0.5f;
    }

    public boolean isRising() {
        return velocity.y < -0.5f;
    }

    public boolean isAt(float x, float y) {
        return Math.abs(getX() - x) < 0.5f && Math.abs(getY() - y) < 0.5f;
    }

    public boolean isAutoJumping() {
        return isAutoJumping;
    }

    // ========== GRAVITY CONTROL ==========

    public void setGravity(boolean enabled) {
        setUseGravity(enabled);
        if (enabled && this.gravityStrength == 0) {
            this.gravityStrength = DEFAULT_GRAVITY;
        }
    }

    public void setGravity(float gravityValue) {
        this.gravityStrength = gravityValue;
        setUseGravity(true);
    }

    public boolean isGravityEnabled() {
        return useGravity;
    }

    // ========== SPEED CONTROL API ==========

    /**
     * SMART setSpeed - automatically applies to current gravity mode
     * If gravity ON → sets walkSpeed
     * If gravity OFF → sets flySpeed
     */
    public void setSpeed(float speed) {
        float clampedSpeed = Math.max(0.1f, speed);

        if (useGravity) {
            this.walkSpeed = clampedSpeed;
        } else {
            this.flySpeed = clampedSpeed;
        }

        // For backward compatibility
        this.moveSpeed = clampedSpeed;
    }

    /**
     * Get current effective speed based on mode
     */
    public float getCurrentSpeed() {
        return useGravity ? walkSpeed : flySpeed;
    }

    /**
     * Explicitly set flying mode speed (gravity OFF)
     */
    public void setFlySpeed(float speed) {
        this.flySpeed = Math.max(0.1f, speed);
    }

    public float getFlySpeed() {
        return flySpeed;
    }

    /**
     * Explicitly set gravity mode speed (gravity ON)
     */
    public void setWalkSpeed(float speed) {
        this.walkSpeed = Math.max(0.1f, speed);
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    /**
     * Explicitly set acceleration for gravity mode
     */
    public void setAcceleration(float accel) {
        this.acceleration = Math.max(1.0f, accel);
    }

    public float getAcceleration() {
        return acceleration;
    }

    /**
     * Set jump power (for gravity mode parabolic jump)
     */
    public void setJumpPower(float power) {
        this.jumpPower = Math.max(1.0f, power);
    }

    public float getJumpPower() {
        return jumpPower;
    }

    /**
     * Set flying mode jump height
     */
    public void setFlyJumpHeight(float height) {
        this.flyJumpDistance = Math.max(0.5f, height);
    }

    public float getFlyJumpHeight() {
        return flyJumpDistance;
    }

    // ========== APPEARANCE CUSTOMIZATION ==========

    public void setColor(Color color) {
        getRenderComponent().setFillColor(color);
    }

    public void setColor(int r, int g, int b) {
        getRenderComponent().setFillColor(r, g, b);
    }

    public void setColor(String colorNameOrHex) {
        if (colorNameOrHex.startsWith("#")) {
            getRenderComponent().setFillColor(colorNameOrHex);
        } else {
            getRenderComponent().setFillColor(parseColorName(colorNameOrHex));
        }
    }

    private Color parseColorName(String name) {
        switch (name.toUpperCase()) {
            case "RED": return Color.RED;
            case "BLUE": return Color.BLUE;
            case "GREEN": return Color.GREEN;
            case "YELLOW": return Color.YELLOW;
            case "ORANGE": return Color.ORANGE;
            case "PURPLE": return Color.PURPLE;
            case "PINK": return Color.PINK;
            case "CYAN": return Color.CYAN;
            case "MAGENTA": return Color.MAGENTA;
            case "LIME": return Color.LIME;
            case "BROWN": return Color.BROWN;
            case "GRAY": return Color.GRAY;
            case "BLACK": return Color.BLACK;
            case "WHITE": return Color.WHITE;
            case "GOLD": return Color.GOLD;
            case "SILVER": return Color.SILVER;
            default: return Color.web(name);
        }
    }

    public void setSize(float width, float height) {
        getRenderComponent().setSize(width, height);
        if (hasCollider()) {
            getCollider().setSize(width, height);
        }
    }

    public void setRoundness(float roundness) {
        if (roundness > 0) {
            getRenderComponent().setShapeType(com.example.codeplayfx.world.render.ShapeType.ROUNDED_RECTANGLE);
        } else {
            getRenderComponent().setShapeType(com.example.codeplayfx.world.render.ShapeType.RECTANGLE);
        }
    }

    public void loadSprite(String imagePath) {
        getRenderComponent().loadImage(imagePath);
    }

    // ========== OVERRIDES ==========

    @Override
    protected void onDamage(float amount) {}

    @Override
    protected void onDeath() {
        super.onDeath();
        stop();
    }
}