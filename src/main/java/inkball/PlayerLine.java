package inkball;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@code PlayerLine} class represents a line drawn by the player in the Inkball game.
 * This class implements both the {@code Drawable} and {@code Collidable} interfaces,
 * meaning that it can be rendered in the game and can also interact with balls by detecting collisions.
 */
public class PlayerLine implements Drawable, Collidable {

    /** A list of points that make up the player's line. */
    private List<PVector> points;

    /** The thickness of the line, set to 10 pixels. */
    private static final float THICKNESS = 10;

    /** Indicates whether the line has already collided with a ball. */
    private boolean hasCollided;

    /**
     * Constructs a new {@code PlayerLine} object.
     * Initializes an empty list of points and sets {@code hasCollided} to false.
     */
    public PlayerLine() {
        points = new ArrayList<>();
        hasCollided = false;
    }

    /**
     * Adds a new point to the player line at the specified coordinates.
     *
     * @param x The x-coordinate of the point to add.
     * @param y The y-coordinate of the point to add.
     */
    public void addPoint(float x, float y) {
        points.add(new PVector(x, y));
    }

    /**
     * Draws the player line onto the provided {@code PApplet} object.
     * The line will only be drawn if it has not collided with a ball.
     *
     * @param app The {@code PApplet} object to draw the line on.
     */
    @Override
    public void draw(PApplet app) {
        if (!hasCollided) {
            app.stroke(0);
            app.strokeWeight(THICKNESS);
            app.noFill();
            app.beginShape();
            for (PVector p : points) {
                app.vertex(p.x, p.y);
            }
            app.endShape();
        }
    }

    /**
     * Updates the player line. Currently, this method does not perform any operations
     * because the line remains static after being drawn.
     */
    @Override
    public void update() {
        // No update behavior is required for player lines
    }

    /**
     * Checks if the player line has collided with a ball. A collision is detected
     * if the ball's next position overlaps with any part of the line.
     *
     * @param ball The ball to check for collision with the player line.
     * @return {@code true} if a collision is detected, {@code false} otherwise.
     */
    @Override
    public boolean checkCollision(Ball ball) {
        if (hasCollided || points.size() < 2) return false;
        PVector ballPos = new PVector(ball.getX(), ball.getY());
        PVector ballVelocity = new PVector(ball.getVx(), ball.getVy());
        PVector nextPos = PVector.add(ballPos, ballVelocity);
        for (int i = 0; i < points.size() - 1; i++) {
            PVector p1 = points.get(i);
            PVector p2 = points.get(i + 1);
            float d1 = PVector.dist(p1, nextPos);
            float d2 = PVector.dist(p2, nextPos);
            float lineLength = PVector.dist(p1, p2);
            if (d1 + d2 < lineLength + ball.getRadius()) {
                // Collision detected, calculate new trajectory
                PVector normal = getNormal(p1, p2, ballPos);
                PVector newVelocity = reflect(ballVelocity, normal);
                ball.setVx(newVelocity.x);
                ball.setVy(newVelocity.y);
                hasCollided = true;
                return true;
            }
        }
        return false;
    }

    /**
     * Calculates the normal vector of the line segment defined by two points relative to the ball's position.
     *
     * @param p1 The first point of the line segment.
     * @param p2 The second point of the line segment.
     * @param ballPos The current position of the ball.
     * @return The normal vector to the line segment closest to the ball.
     */
    private PVector getNormal(PVector p1, PVector p2, PVector ballPos) {
        PVector lineVec = PVector.sub(p2, p1);
        PVector normal1 = new PVector(-lineVec.y, lineVec.x).normalize();
        PVector normal2 = PVector.mult(normal1, -1);
        PVector midpoint = PVector.add(p1, p2).div(2);
        PVector point1 = PVector.add(midpoint, normal1);
        PVector point2 = PVector.add(midpoint, normal2);
        return PVector.dist(point1, ballPos) < PVector.dist(point2, ballPos) ? normal1 : normal2;
    }

    /**
     * Reflects the ball's velocity vector based on the normal vector of the surface it collides with.
     *
     * @param v The original velocity vector of the ball.
     * @param n The normal vector of the surface the ball collides with.
     * @return The new velocity vector after reflection.
     */
    private PVector reflect(PVector v, PVector n) {
        float dot = PVector.dot(v, n);
        return PVector.sub(v, PVector.mult(n, 2 * dot));
    }

    /**
     * Checks if a point is within the bounds of the player line.
     *
     * @param x The x-coordinate of the point to check.
     * @param y The y-coordinate of the point to check.
     * @return {@code true} if the point is within the bounds of the line, {@code false} otherwise.
     */
    public boolean containsPoint(float x, float y) {
        for (PVector p : points) {
            if (PVector.dist(p, new PVector(x, y)) < THICKNESS / 2) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether the player line has collided with a ball.
     *
     * @return {@code true} if the line has collided, {@code false} otherwise.
     */
    public boolean hasCollided() {
        return hasCollided;
    }

    /**
     * Returns the list of points that make up the player line.
     *
     * @return A list of {@code PVector} objects representing the points of the player line.
     */
    public List<PVector> getPoints() {
        return points;
    }
}
