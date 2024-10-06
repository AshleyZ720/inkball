package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Ball implements Drawable {
    private float x, y; // Using float for position
    private PImage image;
    private int type;
    private float vx, vy; // Velocity in float
    private final int radius = 12; // Setting radius to 12px

    public Ball(int x, int y, PImage image, int type) {
        this.x = x * App.CELLSIZE + App.CELLSIZE / 2; // Adjusting to canvas coordinates
        this.y = y * App.CELLSIZE + App.CELLSIZE / 2;
        this.image = image;
//        this.vx = App.random.nextFloat() * 5 + 2; // Random speed
//        this.vy = App.random.nextFloat() * 5 + 2;
        this.type = type;
        // Set initial velocity
        float baseSpeed = 2.0f; // Base speed of 2 pixels per frame at 30 fps

        // Adjust speed if using 60 fps
        if (App.FPS == 60) {
            baseSpeed /= 2;
        }

        // Randomly set velocity to either -2 or 2 (or the adjusted value for 60 fps) for both x and y
        this.vx = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
        this.vy = App.random.nextBoolean() ? baseSpeed : -baseSpeed;
    }

    @Override
    public void draw(PApplet applet) {
        applet.image(image, x - radius, y - radius); // Drawing the ball centered at (x, y)
    }

    @Override
    public void update() {
        // Update position
        x += vx;
        y += vy;

        // Collision detection
        checkCollision();
    }

    private void checkCollision() {
        int gridX = (int) (x / App.CELLSIZE);
        int gridY = (int) (y / App.CELLSIZE);

        float totalCollisionX = 0;
        float totalCollisionY = 0;
        int collisionCount = 0;

        // Loop over neighboring tiles
        for (int i = gridX - 1; i <= gridX + 1; i++) {
            for (int j = gridY - 1; j <= gridY + 1; j++) {
                if (i >= 0 && i < App.BOARD_WIDTH && j >= 0 && j < App.BOARD_HEIGHT) {
                    Tile tile = App.getGrid()[i][j];
                    if (tile != null && tile.getDrawable() instanceof Wall) {
                        float left = i * App.CELLSIZE;
                        float right = left + App.CELLSIZE;
                        float top = j * App.CELLSIZE;
                        float bottom = top + App.CELLSIZE;

                        // Find the closest point on the wall to the ball
                        float closestX = clamp(x, left, right);
                        float closestY = clamp(y, top, bottom);

                        // Calculate the distance between the ball's center and this closest point
                        float distanceX = x - closestX;
                        float distanceY = y - closestY;

                        // If the distance is less than the radius, there's a collision
                        float distanceSquared = (distanceX * distanceX) + (distanceY * distanceY);
                        if (distanceSquared < radius * radius) {
                            float distance = (float) Math.sqrt(distanceSquared);
                            float overlap = radius - distance;

                            // Normalize the collision vector
                            float collisionX = distanceX / distance;
                            float collisionY = distanceY / distance;

                            totalCollisionX += collisionX;
                            totalCollisionY += collisionY;
                            collisionCount++;

                            // Adjust the ball's position to prevent overlap
                            x += collisionX * overlap / 2; // Divide by 2 to avoid over-correction
                            y += collisionY * overlap / 2;
                        }
                    }
                }
            }
        }

        // If there were any collisions, calculate the average collision vector
        if (collisionCount > 0) {
            float avgCollisionX = totalCollisionX / collisionCount;
            float avgCollisionY = totalCollisionY / collisionCount;

            // Normalize the average collision vector
            float magnitude = (float) Math.sqrt(avgCollisionX * avgCollisionX + avgCollisionY * avgCollisionY);
            avgCollisionX /= magnitude;
            avgCollisionY /= magnitude;

            // Reflect the velocity vector based on the average collision normal
            float dotProduct = vx * avgCollisionX + vy * avgCollisionY;
            vx -= 2 * dotProduct * avgCollisionX;
            vy -= 2 * dotProduct * avgCollisionY;
        }
    }

    private float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getVx() {
        return vx;
    }

    public void setVx(float vx) {
        this.vx = vx;
    }

    public float getVy() {
        return vy;
    }

    public void setVy(float vy) {
        this.vy = vy;
    }

    public int getRadius() {
        return radius;
    }
}