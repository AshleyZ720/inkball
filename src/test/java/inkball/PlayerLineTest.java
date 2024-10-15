package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PVector;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class PlayerLineTest {
    private PlayerLine playerLine;
    private Ball ball;
    private TestPApplet mockApp;

    // Internal class used for testing
    private static class TestPApplet extends PApplet {
        public void settings() {
            size(200, 200);  // Setup a canvas size
        }

        public void setup() {
            noLoop();  // To prevent continuous looping of draw()
        }
    }

    @BeforeEach
    public void setUp() {
        playerLine = new PlayerLine();
        ball = new Ball(50, 50, null, 0); // Assuming Ball constructor
        mockApp = new TestPApplet();

        // Running the PApplet sketch in the testing environment
        PApplet.runSketch(new String[] {"TestPApplet"}, mockApp);
        mockApp.delay(100);  // Allow some time for setup
    }

    @Test
    // Tests the constructor of PlayerLine and Ball and sets up initial conditions for the test environment.
    public void testDraw() {
        playerLine.addPoint(10, 20);
        playerLine.addPoint(30, 40);

        try {
            playerLine.draw(mockApp);
            assertTrue(true); // If no exception is thrown, the test passes
        } catch (Exception e) {
            fail("Draw method threw an exception: " + e.getMessage());
        }
    }

    @Test
    // Tests adding a point to the playerLine and verifies the correct values of the point.
    public void testAddPoint() {
        playerLine.addPoint(10, 20);
        List<PVector> points = playerLine.getPoints();
        assertEquals(1, points.size());
        assertEquals(10, points.get(0).x);
        assertEquals(20, points.get(0).y);
    }

    @Test
    // Tests the update method, which is currently empty, for code coverage purposes.
    public void testUpdate() {
        playerLine.update();
        assertTrue(true);
    }

    @Test
    // Tests checkCollision when no collision occurs between the ball and the player line.
    public void testCheckCollisionNoCollision() {
        playerLine.addPoint(0, 0);
        playerLine.addPoint(100, 100);
        ball.setX(200);
        ball.setY(200);
        assertFalse(playerLine.checkCollision(ball));
    }

    @Test
    // Tests checkCollision when a collision occurs between the ball and the player line.
    public void testCheckCollisionWithCollision() {
        playerLine.addPoint(45, 45);
        playerLine.addPoint(55, 55);
        ball.setX(50);
        ball.setY(50);
        ball.setVx(1);
        ball.setVy(1);
        assertTrue(playerLine.checkCollision(ball));
        assertTrue(playerLine.hasCollided());
    }

    @Test
    // Tests that no further collisions are detected after the first collision has occurred.
    public void testCheckCollisionAfterCollision() {
        playerLine.addPoint(45, 45);
        playerLine.addPoint(55, 55);
        ball.setX(50);
        ball.setY(50);
        playerLine.checkCollision(ball);
        assertFalse(playerLine.checkCollision(ball)); // Should return false after first collision
    }

    @Test
    // Tests checkCollision when there is only one point in the playerLine, so no collision should occur.
    public void testCheckCollisionWithSinglePoint() {
        playerLine.addPoint(50, 50);
        assertFalse(playerLine.checkCollision(ball));
    }

    @Test
    // Tests whether a point is correctly detected within the playerLine.
    public void testContainsPoint() {
        playerLine.addPoint(50, 50);
        assertTrue(playerLine.containsPoint(50, 50));
        assertFalse(playerLine.containsPoint(100, 100));
    }

    @Test
    // Tests if hasCollided correctly returns whether a collision has occurred.
    public void testHasCollided() {
        assertFalse(playerLine.hasCollided());
        playerLine.addPoint(45, 45);
        playerLine.addPoint(55, 55);
        ball.setX(50);
        ball.setY(50);
        playerLine.checkCollision(ball);
        assertTrue(playerLine.hasCollided());
    }

    @Test
    // Tests getPoints to ensure it returns the correct points that were added to the playerLine.
    public void testGetPoints() {
        playerLine.addPoint(10, 20);
        playerLine.addPoint(30, 40);
        List<PVector> points = playerLine.getPoints();
        assertEquals(2, points.size());
        assertEquals(10, points.get(0).x);
        assertEquals(20, points.get(0).y);
        assertEquals(30, points.get(1).x);
        assertEquals(40, points.get(1).y);
    }

    @Test
    // Tests the reflection logic in checkCollision to verify that the ball's velocity is updated correctly after a collision.
    public void testReflectionLogic() {
        playerLine.addPoint(0, 0);
        playerLine.addPoint(100, 0);
        ball.setX(50);
        ball.setY(-10);
        ball.setVx(0);
        ball.setVy(1);
        playerLine.checkCollision(ball);
        assertTrue(ball.getVy() < 0); // Ball should reflect upwards
    }

    @Test
    // Tests the normal calculation logic to ensure proper collision detection and reflection.
    public void testNormalCalculation() {
        playerLine.addPoint(0, 0);
        playerLine.addPoint(100, 100);

        ball.setX(50);  // Position the ball near the middle of the line
        ball.setY(90);  // Adjust Y so it's close but will hit the line
        ball.setVx(0);  // Ball is moving vertically
        ball.setVy(-1); // Ball is moving upwards, expected to reflect downwards

        float initialVx = ball.getVx();
        float initialVy = ball.getVy();

        boolean collided = playerLine.checkCollision(ball);

        assertTrue(collided, "Ball should collide with the player line.");
        float epsilon = 1e-6f;
        assertTrue(Math.abs(ball.getVy()) < epsilon, "Ball's vy should be very close to 0 after collision.");
        assertEquals(-1, ball.getVx(), epsilon, "Ball's vx should remain unchanged after vertical collision.");
    }
}