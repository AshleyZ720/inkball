package inkball;

/**
 * The {@code Collidable} interface defines a contract for any object that can
 * detect and respond to collisions with a {@code Ball} in the Inkball game.
 * Classes implementing this interface must define how collisions are detected
 * and handled.
 */
public interface Collidable {

    /**
     * Checks if a collision has occurred between this object and the specified ball.
     * If a collision is detected, the method can adjust the ball's properties (such as position or velocity)
     * to handle the collision.
     *
     * @param ball The {@code Ball} object to check for collision with.
     * @return {@code true} if a collision is detected, {@code false} otherwise.
     */
    boolean checkCollision(Ball ball);
}
