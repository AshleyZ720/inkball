package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

/**
 * The {@code Wall} class represents a stationary wall object on the Inkball game board.
 * It can detect collisions with {@code Ball} objects and reflect their velocities accordingly.
 * Each wall has a position on the grid and an image representing its appearance.
 */
public class Wall implements Drawable, Collidable {

    /** The x-coordinate of the wall's position on the grid. */
    private int x;

    /** The y-coordinate of the wall's position on the grid. */
    private int y;

    /** The image of the wall, used for rendering. */
    private PImage image;

    /** The type of the wall, which can affect ball interactions. */
    private int type;

    /**
     * Constructs a new {@code Wall} object at the specified grid position with the provided image and type.
     *
     * @param x      The x-coordinate of the wall's position on the grid.
     * @param y      The y-coordinate of the wall's position on the grid.
     * @param image  The image representing the wall.
     * @param type   The type of the wall, which determines how it interacts with balls.
     */
    public Wall(int x, int y, PImage image, int type) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.type = type;
    }

    /**
     * Returns the type of the wall.
     *
     * @return The type of the wall, which can affect how balls interact with it.
     */
    public int getType() {
        return type;
    }

    /**
     * Draws the wall on the game board at its grid position.
     *
     * @param app The {@code PApplet} object used to render the wall.
     */
    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    /**
     * Updates the state of the wall. In this implementation, walls are stationary and do not need to be updated.
     */
    @Override
    public void update() {
        // Walls don't need to update
    }

    /**
     * Checks for a collision between the wall and a ball. If a collision is detected, it reflects the ball's velocity.
     *
     * @param ball The {@code Ball} object to check for a collision with the wall.
     * @return {@code true} if a collision is detected, {@code false} otherwise.
     */
    @Override
    public boolean checkCollision(Ball ball) {
        float ballX = ball.getX();
        float ballY = ball.getY();
        float radius = ball.getRadius();

        float left = x * App.CELLSIZE;
        float right = left + App.CELLSIZE;
        float top = y * App.CELLSIZE;
        float bottom = top + App.CELLSIZE;

        // Find the closest point on the wall to the ball
        float closestX = clamp(ballX, left, right);
        float closestY = clamp(ballY, top, bottom);

        // Calculate the distance between the ball's center and this closest point
        float distanceX = ballX - closestX;
        float distanceY = ballY - closestY;

        // If the distance is less than the radius, there's a collision
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        if (distanceSquared < radius * radius) {
            float distance = (float) Math.sqrt(distanceSquared);
            float overlap = radius - distance;

            // Normalize the collision vector
            float collisionX = distanceX / distance;
            float collisionY = distanceY / distance;

            // Adjust the ball's position to prevent overlap
            ball.setX(ball.getX() + collisionX * overlap / 2);
            ball.setY(ball.getY() + collisionY * overlap / 2);

            // Reflect the ball's velocity
            PVector velocity = new PVector(ball.getVx(), ball.getVy());
            PVector normal = new PVector(collisionX, collisionY);
            float dotProduct = velocity.dot(normal);
            velocity.sub(PVector.mult(normal, 2 * dotProduct));

            ball.setVx(velocity.x);
            ball.setVy(velocity.y);

            // If the wall has a type, update the ball's type if needed
            if (this.type != 0 && ball.getType() != this.type) {
                ball.updateType(this.type);
            }

            return true;
        }

        return false;
    }

    /**
     * Clamps a value between a minimum and maximum.
     *
     * @param value The value to clamp.
     * @param min   The minimum value.
     * @param max   The maximum value.
     * @return The clamped value.
     */
    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    /**
     * Returns the x-coordinate of the wall's position on the grid.
     *
     * @return The x-coordinate of the wall.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the wall's position on the grid.
     *
     * @return The y-coordinate of the wall.
     */
    public int getY() {
        return y;
    }
}
