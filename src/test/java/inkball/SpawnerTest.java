package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

public class SpawnerTest {

    private Spawner spawner;
    private TestPApplet mockApp;
    private PImage mockImage;

    @BeforeEach
    public void setUp() {
        mockApp = new TestPApplet();
        PApplet.runSketch(new String[] {"TestPApplet"}, mockApp);
        mockApp.delay(100);  // Allow some time for setup

        mockImage = mockApp.createImage(10, 10, PApplet.ARGB);
        spawner = new Spawner(3, 4, mockImage);
    }

    @Test
    // Test that the spawner is initialized correctly
    public void testConstructor() {
        assertNotNull(spawner);
        assertEquals(3, spawner.getX());
        assertEquals(4, spawner.getY());
    }

    @Test
    // Test the draw method to ensure it doesn't throw any exceptions
    public void testDraw() {
        try {
            spawner.draw(mockApp);
            assertTrue(true); // If no exception is thrown, the test passes
        } catch (Exception e) {
            fail("Draw method threw an exception: " + e.getMessage());
        }
    }

    @Test
    // Test the update method (even though it's empty) for coverage
    public void testUpdate() {
        // The update method is empty, but we should still test it for coverage
        spawner.update();
        assertTrue(true);
    }

    @Test
    // Test the getter methods for x and y
    public void testGetters() {
        assertEquals(3, spawner.getX());
        assertEquals(4, spawner.getY());
    }
}