package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

public class BallTest {
    private static final float ORIGINAL_RADIUS = 12f;
    private App app;
    private Ball ball;
    private PImage mockImage;

    @BeforeEach
    public void setUp() {
        app = new App();
        app.noLoop();
        PApplet.runSketch(new String[]{"Test"}, app);
        app.setup();
        mockImage = app.createImage(32, 32, PApplet.RGB);
        ball = new Ball(1, 1, mockImage, 1);
        App.random.setSeed(12345);
    }

    @Test
    // Tests the initialization of the ball, ensuring its position, type, and velocity are correctly set.
    public void testBallInitialization() {
        assertEquals(48, ball.getX(), 0.01);
        assertEquals(48, ball.getY(), 0.01);
        assertEquals(1, ball.getType());
        assertTrue(Math.abs(ball.getVx()) <= 2.0f);
        assertTrue(Math.abs(ball.getVy()) <= 2.0f);
    }

    @Test
    // Tests the update method, ensuring that the ball's position changes after an update.
    public void testBallUpdate() {
        float initialX = ball.getX();
        float initialY = ball.getY();
        ball.update();
        assertNotEquals(initialX, ball.getX());
        assertNotEquals(initialY, ball.getY());
    }

    @Test
    // Tests the updateType method to verify if the ball type is correctly updated.
    public void testBallUpdateType() {
        ball.updateType(2);
        assertEquals(2, ball.getType());
    }

    @Test
    // Tests the applyAttraction method, checking if the velocity changes when attraction forces are applied.
    public void testBallApplyAttraction() {
        float initialVx = ball.getVx();
        float initialVy = ball.getVy();
        ball.applyAttraction(0.1f, 0.1f);
        assertNotEquals(initialVx, ball.getVx());
        assertNotEquals(initialVy, ball.getVy());
    }

    @Test
    // Tests the adjustSize method, checking if the ball's radius is correctly adjusted based on distance.
    public void testBallAdjustSize() {
        // Scenario 1: Distance is less than or equal to maxDistance (adjust size)
        ball.setRadius(ORIGINAL_RADIUS); // Reset radius to original size
        float initialRadius = ball.getRadius();

        float distance = 10;
        float maxDistance = 32;
        ball.adjustSize(distance, maxDistance);
        assertNotEquals(initialRadius, ball.getRadius(), "Ball radius should be adjusted based on distance.");
        assertTrue(ball.getRadius() < ORIGINAL_RADIUS, "Adjusted radius should be smaller than original.");

        // Scenario 2: Distance is greater than maxDistance (no size adjustment, radius should be original)
        ball.setRadius(ORIGINAL_RADIUS); // Reset radius to original size
        float newDistance = 40; // Greater than maxDistance
        ball.adjustSize(newDistance, maxDistance);
        assertEquals(ORIGINAL_RADIUS, ball.getRadius(), "Ball radius should remain original when distance exceeds maxDistance.");
    }

    @Test
    // Tests the resetSize method, ensuring the ball's radius is reset to its original value.
    public void testBallResetSize() {
        ball.adjustSize(10, 32);
        ball.resetSize();
        assertEquals(12f, ball.getRadius(), 0.01);
    }

    @Test
    // Tests if the ball correctly detects when it overlaps with a hole.
    public void testBallIsOverlappingHole() {
        ball.setX(96);
        ball.setY(96);
        assertTrue(ball.isOverlappingHole(96, 96));
        assertFalse(ball.isOverlappingHole(100, 100));
    }

    @Test
    // Tests the captured state of the ball, ensuring it can be set and retrieved correctly.
    public void testBallCaptured() {
        assertFalse(ball.isCaptured());
        ball.setCaptured(true);
        assertTrue(ball.isCaptured());
    }

    @Test
    // Tests saving and restoring the velocity, ensuring the ball saves and restores velocity correctly.
    public void testBallSaveRestoreVelocity() {
        // Scenario 1: Ball has non-zero velocity, and it is saved and restored correctly.
        ball.setVx(1);
        ball.setVy(1);
        ball.saveVelocity();  // Saving velocity
        assertEquals(0, ball.getVx(), 0.01, "Ball's vx should be 0 after saving.");
        assertEquals(0, ball.getVy(), 0.01, "Ball's vy should be 0 after saving.");
        ball.restoreVelocity();  // Restoring velocity
        assertEquals(1, ball.getVx(), 0.01, "Ball's vx should be restored to 1.");
        assertEquals(1, ball.getVy(), 0.01, "Ball's vy should be restored to 1.");

        // Scenario 2: Ball already has zero velocity, so saveVelocity should not change saved values.
        ball.setVx(0);
        ball.setVy(0);
        ball.saveVelocity();  // Trying to save zero velocity
        assertEquals(0, ball.getVx(), 0.01, "Ball's vx should remain 0.");
        assertEquals(0, ball.getVy(), 0.01, "Ball's vy should remain 0.");

        // Scenario 3: Ball should not restore velocity if it's already moving (non-zero velocity)
        ball.setVx(2);
        ball.setVy(2);
        ball.restoreVelocity();  // Trying to restore velocity when it already has one
        assertEquals(2, ball.getVx(), 0.01, "Ball's vx should remain 2 (no restore).");
        assertEquals(2, ball.getVy(), 0.01, "Ball's vy should remain 2 (no restore).");

        // Scenario 4: Ball has velocity set to 0 after save, and it correctly restores.
        ball.setVx(3);
        ball.setVy(3);
        ball.saveVelocity();  // Save current velocity
        assertEquals(0, ball.getVx(), 0.01, "Ball's vx should be 0 after saving.");
        assertEquals(0, ball.getVy(), 0.01, "Ball's vy should be 0 after saving.");

        ball.restoreVelocity();  // Restoring the saved velocity
        assertEquals(3, ball.getVx(), 0.01, "Ball's vx should be restored to 3.");
        assertEquals(3, ball.getVy(), 0.01, "Ball's vy should be restored to 3.");
    }

    @Test
    // Tests the canAccelerate method, ensuring it correctly determines if the ball can accelerate.
    public void testBallCanAccelerate() {
        ball.setVx(3);
        ball.setVy(3);
        assertTrue(ball.canAccelerate());
        ball.setVx(5);
        ball.setVy(5);
        assertFalse(ball.canAccelerate());
    }
}