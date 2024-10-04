package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Ball implements Drawable {
    private int x, y;
    private PImage image;
    private int type;

    public Ball(int x, int y, PImage image, int type) {
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
}
