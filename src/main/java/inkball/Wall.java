package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Wall implements Drawable {
    private int x, y;
    private PImage image;

    public Wall(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    // Getter methods for position if needed
    public int getX() { return x; }
    public int getY() { return y; }
}

