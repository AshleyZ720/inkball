package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Tile implements Drawable {
    public static final int EMPTY = 0;
    public static final int WALL = 1;
    public static final int HOLE = 2;
    public static final int SPAWNER = 3;
    public static final int BALL = 4;

    private int x, y;  // Position on the grid
    private int type;  // Tile type
    private PImage baseImage;  // The base tile image (tile.png)
    private PImage overlayImage;
    private boolean isCovered;

    public Tile(int x, int y, int type, PImage baseImage, PImage overlayImage) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.baseImage = baseImage;
        this.overlayImage = overlayImage;
        this.isCovered = false;
    }

    @Override
    public void draw(PApplet app) {
        if (baseImage != null) {
            app.image(baseImage, x * App.CELLSIZE, y * App.CELLSIZE);
        }
        if (overlayImage != null) {
            app.image(overlayImage, x * App.CELLSIZE, y * App.CELLSIZE);
        }
    }

    public int getType() {
        return type;
    }

    public void setCovered(boolean covered) {
        this.isCovered = covered;
    }

    public boolean isCovered() {
        return isCovered;
    }
}
