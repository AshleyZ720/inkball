package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The {@code Spawner} class represents a spawner tile in the Inkball game.
 * This class implements the {@code Drawable} interface, allowing it to be rendered
 * in the game. A spawner is responsible for spawning new balls during the game.
 */
public class Spawner implements Drawable {

    /** The x-coordinate of the spawner on the grid. */
    private int x;

    /** The y-coordinate of the spawner on the grid. */
    private int y;

    /** The image representing the spawner tile. */
    private PImage image;

    /**
     * Constructs a new {@code Spawner} object with a specific grid position and an image.
     *
     * @param x The x-coordinate of the spawner's position on the grid.
     * @param y The y-coordinate of the spawner's position on the grid.
     * @param image The {@code PImage} object representing the spawner.
     */
    public Spawner(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    /**
     * Draws the spawner on the game grid using the provided {@code PApplet} object.
     *
     * @param app The {@code PApplet} object used to draw the spawner.
     */
    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    /**
     * Updates the spawner. Currently, this method does not perform any operations
     * because the spawner remains static throughout the game.
     */
    @Override
    public void update() {
        // No update behavior is required for spawners.
    }

    /**
     * Returns the x-coordinate of the spawner's position on the grid.
     *
     * @return The x-coordinate of the spawner.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the spawner's position on the grid.
     *
     * @return The y-coordinate of the spawner.
     */
    public int getY() {
        return y;
    }
}
