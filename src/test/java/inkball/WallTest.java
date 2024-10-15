package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import static org.junit.jupiter.api.Assertions.*;

public class WallTest {
    private App app;
    private Wall wall;
    private Ball ball;
    private PImage mockImage;

    @BeforeEach
    public void setUp() {
        app = new App();
        app.noLoop();
        PApplet.runSketch(new String[]{"Test"}, app);
        app.setup();
        mockImage = app.createImage(32, 32, PApplet.RGB);
        wall = new Wall(1, 1, mockImage, 1);
        ball = new Ball(2, 2, mockImage, 0);
    }

    @Test
    // Test the constructor to ensure correct wall initialization
    public void testWallInitialization() {
        assertEquals(1, wall.getX());
        assertEquals(1, wall.getY());
        assertEquals(1, wall.getType());
    }

    @Test
    // Test wall collision detection (both collision and non-collision scenarios)
    public void testWallCollisionDetection() {
        ball.setX(wall.getX() * App.CELLSIZE);
        ball.setY(wall.getY() * App.CELLSIZE);
        assertTrue(wall.checkCollision(ball));

        ball.setX((wall.getX() + 2) * App.CELLSIZE);
        ball.setY((wall.getY() + 2) * App.CELLSIZE);
        assertFalse(wall.checkCollision(ball));
    }

    @Test
    // Test wall collision response (check if ball velocity reflects after collision)
    public void testWallCollisionResponse() {
        ball.setX(wall.getX() * App.CELLSIZE + App.CELLSIZE);
        ball.setY(wall.getY() * App.CELLSIZE + App.CELLSIZE);
        ball.setVx(-1);
        ball.setVy(-1);

        float initialVx = ball.getVx();
        float initialVy = ball.getVy();

        boolean collided = wall.checkCollision(ball);

        assertTrue(collided, "Ball should collide with the wall");

        if (collided) {
            assertTrue(ball.getVx() != initialVx || ball.getVy() != initialVy,
                    "Ball velocity should change after collision. " +
                            "Initial velocity: (" + initialVx + ", " + initialVy + "), " +
                            "Final velocity: (" + ball.getVx() + ", " + ball.getVy() + ")");
        }
    }

    @Test
    // Test that the ball's type changes when it collides with a wall of a different type
    public void testWallTypeChange() {
        Wall colorWall = new Wall(1, 1, mockImage, 2);
        ball.setX(colorWall.getX() * App.CELLSIZE);
        ball.setY(colorWall.getY() * App.CELLSIZE);
        colorWall.checkCollision(ball);
        assertEquals(2, ball.getType());
    }

    @Test
    public void testWallUpdate() {
        // Wall doesn't need to update, so this method should do nothing
        wall.update();
        // We can only assert that the method doesn't throw an exception
        // Add more specific assertions if the Wall class has any state that might change during update
    }
}