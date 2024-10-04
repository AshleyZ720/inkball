package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Spawner implements Drawable {
    private int x, y;
    private PImage image;

    public Spawner(int x, int y, PImage image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }

    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    @Override
    public void update() {

    }
}
