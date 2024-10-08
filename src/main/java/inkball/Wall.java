package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;

public class Wall implements Drawable, Collidable {
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

    @Override
    public void update() {
        // Walls don't need to update
    }

    @Override
    public boolean checkCollision(Ball ball) {
        float ballX = ball.getX();
        float ballY = ball.getY();
        float radius = ball.getRadius();

        float left = x * App.CELLSIZE;
        float right = left + App.CELLSIZE;
        float top = y * App.CELLSIZE;
        float bottom = top + App.CELLSIZE;

        // Find the closest point on the wall to the ball
        float closestX = clamp(ballX, left, right);
        float closestY = clamp(ballY, top, bottom);

        // Calculate the distance between the ball's center and this closest point
        float distanceX = ballX - closestX;
        float distanceY = ballY - closestY;

        // If the distance is less than the radius, there's a collision
        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
        if (distanceSquared < radius * radius) {
            float distance = (float) Math.sqrt(distanceSquared);
            float overlap = radius - distance;

            // Normalize the collision vector
            float collisionX = distanceX / distance;
            float collisionY = distanceY / distance;

            // Adjust the ball's position to prevent overlap
            ball.setX(ball.getX() + collisionX * overlap / 2);
            ball.setY(ball.getY() + collisionY * overlap / 2);

            // Reflect the velocity vector
            PVector velocity = new PVector(ball.getVx(), ball.getVy());
            PVector normal = new PVector(collisionX, collisionY);
            float dotProduct = velocity.dot(normal);
            velocity.sub(PVector.mult(normal, 2 * dotProduct));

            ball.setVx(velocity.x);
            ball.setVy(velocity.y);

            return true;
        }

        return false;
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    // Getter methods for position if needed
    public int getX() { return x; }
    public int getY() { return y; }
}