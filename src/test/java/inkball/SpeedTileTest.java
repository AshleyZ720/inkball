package inkball;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;
import static org.junit.jupiter.api.Assertions.*;

public class SpeedTileTest {

    private SpeedTile speedTile;
    private TestPApplet mockApp;
    private PImage mockImage;
    private TestBall testBall;

    // Internal class used for testing
    private static class TestPApplet extends PApplet {
        public void settings() {
            size(200, 200);
        }

        public void setup() {
            noLoop();
        }
    }

    // Test implementation of Ball for controlled testing
    private static class TestBall extends Ball {
        private boolean canAccelerate = true;

        public TestBall(float x, float y, PImage image, int type) {
            super((int) x, (int) y, image, type);
        }

        @Override
        public boolean canAccelerate() {
            return canAccelerate;
        }

        public void setCanAccelerate(boolean canAccelerate) {
            this.canAccelerate = canAccelerate;
        }
    }

    @BeforeEach
    public void setUp() {
        mockApp = new TestPApplet();
        PApplet.runSketch(new String[] {"TestPApplet"}, mockApp);
        mockApp.delay(100);

        mockImage = mockApp.createImage(10, 10, PApplet.ARGB);
        speedTile = new SpeedTile(3, 4, mockImage, '^');

        testBall = new TestBall(50, 50, mockImage, 0);
    }

    @Test
    // Test the constructor to ensure correct initialization
    public void testConstructor() {
        assertEquals(3, speedTile.getX());
        assertEquals(4, speedTile.getY());
        assertEquals('^', speedTile.getDirection());
    }

    @Test
    // Test the draw method to ensure it doesn't throw any exceptions
    public void testDraw() {
        try {
            speedTile.draw(mockApp);
            assertTrue(true); // If no exception is thrown, the test passes
        } catch (Exception e) {
            fail("Draw method threw an exception: " + e.getMessage());
        }
    }

    @Test
    // Test the update method to ensure it can be called without error
    public void testUpdate() {
        speedTile.update();
        assertTrue(true); // The method is empty, so we just ensure it doesn't throw an exception
    }

    @Test
    // Test applying speed boost when the ball is moving upward
    public void testApplySpeedBoostUpward() {
        testBall.setVx(0);
        testBall.setVy(-1);
        speedTile.applySpeedBoost(testBall);
        assertEquals(0, testBall.getVx(), 0.001f);
        assertEquals(-1.05f, testBall.getVy(), 0.001f);
    }

    @Test
    // Test applying speed boost when the ball is moving downward
    public void testApplySpeedBoostDownward() {
        SpeedTile downSpeedTile = new SpeedTile(3, 4, mockImage, 'v');
        testBall.setVx(0);
        testBall.setVy(1);
        downSpeedTile.applySpeedBoost(testBall);
        assertEquals(0, testBall.getVx(), 0.001f);
        assertEquals(1.05f, testBall.getVy(), 0.001f);
    }

    @Test
    // Test applying speed boost when the ball is moving rightward
    public void testApplySpeedBoostRightward() {
        SpeedTile rightSpeedTile = new SpeedTile(3, 4, mockImage, '>');
        testBall.setVx(1);
        testBall.setVy(0);
        rightSpeedTile.applySpeedBoost(testBall);
        assertEquals(1.05f, testBall.getVx(), 0.001f);
        assertEquals(0, testBall.getVy(), 0.001f);
    }

    @Test
    // Test applying speed boost when the ball is moving leftward
    public void testApplySpeedBoostLeftward() {
        SpeedTile leftSpeedTile = new SpeedTile(3, 4, mockImage, '<');
        testBall.setVx(-1);
        testBall.setVy(0);
        leftSpeedTile.applySpeedBoost(testBall);
        assertEquals(-1.05f, testBall.getVx(), 0.001f);
        assertEquals(0, testBall.getVy(), 0.001f);
    }

    @Test
    // Test applying speed boost when the ball cannot accelerate
    public void testApplySpeedBoostNoAcceleration() {
        testBall.setCanAccelerate(false);
        testBall.setVx(1);
        testBall.setVy(1);
        speedTile.applySpeedBoost(testBall);
        assertEquals(1, testBall.getVx(), 0.001f);
        assertEquals(1, testBall.getVy(), 0.001f);
    }

    @Test
    // Test applying speed boost in the wrong direction (ball moving downward but tile boosts upward)
    public void testApplySpeedBoostWrongDirection() {
        testBall.setVx(0);
        testBall.setVy(1);
        speedTile.applySpeedBoost(testBall);
        assertEquals(0, testBall.getVx(), 0.001f);
        assertEquals(1, testBall.getVy(), 0.001f);
    }

    @Test
    // Test the getter methods for x, y, and direction
    public void testGetters() {
        assertEquals(3, speedTile.getX());
        assertEquals(4, speedTile.getY());
        assertEquals('^', speedTile.getDirection());
    }
}
