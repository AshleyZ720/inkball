package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Tile implements Drawable {
    private int x, y;  // Position on the grid
    private Drawable drawable;   // Tile type
    private PImage baseImage;  // The base tile image (tile.png)
    private PImage overlayImage;
    private boolean isCovered;


    public Tile(int x, int y, Drawable drawable, PImage baseImage, PImage overlayImage) {
        this.x = x;
        this.y = y;
        this.drawable = drawable;
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
        if (drawable != null) {
            drawable.draw(app);
        }
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public boolean isEmpty() {
        return drawable == null;
    }

    public void setCovered(boolean covered) {
        this.isCovered = covered;
    }

    public boolean isCovered() {
        return isCovered;
    }

    @Override
    public void update() {

    }
}
