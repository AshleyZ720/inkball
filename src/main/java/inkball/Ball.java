package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class Ball implements Drawable {
    private float x, y; // 改为 float 类型
    private PImage image;
    private int radius;
    private float vx, vy; // 速度改为 float 类型

    public Ball(int x, int y, PImage image, int radius) {
        this.x = x * App.CELLSIZE + App.CELLSIZE / 2; // 调整为画布坐标
        this.y = y * App.CELLSIZE + App.CELLSIZE / 2;
        this.image = image;
        this.radius = radius;
        this.vx = App.random.nextFloat() * 5 + 2; // 随机速度
        this.vy = App.random.nextFloat() * 5 + 2; // 随机速度
    }

    @Override
    public void draw(PApplet applet) {
        applet.image(image, x - radius, y - radius);
    }

    @Override
    public void update() {
        // 更新位置
        x += vx;
        y += vy;

        // 碰撞检测
        checkCollision();
    }

    private void checkCollision() {
        // 检测与墙壁的碰撞
        int gridX = (int) (x / App.CELLSIZE);
        int gridY = (int) (y / App.CELLSIZE);

        // 检查四个方向的墙壁
        if (gridX < 0 || gridX >= App.BOARD_WIDTH || gridY < 0 || gridY >= App.BOARD_HEIGHT) {
            // 碰到边界
            if (gridX < 0 || gridX >= App.BOARD_WIDTH) vx *= -1;
            if (gridY < 0 || gridY >= App.BOARD_HEIGHT) vy *= -1;
        } else {
            Tile tile = App.getGrid()[gridX][gridY];
            if (tile != null && tile.getDrawable() instanceof Wall) {
                if (Math.abs(x % App.CELLSIZE) < radius || Math.abs(y % App.CELLSIZE) < radius) {
                    vx *= -1; // 反弹
                }
            }
        }
    }
}

