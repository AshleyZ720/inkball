package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Hole implements Drawable {
    private int x, y;
    private PImage image;

    public Hole(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }
}
