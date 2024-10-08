package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Hole implements Drawable {
    private float x, y;
    private PImage image;
    private int type;
    private static final float ATTRACT_RADIUS = 32;
    private static final float ATTRACTION_FORCE = 0.005f; // 0.5% of the vector

    public Hole(int x, int y, PImage image, int type) {
        this.x = (x + 1) * App.CELLSIZE;
        this.y = (y + 1) * App.CELLSIZE;
        this.image = image;
        this.type = type;
        //System.out.println("Hole at (" + this.x * App.CELLSIZE + ", " + this.y * App.CELLSIZE + ")");
    }

    public int getType() {
        return type;
    }

    @Override
    public void draw(PApplet app) {
        app.image(image, x - App.CELLSIZE, y - App.CELLSIZE, App.CELLSIZE * 2, App.CELLSIZE * 2);
    }

    @Override
    public void update() {
        // No update needed for static hole
    }

    public void attractBall(Ball ball, App app) {
        if (ball.isCaptured()) return;

        float dx = x - ball.getX();
        float dy = y - ball.getY();
        float dist = PApplet.sqrt(dx * dx + dy * dy);

        if (dist <= ATTRACT_RADIUS) {
            // Apply attraction force
            float fx = ATTRACTION_FORCE * dx;
            float fy = ATTRACTION_FORCE * dy;
            ball.applyAttraction(fx, fy);

            // Adjust ball size
            ball.adjustSize(dist, ATTRACT_RADIUS);

            // Check for capture
            if (dist < 1 || ball.getRadius() <= 4f) {
                captureBall(ball, app);
            }
        } else {
            // Reset ball size if it's outside the attraction radius
            //ball.resetSize();
        }
    }


    private void captureBall(Ball ball, App app) {
        if (!ball.isCaptured()) {
            ball.setCaptured(true);

            app.getDrawables().remove(ball);

            if (ball.getType() == type || ball.getType() == 0 || type == 0) {
                app.increaseScore();
            } else {
                app.decreaseScore();
                app.respawnBall(ball);
            }
        }
    }
}