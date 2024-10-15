package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * Represents a hole in the game that can attract and capture balls.
 * A hole attracts nearby balls, reduces their size as they approach, and eventually captures them.
 * If the ball matches the hole's type, the player's score increases, otherwise it decreases.
 */
public class Hole implements Drawable {
    private float x, y;  // Position of the hole on the grid
    private PImage image;  // Image of the hole
    private int type;  // Type of the hole, used for matching ball types
    private static final float ATTRACT_RADIUS = 32;  // The radius in which the hole attracts balls
    private static final float ATTRACTION_FORCE = 0.005f; // Force of attraction, 0.5% of the distance vector
    private boolean wasWithinAttractRadius = false;  // Flag to check if the ball was within attraction radius

    /**
     * Constructs a Hole object with a given position, image, and type.
     *
     * @param x The x-coordinate of the hole (in grid units).
     * @param y The y-coordinate of the hole (in grid units).
     * @param image The image representing the hole.
     * @param type The type of the hole, used to determine score interactions.
     */
    public Hole(int x, int y, PImage image, int type) {
        this.x = (x + 1) * App.CELLSIZE;
        this.y = (y + 1) * App.CELLSIZE;
        this.image = image;
        this.type = type;
    }

    /**
     * Gets the type of the hole.
     *
     * @return The type of the hole.
     */
    public int getType() {
        return type;
    }

    /**
     * Draws the hole on the game screen.
     *
     * @param app The PApplet instance used for drawing the hole.
     */
    @Override
    public void draw(PApplet app) {
        app.image(image, x - App.CELLSIZE, y - App.CELLSIZE, App.CELLSIZE * 2, App.CELLSIZE * 2);
    }

    /**
     * Updates the hole. No specific update logic for static holes.
     */
    @Override
    public void update() {
        // No update needed for static hole
    }

    /**
     * Attracts a ball toward the hole and captures it if it gets close enough.
     * If the ball is within the attraction radius, the hole will apply a force to pull the ball closer.
     * The ball's size is reduced as it approaches, and if it gets close enough, it is captured.
     *
     * @param ball The ball being attracted.
     * @param app The main application instance.
     */
    public void attractBall(Ball ball, App app) {
        if (ball.isCaptured()) return;

        float dx = x - ball.getX();
        float dy = y - ball.getY();
        float dist = PApplet.sqrt(dx * dx + dy * dy);

        if (dist <= ATTRACT_RADIUS) {
            wasWithinAttractRadius = true;
            // Apply attraction force
            float fx = ATTRACTION_FORCE * dx;
            float fy = ATTRACTION_FORCE * dy;
            ball.applyAttraction(fx, fy);

            // Adjust ball size as it approaches
            ball.adjustSize(dist, ATTRACT_RADIUS);

            // Capture the ball if it's close enough
            if (dist < 1 || ball.getRadius() <= 4f) {
                captureBall(ball, app);
            }
        } else {
            // Reset ball size if it moves out of the attraction radius
            if (wasWithinAttractRadius) {
                ball.resetSize();
            }
            wasWithinAttractRadius = false;
        }
    }

    /**
     * Captures the ball when it reaches the center of the hole or becomes small enough.
     * If the ball matches the hole's type, the player's score is increased.
     * If the ball type doesn't match, the score decreases and the ball is respawned.
     *
     * @param ball The ball to capture.
     * @param app The main application instance.
     */
    private void captureBall(Ball ball, App app) {
        if (!ball.isCaptured()) {
            ball.setCaptured(true);

            app.getDrawables().remove(ball);

            // Increase or decrease the score based on the ball type and hole type
            if (ball.getType() == type || ball.getType() == 0 || type == 0) {
                app.increaseScore(ball.getType());
            } else {
                app.decreaseScore(ball.getType());
                app.respawnBall(ball);
            }
        }
    }

    /**
     * Gets the x-coordinate of the hole.
     *
     * @return The x-coordinate of the hole.
     */
    public float getX() {
        return x;
    }

    /**
     * Sets the x-coordinate of the hole.
     *
     * @param x The new x-coordinate of the hole.
     */
    public void setX(float x) {
        this.x = x;
    }

    /**
     * Gets the y-coordinate of the hole.
     *
     * @return The y-coordinate of the hole.
     */
    public float getY() {
        return y;
    }

    /**
     * Sets the y-coordinate of the hole.
     *
     * @param y The new y-coordinate of the hole.
     */
    public void setY(float y) {
        this.y = y;
    }
}
