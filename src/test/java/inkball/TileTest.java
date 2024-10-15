package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.core.PImage;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

public class TileTest {

    private Tile tile;
    private TestPApplet mockApp;
    private PImage baseImage;
    private PImage overlayImage;
    private Drawable drawable;

    /**
     * Internal subclass of PApplet for testing draw methods.
     */
    private static class TestPApplet extends PApplet {
        public void settings() {
            size(200, 200);  // Setup a canvas size
        }

        public void setup() {
            noLoop();  // Prevent continuous looping of draw()
        }

        @Override
        public void draw() {
            // Intentionally left empty for testing
        }
    }

    @BeforeEach
    public void setUp() {
        mockApp = new TestPApplet();
        PApplet.runSketch(new String[] { "TestPApplet" }, mockApp);
        mockApp.setup();
        mockApp.delay(100);  // Allow some time for setup

        // Initialize mock PImage instances (can be null if image loading isn't required)
        baseImage = null; // Alternatively, you can load a test image if needed
        overlayImage = null;
        drawable = new DummyDrawable(); // Using a dummy drawable implementation

        tile = new Tile(2, 3, drawable, baseImage, overlayImage);
    }

    /**
     * Dummy implementation of Drawable for testing purposes.
     */
    private static class DummyDrawable implements Drawable {
        @Override
        public void draw(PApplet app) {
            // Intentionally left empty
        }

        @Override
        public void update() {
            // Intentionally left empty
        }
    }

    @Test
    // Test that the Tile constructor initializes all fields correctly.
    public void testConstructorInitialization() {
        assertEquals(2, tile.getX(), "Tile X position should be initialized correctly.");
        assertEquals(3, tile.getY(), "Tile Y position should be initialized correctly.");
        assertEquals(drawable, tile.getDrawable(), "Drawable should be set correctly.");
        assertEquals(overlayImage, tile.getOverlayImage(), "Overlay image should be set correctly.");
        assertFalse(tile.isCovered(), "Tile should not be covered initially.");
    }

    @Test
    // Test the getDrawable and setDrawable methods.
    public void testGetSetDrawable() {
        Drawable newDrawable = new DummyDrawable();
        tile.setDrawable(newDrawable);
        assertEquals(newDrawable, tile.getDrawable(), "Drawable should be updated correctly.");
    }

    @Test
    // Test the isEmpty method when drawable is not null.
    public void testIsEmptyFalse() {
        assertFalse(tile.isEmpty(), "Tile should not be empty when drawable is set.");
    }

    @Test
    // Test the isEmpty method when drawable is null.
    public void testIsEmptyTrue() {
        tile.setDrawable(null);
        assertTrue(tile.isEmpty(), "Tile should be empty when drawable is null.");
    }

    @Test
    // Test the setCovered and isCovered methods.
    public void testSetIsCovered() {
        tile.setCovered(true);
        assertTrue(tile.isCovered(), "Tile should be marked as covered.");
        tile.setCovered(false);
        assertFalse(tile.isCovered(), "Tile should be marked as not covered.");
    }

    @Test
    // Test the setOverlayImage method.
    public void testSetOverlayImage() {
        PImage newOverlay = null; // Alternatively, load a test image if needed
        tile.setOverlayImage(newOverlay);
        assertEquals(newOverlay, tile.getOverlayImage(), "Overlay image should be updated correctly.");
    }

    @Test
    // Test the resetOverlayImage method.
    public void testResetOverlayImage() {
        PImage newOverlay = null; // Alternatively, set to a different image
        tile.setOverlayImage(newOverlay);
        tile.resetOverlayImage();
        assertEquals(overlayImage, tile.getOverlayImage(), "Overlay image should be reset to original.");
    }

    @Test
    // Test the draw method does not throw exceptions when images and drawable are present.
    public void testDrawWithImagesAndDrawable() {
        assertDoesNotThrow(() -> tile.draw(mockApp), "Draw method should not throw exception with valid images and drawable.");
        // Since images are null, we cannot verify image calls. If images are not null, you can verify PApplet.image calls.
        // Example:
        // verify(mockApp, times(1)).image(baseImage, 2 * App.CELLSIZE, 3 * App.CELLSIZE);
        // verify(mockApp, times(1)).image(overlayImage, 2 * App.CELLSIZE, 3 * App.CELLSIZE);
        // However, without a mocking framework, verifying method calls is not straightforward.
    }

    @Test
    // Test the draw method when overlayImage is null.
    public void testDrawWithNullOverlayImage() {
        tile.setOverlayImage(null);
        assertDoesNotThrow(() -> tile.draw(mockApp), "Draw method should not throw exception when overlayImage is null.");
    }

    @Test
    // Test the draw method when drawable is null.
    public void testDrawWithNullDrawable() {
        tile.setDrawable(null);
        assertDoesNotThrow(() -> tile.draw(mockApp), "Draw method should not throw exception when drawable is null.");
    }

    @Test
    //Test that the update method does not throw exceptions.
    public void testUpdate() {
        assertDoesNotThrow(tile::update, "Update method should not throw exception.");
    }
}
