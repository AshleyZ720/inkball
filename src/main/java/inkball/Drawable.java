package inkball;

import processing.core.PApplet;

/**
 * The {@code Drawable} interface defines the behavior for objects that can be
 * drawn and updated within the Inkball game. Any object that implements this
 * interface should provide mechanisms to visually represent itself on the game
 * canvas and update its internal state as necessary.
 */
public interface Drawable {

    /**
     * Draws the object on the specified {@link PApplet} instance. This method
     * is responsible for rendering the object visually on the game screen.
     *
     * @param app the {@link PApplet} instance representing the game window or canvas
     */
    void draw(PApplet app);

    /**
     * Updates the state of the object, typically called once per game frame.
     * This method should handle any necessary logic updates, such as movement or
     * state transitions.
     */
    void update();
}
