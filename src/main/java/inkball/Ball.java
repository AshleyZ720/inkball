package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Ball implements Drawable {
    private float x, y; // Using float for position
    private PImage image;
    private int type;
    private float vx, vy; // Velocity in float
    private final int radius = 12; // Setting radius to 12px

    public Ball(int x, int y, PImage image, int type) {
        this.x = x * App.CELLSIZE + App.CELLSIZE / 2; // Adjusting to canvas coordinates
        this.y = y * App.CELLSIZE + App.CELLSIZE / 2;
        this.image = image;
//        this.vx = App.random.nextFloat() * 5 + 2; // Random speed
//        this.vy = App.random.nextFloat() * 5 + 2;
        this.type = type;
        // Set initial velocity
        float baseSpeed = 2.0f; // Base speed of 2 pixels per frame at 30 fps

        // Adjust speed if using 60 fps
        if (App.FPS == 60) {
            baseSpeed /= 2;
        }

        // Randomly set velocity to either -2 or 2 (or the adjusted value for 60 fps) for both x and y
        this.vx = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
        this.vy = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
    }

    @Override
    public void draw(PApplet applet) {
        applet.image(image, x - radius, y - radius); // Drawing the ball centered at (x, y)
    }

    @Override
    public void update() {
        // Update position
        x += vx;
        y += vy;


    }

    public Collidable checkCollisions(Collidable[] collidables) {
        for (Collidable collidable : collidables) {
            if (collidable.checkCollision(this)) {
                return collidable;
            }
        }
        return null;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public int getRadius() {
        return radius;
    }
}