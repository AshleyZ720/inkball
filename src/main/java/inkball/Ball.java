package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Ball implements Drawable {
    private float x, y;
    private PImage image;
    private int type;
    private float vx, vy;
    private float radius;
    private static final float ORIGINAL_RADIUS = 12f;
    private static final float MIN_RADIUS = 0.5f;
    private static final float SIZE_ADJUSTMENT_SPEED = 2.0f;
    private boolean captured = false;

    public Ball(int x, int y, PImage image, int type) {
        this.x = x * App.CELLSIZE + App.CELLSIZE / 2f;
        this.y = y * App.CELLSIZE + App.CELLSIZE / 2f;
        this.image = image;
        this.type = type;
        this.radius = ORIGINAL_RADIUS;

        float baseSpeed = 2.0f;
        if (App.FPS == 60) {
            baseSpeed /= 2;
        }

        this.vx = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
        this.vy = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
    }

    @Override
    public void draw(PApplet applet) {
        if (!captured) {
            float diameter = radius * 2;
            applet.image(image, x - radius, y - radius, diameter, diameter);
        }
    }

    @Override
    public void update() {
        if (!captured) {
            x += vx;
            y += vy;
        }
    }

    public void applyAttraction(float fx, float fy) {
        vx += fx;
        vy += fy;
    }

    public void adjustSize(float distance, float maxDistance) {
        if (distance <= maxDistance) {
            float sizeRatio = distance / maxDistance;
            sizeRatio = (float) Math.pow(sizeRatio, SIZE_ADJUSTMENT_SPEED);
            radius = PApplet.map(sizeRatio, 0, 1, MIN_RADIUS, ORIGINAL_RADIUS);
        } else {
            radius = ORIGINAL_RADIUS;
        }
    }

    public void resetSize() {
        radius = ORIGINAL_RADIUS;
        captured = false;
    }

    public boolean isOverlappingHole(float holeX, float holeY) {
        return PApplet.dist(x, y, holeX, holeY) < 1; // Very small threshold for capture
    }

    public boolean isCaptured() {
        return captured;
    }

    public void setCaptured(boolean captured) {
        this.captured = captured;
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

    public float getRadius() {
        return radius;
    }

    public int getType() {
        return type;
    }
}