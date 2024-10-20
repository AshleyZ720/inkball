package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Represents a ball in the Inkball game. A ball is a moving object that can
 * interact with other game elements such as walls, speed tiles, and holes.
 * It has velocity, position, size, and can be captured or affected by external forces.
 */
public class Ball implements Drawable {
    private float x, y; // Current x and y coordinates of the ball
    private PImage image; // Image representing the ball
    private int type; // The type of ball, used for distinguishing colors and behaviors
    private float vx, vy; // Current velocity in the x and y directions
    private float savedVx, savedVy; // Saved velocities for restoring after pause
    private float radius; // Current radius of the ball
    private static final float ORIGINAL_RADIUS = 12f; // Original radius of the ball
    private static final float MIN_RADIUS = 0.5f; // Minimum allowed radius for the ball
    private static final float SIZE_ADJUSTMENT_SPEED = 2.0f; // Speed of size adjustment when affected by holes
    private boolean captured = false; // Whether the ball has been captured by a hole

    private static final int MAX_VX = 4; // Maximum velocity in the x direction
    private static final int MAX_VY = 4; // Maximum velocity in the y direction

    /**
     * Constructs a new Ball object at the specified position.
     *
     * @param x the initial x-coordinate of the ball
     * @param y the initial y-coordinate of the ball
     * @param image the PImage used to display the ball
     * @param type the type of ball (used for different colors)
     */
    public Ball(int x, int y, PImage image, int type) {
        this.x = x * App.CELLSIZE + App.CELLSIZE / 2f;
        this.y = y * App.CELLSIZE + App.CELLSIZE / 2f;
        this.image = image;
        this.type = type;
        this.radius = ORIGINAL_RADIUS;

        float baseSpeed = 2.0f;
        if (App.FPS == 60) {
            baseSpeed /= 2;
        }

        this.vx = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
        this.vy = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
    }

    /**
     * Draws the ball on the screen using the provided PApplet.
     *
     * @param applet the PApplet instance to draw on
     */
    @Override
    public void draw(PApplet applet) {
        if (!captured) {
            float diameter = radius * 2;
            applet.image(image, x - radius, y - radius, diameter, diameter);
        }
    }

    /**
     * Updates the position of the ball based on its velocity.
     */
    @Override
    public void update() {
        if (!captured) {
            x += vx;
            y += vy;
        }
    }

    /**
     * Updates the type and image of the ball.
     *
     * @param newType the new type for the ball
     */
    public void updateType(int newType) {
        this.type = newType;
        this.image = App.getBallImageByType(newType);
    }

    /**
     * Applies attraction forces to the ball.
     *
     * @param fx the x-component of the attraction force
     * @param fy the y-component of the attraction force
     */
    public void applyAttraction(float fx, float fy) {
        vx += fx;
        vy += fy;
    }

    /**
     * Adjusts the size of the ball based on its distance from a point.
     * This is typically used when the ball is near a hole to simulate being "sucked" into the hole.
     *
     * @param distance the current distance from the point
     * @param maxDistance the maximum distance for size adjustment
     */
    public void adjustSize(float distance, float maxDistance) {
        if (distance <= maxDistance) {
            float sizeRatio = distance / maxDistance;
            sizeRatio = (float) Math.pow(sizeRatio, SIZE_ADJUSTMENT_SPEED);
            radius = PApplet.map(sizeRatio, 0, 1, MIN_RADIUS, ORIGINAL_RADIUS);
        } else {
            radius = ORIGINAL_RADIUS;
        }
    }

    /**
     * Resets the size of the ball to its original radius and uncaptures it.
     * This is typically used after a ball has been attracted to and released by a hole.
     */
    public void resetSize() {
        radius = ORIGINAL_RADIUS;
        captured = false;
    }

    /**
     * Checks if the ball is overlapping with a hole. Used to determine if the ball should be captured.
     *
     * @param holeX the x-coordinate of the hole
     * @param holeY the y-coordinate of the hole
     * @return true if the ball is overlapping the hole, false otherwise
     */
    public boolean isOverlappingHole(float holeX, float holeY) {
        return PApplet.dist(x, y, holeX, holeY) < 1; // Very small threshold for capture
    }

    /**
     * Checks if the ball is captured.
     *
     * @return true if the ball is captured, false otherwise
     */
    public boolean isCaptured() {
        return captured;
    }

    /**
     * Sets the captured state of the ball.
     *
     * @param captured the new captured state
     */
    public void setCaptured(boolean captured) {
        this.captured = captured;
    }

    /**
     * Saves the current velocity of the ball and sets it to zero.
     * This is used when the game is paused.
     */
    public void saveVelocity() {
        if (this.vx != 0 || this.vy != 0) {
            this.savedVx = this.vx;
            this.savedVy = this.vy;
            this.vx = 0;
            this.vy = 0;
        }
    }

    /**
     * Restores the previously saved velocity of the ball.
     * This is used when the game is unpaused.
     */
    public void restoreVelocity() {
        if (this.vx == 0 && this.vy == 0) {
            this.vx = this.savedVx;
            this.vy = this.savedVy;
        }
    }

    /**
     * Checks if the ball can accelerate further based on its current velocity.
     *
     * @return true if the ball can accelerate, false otherwise
     */
    public boolean canAccelerate() {
        return (Math.abs(this.vx) <= MAX_VX && Math.abs(this.vy) <= MAX_VY);
    }

    // Getter and setter methods

    /**
     * Gets the x-coordinate of the ball.
     *
     * @return the x-coordinate
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the ball.
     *
     * @param x the new x-coordinate
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the ball.
     *
     * @return the y-coordinate
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the ball.
     *
     * @param y the new y-coordinate
     */
    public void setY(float y) {
        this.y = y;
    }

    /**
     * Gets the x-component of the ball's velocity.
     *
     * @return the x-component of velocity
     */
    public float getVx() {
        return vx;
    }

    /**
     * Sets the x-component of the ball's velocity.
     *
     * @param vx the new x-component of velocity
     */
    public void setVx(float vx) {
        this.vx = vx;
    }

    /**
     * Gets the y-component of the ball's velocity.
     *
     * @return the y-component of velocity
     */
    public float getVy() {
        return vy;
    }

    /**
     * Sets the y-component of the ball's velocity.
     *
     * @param vy the new y-component of velocity
     */
    public void setVy(float vy) {
        this.vy = vy;
    }

    /**
     * Gets the radius of the ball.
     *
     * @return the radius
     */
    public float getRadius() {
        return radius;
    }

    /**
     * Sets the radius of the ball.
     *
     * @param radius the new radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
    }

    /**
     * Gets the type of the ball.
     *
     * @return the ball type
     */
    public int getType() {
        return type;
    }
}
