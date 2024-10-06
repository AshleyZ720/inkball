package inkball;

import processing.core.PApplet;
import processing.core.PVector;
import java.util.ArrayList;
import java.util.List;

public class PlayerLine implements Drawable, Collidable {
    private List<PVector> points;
    private static final float THICKNESS = 10;
    private boolean hasCollided;

    public PlayerLine() {
        points = new ArrayList<>();
        hasCollided = false;
    }

    public void addPoint(float x, float y) {
        points.add(new PVector(x, y));
    }

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

    @Override
    public void update() {
        // PlayerLine doesn't need to update its state every frame
        // but we need to implement this method as part of the Drawable interface
    }

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

    private PVector getNormal(PVector p1, PVector p2, PVector ballPos) {
        PVector lineVec = PVector.sub(p2, p1);
        PVector normal1 = new PVector(-lineVec.y, lineVec.x).normalize();
        PVector normal2 = PVector.mult(normal1, -1);
        PVector midpoint = PVector.add(p1, p2).div(2);
        PVector point1 = PVector.add(midpoint, normal1);
        PVector point2 = PVector.add(midpoint, normal2);
        return PVector.dist(point1, ballPos) < PVector.dist(point2, ballPos) ? normal1 : normal2;
    }

    private PVector reflect(PVector v, PVector n) {
        float dot = PVector.dot(v, n);
        return PVector.sub(v, PVector.mult(n, 2 * dot));
    }

    public boolean containsPoint(float x, float y) {
        for (PVector p : points) {
            if (PVector.dist(p, new PVector(x, y)) < THICKNESS / 2) {
                return true;
            }
        }
        return false;
    }

    public boolean hasCollided() {
        return hasCollided;
    }
}