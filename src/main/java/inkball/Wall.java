package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Wall implements Drawable {
    private int x, y;
    private PImage image;
    private int type;

    public Wall(int x, int y, PImage image, int type) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.type = type;
    }

    public int getType() {
        return type;
    }
    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    // Getter methods for position if needed
    public int getX() { return x; }
    public int getY() { return y; }
}

