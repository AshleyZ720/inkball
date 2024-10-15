package inkball;

import processing.core.PApplet;
import processing.core.PImage;

/**
 * The {@code Tile} class represents a single tile on the game board in the Inkball game.
 * Each tile has a position on the grid, a base image, an optional overlay image,
 * and a {@code Drawable} object associated with it, such as a wall or hole.
 * The {@code Tile} can also be "covered," meaning it is obscured by another object or effect.
 */
public class Tile implements Drawable {

    /** The x-coordinate of the tile's position on the grid. */
    private int x;

    /** The y-coordinate of the tile's position on the grid. */
    private int y;

    /** The drawable object representing the tile type (such as a wall or hole). */
    private Drawable drawable;

    /** The base image of the tile (usually 'tile.png'). */
    private PImage baseImage;

    /** The optional overlay image for the tile, displayed on top of the base image. */
    private PImage overlayImage;

    /** Indicates whether the tile is covered or not. */
    private boolean isCovered;

    /** The original overlay image for the tile, used for resetting the overlay. */
    private PImage originalOverlayImage;

    /**
     * Constructs a new {@code Tile} object at the specified grid position with a base image, an overlay image,
     * and an associated {@code Drawable} object.
     *
     * @param x             The x-coordinate of the tile's position on the grid.
     * @param y             The y-coordinate of the tile's position on the grid.
     * @param drawable      The {@code Drawable} object representing the tile type.
     * @param baseImage     The base image for the tile.
     * @param overlayImage  The optional overlay image for the tile.
     */
    public Tile(int x, int y, Drawable drawable, PImage baseImage, PImage overlayImage) {
        this.x = x;
        this.y = y;
        this.drawable = drawable;
        this.baseImage = baseImage;
        this.originalOverlayImage = overlayImage;
        this.isCovered = false;
    }

    /**
     * Draws the tile on the game board. It first draws the base image, then the overlay image (if present),
     * and finally the {@code Drawable} object associated with the tile.
     *
     * @param app The {@code PApplet} object used to draw the tile.
     */
    @Override
    public void draw(PApplet app) {
        if (baseImage != null) {
            app.image(baseImage, x * App.CELLSIZE, y * App.CELLSIZE);
        }
        if (overlayImage != null) {
            app.image(overlayImage, x * App.CELLSIZE, y * App.CELLSIZE);
        }
        if (drawable != null) {
            drawable.draw(app);
        }
    }

    /**
     * Returns the {@code Drawable} object associated with the tile.
     *
     * @return The {@code Drawable} object for the tile.
     */
    public Drawable getDrawable() {
        return drawable;
    }

    /**
     * Sets the {@code Drawable} object for the tile.
     *
     * @param drawable The new {@code Drawable} object to associate with the tile.
     */
    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    /**
     * Checks whether the tile is empty (i.e., no {@code Drawable} object is associated with it).
     *
     * @return {@code true} if the tile is empty, {@code false} otherwise.
     */
    public boolean isEmpty() {
        return drawable == null;
    }

    /**
     * Sets whether the tile is covered or not.
     *
     * @param covered {@code true} to mark the tile as covered, {@code false} otherwise.
     */
    public void setCovered(boolean covered) {
        this.isCovered = covered;
    }

    /**
     * Checks whether the tile is currently covered.
     *
     * @return {@code true} if the tile is covered, {@code false} otherwise.
     */
    public boolean isCovered() {
        return isCovered;
    }

    /**
     * Updates the tile. In this implementation, the tile does not change over time, so this method does nothing.
     */
    @Override
    public void update() {
        // No update behavior is required for static tiles.
    }

    /**
     * Returns the overlay image currently set for the tile.
     *
     * @return The current overlay image.
     */
    public PImage getOverlayImage() {
        return overlayImage;
    }

    /**
     * Sets a new overlay image for the tile.
     *
     * @param newOverlayImage The new overlay image to be set.
     */
    public void setOverlayImage(PImage newOverlayImage) {
        this.overlayImage = newOverlayImage;
    }

    /**
     * Resets the overlay image to the original overlay image that was set when the tile was created.
     */
    public void resetOverlayImage() {
        this.overlayImage = originalOverlayImage;
    }

    /**
     * Returns the x-coordinate of the tile's position on the grid.
     *
     * @return The x-coordinate of the tile.
     */
    public int getX() {
        return x;
    }

    /**
     * Returns the y-coordinate of the tile's position on the grid.
     *
     * @return The y-coordinate of the tile.
     */
    public int getY() {
        return y;
    }
}
