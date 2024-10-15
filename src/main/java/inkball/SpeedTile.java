package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The {@code SpeedTile} class represents a special tile in the Inkball game
 * that can accelerate the ball in a specific direction when the ball passes over it.
 * This class implements the {@code Drawable} interface, allowing it to be rendered
 * on the game grid.
 */
public class SpeedTile implements Drawable {

    /** The x-coordinate of the speed tile on the grid. */
    private int x;

    /** The y-coordinate of the speed tile on the grid. */
    private int y;

    /** The image representing the speed tile. */
    private PImage image;

    /** The direction in which the speed tile accelerates the ball ('^', 'v', '>', '<'). */
    private char direction;

    /**
     * Constructs a new {@code SpeedTile} object with a specific grid position, image, and direction.
     *
     * @param x         The x-coordinate of the speed tile's position on the grid.
     * @param y         The y-coordinate of the speed tile's position on the grid.
     * @param image     The {@code PImage} object representing the speed tile.
     * @param direction The direction the tile will boost the ball, one of '^', 'v', '>', '<'.
     */
    public SpeedTile(int x, int y, PImage image, char direction) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.direction = direction;
    }

    /**
     * Draws the speed tile on the game grid using the provided {@code PApplet} object.
     *
     * @param app The {@code PApplet} object used to draw the speed tile.
     */
    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    /**
     * Updates the speed tile. Currently, this method does not perform any operations
     * because speed tiles remain static throughout the game.
     */
    @Override
    public void update() {
        // No update behavior is required for speed tiles.
    }

    /**
     * Applies a speed boost to the ball if it is moving in the correct direction
     * and can still accelerate.
     *
     * @param ball The {@code Ball} object that is affected by the speed tile.
     */
    public void applySpeedBoost(Ball ball) {
        if (!ball.canAccelerate()) {
            return;
        }

        float speedMultiplier = 1.05f;
        boolean shouldAccelerate = false;

        switch (direction) {
            case '^':
                if (ball.getVy() < 0) shouldAccelerate = true;
                break;
            case 'v':
                if (ball.getVy() > 0) shouldAccelerate = true;
                break;
            case '>':
                if (ball.getVx() > 0) shouldAccelerate = true;
                break;
            case '<':
                if (ball.getVx() < 0) shouldAccelerate = true;
                break;
        }

        if (shouldAccelerate && ball.canAccelerate()) {
            ball.setVx(ball.getVx() * speedMultiplier);
            ball.setVy(ball.getVy() * speedMultiplier);
        }
    }

    /**
     * Returns the x-coordinate of the speed tile's position on the grid.
     *
     * @return The x-coordinate of the speed tile.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the speed tile's position on the grid.
     *
     * @return The y-coordinate of the speed tile.
     */
    public int getY() {
        return y;
    }

    /**
     * Returns the direction in which the speed tile accelerates the ball.
     * The direction can be one of the following: '^' (up), 'v' (down), '>' (right), '<' (left).
     *
     * @return The direction of the speed tile.
     */
    public char getDirection() {
        return direction;
    }
}
