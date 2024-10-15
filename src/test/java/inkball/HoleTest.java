package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import static org.junit.jupiter.api.Assertions.*;

public class HoleTest {
    private App app;
    private Hole hole;
    private Ball ball;
    private PImage mockImage;

    @BeforeEach
    public void setUp() {
        app = new App();
        app.noLoop();
        PApplet.runSketch(new String[]{"Test"}, app);
        app.setup();
        mockImage = app.createImage(32, 32, PApplet.RGB);
        hole = new Hole(2, 2, mockImage, 1);
        ball = new Ball(1, 1, mockImage, 1);
    }

    @Test
    // Tests the initialization of the hole, ensuring its position and type are correctly set.
    public void testHoleInitialization() {
        assertEquals(96, hole.getX(), 0.01);
        assertEquals(96, hole.getY(), 0.01);
        assertEquals(1, hole.getType());
    }

    @Test
    // Tests if the hole can attract a ball within the attraction radius and modifies the ball's velocity.
    public void testHoleAttractBall() {
        ball.setX(80);
        ball.setY(80);
        float initialVx = ball.getVx();
        float initialVy = ball.getVy();
        hole.attractBall(ball, app);
        assertNotEquals(initialVx, ball.getVx());
        assertNotEquals(initialVy, ball.getVy());
    }

    @Test
    // Tests if the hole captures a ball when it's within range.
    public void testHoleCaptureBall() {
        ball.setX(96);
        ball.setY(96);
        hole.attractBall(ball, app);
        assertTrue(ball.isCaptured());
    }

    @Test
    // Tests if the hole does not affect a ball outside the attraction radius.
    public void testHoleAttractBallOutsideRadius() {
        ball.setX(200);
        ball.setY(200);
        float initialVx = ball.getVx();
        float initialVy = ball.getVy();
        hole.attractBall(ball, app);
        assertEquals(initialVx, ball.getVx(), 0.01);
        assertEquals(initialVy, ball.getVy(), 0.01);
    }

    @Test
    // Tests if the hole attracts and captures a wrong-type ball and decreases the score.
    public void testHoleAttractWrongTypeBall() {
        Ball wrongTypeBall = new Ball(1, 1, mockImage, 2);
        wrongTypeBall.setX(96);
        wrongTypeBall.setY(96);
        app.getDrawables().add(wrongTypeBall);
        int initialScore = app.score;
        hole.attractBall(wrongTypeBall, app);
        assertTrue(wrongTypeBall.isCaptured());
        assertTrue(app.score < initialScore);
    }

    @Test
    // Tests if the hole correctly attracts and captures a grey ball, increasing the score.
    public void testHoleAttractGreyBall() {
        Ball grayBall = new Ball(1, 1, mockImage, 0);
        grayBall.setX(96);
        grayBall.setY(96);
        app.getDrawables().add(grayBall);
        int initialScore = app.score;
        hole.attractBall(grayBall, app);
        assertTrue(grayBall.isCaptured());
        assertTrue(app.score > initialScore);
    }

    @Test
    // Tests if a grey hole can attract and capture any ball, increasing the score.
    public void testHoleAttractBallToGreyHole() {
        Hole grayHole = new Hole(2, 2, mockImage, 0);
        ball.setX(96);
        ball.setY(96);
        app.getDrawables().add(ball);
        int initialScore = app.score;
        grayHole.attractBall(ball, app);
        assertTrue(ball.isCaptured());
        assertTrue(app.score > initialScore);
    }
}