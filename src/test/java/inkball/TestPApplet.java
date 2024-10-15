package inkball;

import processing.core.PApplet;
import processing.core.PImage;

import java.util.ArrayList;
import java.util.List;

/**
 * Internal subclass used for testing to prevent actual drawing and capture calls to the image method.
 */
class TestPApplet extends PApplet {
    // List to record all calls to the image method
    List<ImageCall> imageCalls = new ArrayList<>();

    /**
     * Overrides the image method to capture each call and its parameters.
     * @param img The image to be drawn
     * @param x The x-coordinate of the image
     * @param y The y-coordinate of the image
     */
    @Override
    public void image(PImage img, float x, float y) {
        // Capture each call to the image method and its parameters
        imageCalls.add(new ImageCall(img, x, y));
    }

    /**
     * Internal class to store the parameters of image method calls.
     */
    static class ImageCall {
        PImage img;
        float x;
        float y;

        /**
         * Constructor for ImageCall.
         * @param img The image being drawn
         * @param x The x-coordinate of the image
         * @param y The y-coordinate of the image
         */
        ImageCall(PImage img, float x, float y) {
            this.img = img;
            this.x = x;
            this.y = y;
        }
    }

    /**
     * Sets up the initial canvas size.
     */
    @Override
    public void settings() {
        size(1, 1);  // Set minimum canvas size to reduce resource consumption
    }

    /**
     * Initializes the sketch.
     */
    @Override
    public void setup() {
        noLoop();  // Prevent continuous looping of the draw method
        surface.setVisible(false); // Immediately hide the window
    }

    /**
     * Main drawing function (left empty to prevent any actual drawing).
     */
    @Override
    public void draw() {
        // Left empty, no drawing operations executed
    }
}
