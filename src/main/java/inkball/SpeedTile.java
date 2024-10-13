package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class SpeedTile implements Drawable {
    private int x;
    private int y;
    private PImage image;
    private char direction;

    public SpeedTile(int x, int y, PImage image, char direction) {
        this.x = x;
        this.y = y;
        this.image = image;
        this.direction = direction;
    }

    @Override
    public void draw(PApplet app) {
        app.image(image, x * App.CELLSIZE, y * App.CELLSIZE);
    }

    @Override
    public void update() {

    }

    public void applySpeedBoost(Ball ball) {
        if (!ball.canAccelerate()) {
            return;  // 如果球已经加速3次，直接返回
        }

        float speedMultiplier = 1.05f;
        boolean shouldAccelerate = false;

        switch (direction) {
            case '^':
                if (ball.getVy() < 0) shouldAccelerate = true;
                break;
            case 'v':
                if (ball.getVy() > 0) shouldAccelerate = true;
                break;
            case '>':
                if (ball.getVx() > 0) shouldAccelerate = true;
                break;
            case '<':
                if (ball.getVx() < 0) shouldAccelerate = true;
                break;
        }

        if (shouldAccelerate && ball.canAccelerate()) {
            System.out.println("1 ball:" + ball.getVx() + ", " + ball.getVy());
            ball.setVx(ball.getVx() * speedMultiplier);
            ball.setVy(ball.getVy() * speedMultiplier);
            System.out.println("2 ball:" + ball.getVx() + ", " + ball.getVy() + ", " + ball.getType());
            System.out.println(" ");
        }
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public char getDirection() {
        return direction;
    }
}
