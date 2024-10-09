package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import javax.swing.*;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App extends PApplet{

    public static final int CELLSIZE = 32; //8;
    public static final int CELLHEIGHT = 32;

    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 576; //CELLSIZE*BOARD_WIDTH;
    public static int HEIGHT = 640 + 70; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 18;
    public static final int TOP_MARGIN = 70;

    public static final int INITIAL_PARACHUTES = 1;

    public static final float FPS = 30.0f;

    private JSONObject config;
    private JSONArray levels;
    private int currentLevelIndex = 0;
    private Level currentLevel;
    private float timeBonus = 0;

    public String configPath;
    private static Tile[][] grid;
    private List<Drawable> drawables;
    private PImage wall0, wall1, wall2, wall3, wall4;
    private static PImage ball0;
    private static PImage ball1;
    private static PImage ball2;
    private static PImage ball3;
    private static PImage ball4;
    private PImage spawner, hole;
    private float levelTimer = 0; // Example timer in frames (10 seconds at 30 FPS)
    private boolean levelEnded = false;
    private boolean levelWon = false;
    private PImage tileBaseImage;
    private boolean playingWinAnimation = false;
    private boolean noBallsOnBoard = false;
    private boolean gameOver = false;
    private boolean paused = false;
    private float animationTimer = 0;
    private float animationInterval = 0.067f * FPS; // 每0.067秒
    private int animationStep = 0;

    private int leftX, leftY;  // 左上角当前位置
    private int rightX, rightY;
    private int lastLeftX = -1, lastLeftY = -1;
    private int lastRightX = -1, lastRightY = -1;

    public static Random random = new Random();

    private List<PlayerLine> playerLines;
    private PlayerLine currentLine;

    private int score = 0;
    private List<Ball> ballsToRespawn = new ArrayList<>();
    private List<Spawner> spawners = new ArrayList<>();

    private List<String> ballQueue; // 未生成的球队列
    private float spawnTimer;

    public App() {
        this.configPath = "config.json";
    }

    /**
     * Initialise the setting of the window size.
     */
    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
    }

    /**
     * Load all resources such as images. Initialise the elements such as the player and map elements.
     */
    @Override
    public void setup() {
        try {
            frameRate(FPS);
            tileBaseImage = loadImage("src/main/resources/inkball/tile.png");
            loadImages();

            // 加载配置文件
            config = loadJSONObject("config.json");
            if (config == null) {
                System.err.println("Failed to load config.json");
            } else {
                System.out.println("Config loaded successfully: " + config);
            }

            levels = config.getJSONArray("levels");
            playerLines = new ArrayList<>();

            // 加载第一个关卡
            loadLevel(currentLevelIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PImage loadImageFromResources(String filename) throws UnsupportedEncodingException {
        String imagePath = URLDecoder.decode(
                this.getClass().getResource("/inkball/" + filename + ".png").getPath(),
                StandardCharsets.UTF_8.name()
        );
        return loadImage(imagePath);
    }

    private void loadImages() {
        try {
            wall0 = loadImageFromResources("wall0");
            wall1 = loadImageFromResources("wall1");
            wall2 = loadImageFromResources("wall2");
            wall3 = loadImageFromResources("wall3");
            wall4 = loadImageFromResources("wall4");
            ball0 = loadImageFromResources("ball0");
            ball1 = loadImageFromResources("ball1");
            ball2 = loadImageFromResources("ball2");
            ball3 = loadImageFromResources("ball3");
            ball4 = loadImageFromResources("ball4");
            spawner = loadImage("src/main/resources/inkball/entrypoint.png");
            hole = loadImage("src/main/resources/inkball/hole0.png");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading images: " + e.getMessage());
        }
    }

    public void loadLevel(int levelIndex) {
        if (levelIndex >= levels.size()) {
            gameOver = true;
            displayMessage("=== ENDED ===");
            return;
        }

        JSONObject levelData = levels.getJSONObject(levelIndex);
        currentLevel = new Level(levelData);

        // 重置关卡状态
        levelTimer = currentLevel.time * FPS; // 将秒转换为帧数
        spawnTimer = currentLevel.spawnInterval * FPS; // 将秒转换为帧数

        // 加载关卡布局
        loadLevelLayout(currentLevel.layout);

        // 初始化未生成的球队列
        ballQueue = new ArrayList<>(currentLevel.balls);

        // 其他初始化
        score = 0;
        ballsToRespawn.clear();

        levelEnded = false;
        levelWon = false;

        spawnNextBall();
    }

    public void loadLevelLayout(String fileName) {
        String[] rows = loadStrings(fileName);
        grid = new Tile[BOARD_WIDTH][BOARD_HEIGHT];
        drawables = new ArrayList<>();

        for (int y = 0; y < rows.length; y++) {
            String row = rows[y];
            for (int x = 0; x < row.length(); x++) {
                char tileType = row.charAt(x);
                if ((tileType == 'H' || tileType == 'B') && x + 1 < row.length()) {
                    char typeNumber = row.charAt(x + 1);
                    if (Character.isDigit(typeNumber)) {
                        String compositeType = "" + tileType + typeNumber;
                        //int type = getTileType(compositeType);
                        PImage overlayImg = getTileOverlayImage(compositeType);

                        Drawable drawable;
                        if (tileType == 'H') {
                            drawable = new Hole(x, y, overlayImg, typeNumber - '0');
                            //Hole hole = new Hole(x, y, overlayImg, typeNum);

                            grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                            grid[x + 1][y] = new Tile(x + 1, y, null, tileBaseImage, null);
                            grid[x][y + 1] = new Tile(x, y + 1, null, tileBaseImage, null);
                            grid[x + 1][y + 1] = new Tile(x + 1, y + 1, null, tileBaseImage, null);
                            grid[x + 1][y].setCovered(true);
                            grid[x][y + 1].setCovered(true);
                            grid[x + 1][y + 1].setCovered(true);
                            //drawables.add(grid[x + 1][y]);
                            //drawables.add(grid[x][y + 1]);
                            //drawables.add(grid[x + 1][y + 1]);
                        } else {
                            drawable = new Ball(x, y, overlayImg, typeNumber - '0');
                            grid[x + 1][y] = new Tile(x + 1, y, null, tileBaseImage, null);
                            grid[x][y] = new Tile(x, y, null, tileBaseImage, null);
                            //grid[x + 1][y] = new Tile(x + 1, y, Tile.EMPTY, tileBaseImage, null);
                        }
                        drawables.add(drawable);

                        x += 1;
                        continue;
                    }
                }

                PImage overlayImg = getTileOverlayImage(Character.toString(tileType));
                if (tileType == '1' || tileType == '2' || tileType == '3' || tileType == '4') {
                    Drawable drawable = getDrawableForTileType(Character.toString(tileType), x, y, overlayImg, tileType);
                    grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                    if (drawable != null) {
                        drawables.add(drawable);
                    }
                } else if (tileType == 'S') {
                    Spawner spawner = new Spawner(x, y, overlayImg);
                    grid[x][y] = new Tile(x, y, spawner, tileBaseImage, overlayImg);

                    drawables.add(spawner);
                    spawners.add(spawner);
                    //System.out.println("Added Spawner at (" + x + ", " + y + ")");

                } else {
                    Drawable drawable = getDrawableForTileType(Character.toString(tileType), x, y, overlayImg, '0');
                    grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                    if (drawable != null) {
                        drawables.add(drawable);
                    }
                }


            }
        }
        //levelEnded = false;
        //levelTimer = 300;
    }

    private Class<? extends Drawable> getTileType(String tileType) {
        switch (tileType) {
            case "X": return Wall.class;
            case "1": case "2": case "3": case "4": return Wall.class;
            case "S": return Spawner.class;
            case "H0": case "H1": case "H2": case "H3": case "H4": return Hole.class;
            case "B0": case "B1": case "B2": case "B3": case "B4": return Ball.class;
            default: return null;
        }
    }

    private Drawable getDrawableForTileType(String tileType, int x, int y, PImage overlayImage, char typeNumber) {
        switch (tileType) {
            case "X":
            case "1":
            case "2":
            case "3":
            case "4":
                return new Wall(x, y, overlayImage, typeNumber - '0');
            case "S":
                return new Spawner(x, y, overlayImage);
            case "H0":
            case "H1":
            case "H2":
            case "H3":
            case "H4":
                return new Hole(x, y, overlayImage, typeNumber - '0');
            case "B0":
            case "B1":
            case "B2":
            case "B3":
            case "B4":
                return new Ball(x, y, overlayImage, typeNumber - '0');
            default:
                return null;
        }
    }

    // Get the appropriate image based on the tile type
    private PImage getTileOverlayImage(String tileType) {
        switch (tileType) {
            case "X": return wall0;
            case "1": return wall1;
            case "2": return wall2;
            case "3": return wall3;
            case "4": return wall4;
            case "S": return spawner;
            case "H0": return hole; // Extend this for different hole images
            case "H1": case "H2": case "H3": case "H4": return loadImage("src/main/resources/inkball/hole" + tileType.charAt(1) + ".png");
            case "B0": return ball0;
            case "B1": case "B2": case "B3": case "B4": return loadImage("src/main/resources/inkball/ball" + tileType.charAt(1) + ".png");
            default: return null;
        }
    }

    /**
     * Receive key pressed signal from the keyboard.
     */


    @Override
    public void keyPressed(KeyEvent event) {
        if (key == ' ' && !levelEnded) {
            paused = !paused;
            if (!paused) {
                for (Drawable drawable : drawables) {
                    if (drawable instanceof Ball) {
                        Ball ball = (Ball) drawable;
                        ball.restoreVelocity();
                    }
                }
            }
        } else if ((key == 'r' || key == 'R') && (!paused || levelEnded)) {
            playingWinAnimation = false;
            playerLines.clear();
            currentLine = null;
            if (gameOver) {

                currentLevelIndex = 0;
                gameOver = false;
                loadLevel(currentLevelIndex);
            } else {

                loadLevel(currentLevelIndex);
            }
        }

    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased(){

    }

    public void mousePressed(MouseEvent e) {
        if (levelEnded) return;
        if (e.getButton() == LEFT) {
            currentLine = new PlayerLine();
            currentLine.addPoint(e.getX(), e.getY() - TOP_MARGIN);
        } else if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
            removeLine(e.getX(), e.getY() - TOP_MARGIN);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (levelEnded) return;
        if (e.getButton() == LEFT) {
            if (currentLine != null) {
                currentLine.addPoint(e.getX(), e.getY() - TOP_MARGIN);
            }
        } else if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
            removeLine(e.getX(), e.getY() - TOP_MARGIN);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (levelEnded) return;
        if (e.getButton() == LEFT && currentLine != null) {
            playerLines.add(currentLine);
            currentLine = null;
        }
    }

    private void removeLine(float x, float y) {
        Iterator<PlayerLine> iterator = playerLines.iterator();
        while (iterator.hasNext()) {
            PlayerLine line = iterator.next();
            if (line.containsPoint(x, y)) {
                iterator.remove();
                break;
            }
        }
    }

    private void drawUI() {


        textAlign(RIGHT, TOP);
        textSize(24);
        fill(0);

        text("Score: " + score, width - 10, 10);

        text("Time: " +  (int)Math.floor(levelTimer / (double)FPS), width - 10, 40);

        if (gameOver) {
            displayMessage("=== ENDED ===");

        } else if (paused) {
            displayMessage("*** PAUSED ***");
        }


    }

    private void drawBallQueue() {
        int maxDisplay = Math.min(5, ballQueue.size());
        for (int i = 0; i < maxDisplay; i++) {
            String ballColor = ballQueue.get(i);
            PImage ballImage = getBallImageByColor(ballColor);

            // 绘制球图像
            image(ballImage, 10 + i * (App.CELLSIZE + 5), 10, App.CELLSIZE, App.CELLSIZE);
        }
        if (!ballQueue.isEmpty()) {
            fill(0);
            textSize(16);
            textAlign(LEFT, TOP);
            text(String.format("%.1f", spawnTimer / FPS), 10, 10 + App.CELLSIZE + 5);
        }

        // 绘制生成计时器

    }

    private PImage getBallImageByColor(String color) {
        switch (color.toLowerCase()) {
            case "blue":
                return ball2;
            case "orange":
                return ball1;
            case "green":
                return ball3;
            case "yellow":
                return ball4;
            case "grey":
                return ball0;
            default:
                return ball0;
        }
    }

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        background(255);
        drawUI();
        drawBallQueue();

        pushMatrix();
        translate(0, TOP_MARGIN);
        drawGrid();



        List<Ball> balls = new ArrayList<>();
        List<Collidable> collidables = new ArrayList<>();
        List<Drawable> nonBallDrawables = new ArrayList<>();
        List<Ball> ballsToRemove = new ArrayList<>();

        // Separate balls, collidables, and other drawables
        for (Drawable drawable : drawables) {
            if (drawable instanceof Ball) {
                Ball ball = (Ball) drawable;
                ball.update();
                balls.add(ball);
            } else {
                nonBallDrawables.add(drawable);
                if (drawable instanceof Collidable) {
                    collidables.add((Collidable) drawable);
                }
            }
        }

        // Add player lines to collidables
        collidables.addAll(playerLines);

        // Check collisions
        for (Ball ball : balls) {
            for (Collidable collidable : collidables) {
                if (collidable.checkCollision(ball)) {
                    break; // Ball has collided, move to next ball
                }
            }
        }

        // Apply hole attraction
        for (Drawable drawable : nonBallDrawables) {
            if (drawable instanceof Hole) {
                Hole hole = (Hole) drawable;
                for (Ball ball : balls) {
                    hole.attractBall(ball, this);
                    if (ball.isCaptured()) {
                        ballsToRemove.add(ball);
                    }
                }
            }
        }

        // Remove captured balls
        for (Ball ball : ballsToRemove) {
            drawables.remove(ball);
            balls.remove(ball);
        }



        // Drawing phase
        // 1. Draw non-ball drawables (including spawners)
        for (Drawable drawable : nonBallDrawables) {
            drawable.draw(this);
        }

        // 2. Draw balls on top
        for (Ball ball : balls) {
            ball.draw(this);
        }


        // 3. Draw player lines
        for (PlayerLine line : playerLines) {
            line.draw(this);
        }
        if (currentLine != null) {
            currentLine.draw(this);
        }



        if (!levelEnded && !paused && !gameOver) {
            levelTimer--;
            noBallsOnBoard = balls.isEmpty();

            if (!ballQueue.isEmpty()) {
                if (spawnTimer <= 0) {
                    spawnNextBall(); // 生成球
                    spawnTimer = currentLevel.spawnInterval * FPS;// 重置生成计时器
                    noBallsOnBoard = false;
                } else {
                    spawnTimer--;
                }
            }


            if (ballQueue.isEmpty() && noBallsOnBoard) {
                levelEnded = true;
                levelWon = true;
                timeBonus = levelTimer;
                playerLines.clear();
                spawners.clear();
                //System.out.println("Victory condition met. Level won!");
            }

            if (levelTimer <= 0 && !levelWon) {
                levelEnded = true;
                levelWon = false;
                displayMessage("=== TIME'S UP ===");
            }
        } else {
            if (levelWon) {
                if (playingWinAnimation != true) {
                    addTimeBonusToScore();
                    playingWinAnimation = true;
                    initializeWinAnimation();
                }

            } else if (levelEnded) {
                for (Drawable drawable : drawables) {
                    if (drawable instanceof Ball) {
                        Ball ball = (Ball) drawable;
                        ball.setVx(0);
                        ball.setVy(0);
                    }
                }
                playerLines.clear();
                currentLine = null;
                displayMessage("=== TIME'S UP ===");
            } else if (gameOver) {
                for (Drawable drawable : drawables) {
                    if (drawable instanceof Ball) {
                        Ball ball = (Ball) drawable;
                        ball.setVx(0);
                        ball.setVy(0);
                    }
                }

                playerLines.clear();
                currentLine = null;
                //displayMessage("=== TIME'S UP ===");
            } else if (paused) {
                for (Drawable drawable : drawables) {
                    if (drawable instanceof Ball) {
                        Ball ball = (Ball) drawable;
                        ball.saveVelocity();
                        ball.setVx(0);
                        ball.setVy(0);
                    }
                }
            }
        }





        if (playingWinAnimation) {

            animationTimer--;
            if (animationTimer <= 0) {
                animationTimer = animationInterval;
                performWinAnimationStep();
            }
            if (levelTimer > 0) {
                levelTimer -= FPS/2;
                score += 1;
            }

            if (levelTimer <= 0) {
                playingWinAnimation = false;
                levelEnded = false;
                levelWon = false;
                currentLevelIndex++;
                loadLevel(currentLevelIndex);
            }
        }

        popMatrix();

    }

    private List<Ball> getBallsFromDrawables() {
        List<Ball> balls = new ArrayList<>();
        for (Drawable drawable : drawables) {
            if (drawable instanceof Ball) {
                balls.add((Ball) drawable);
            }
        }
        return balls;
    }

    private List<Drawable> getNonBallDrawables() {
        List<Drawable> nonBallDrawables = new ArrayList<>();
        for (Drawable drawable : drawables) {
            if (!(drawable instanceof Ball)) {
                nonBallDrawables.add(drawable);
            }
        }
        return nonBallDrawables;
    }

    private void drawStaticGameElements() {
        List<Ball> balls = getBallsFromDrawables();
        List<Drawable> nonBallDrawables = getNonBallDrawables();

        // 1. 绘制非球的元素（包括生成器）
        for (Drawable drawable : nonBallDrawables) {
            drawable.draw(this);
        }

        // 2. 绘制球
        for (Ball ball : balls) {
            ball.draw(this);
        }

        // 3. 绘制玩家的线
        for (PlayerLine line : playerLines) {
            line.draw(this);
        }
        if (currentLine != null) {
            currentLine.draw(this);
        }
    }

    private void initializeWinAnimation() {
        leftX = 0;
        leftY = 0;
        rightX = BOARD_WIDTH - 1;
        rightY = BOARD_HEIGHT - 1;
        lastLeftX = -1;
        lastLeftY = -1;
        lastRightX = -1;
        lastRightY = -1;
        animationTimer = animationInterval;
    }

    private void performWinAnimationStep() {

        grid[leftX][leftY] = new Tile(leftX, leftY, null, wall4, wall4);
        grid[rightX][rightY] = new Tile(rightX, rightY, null, wall4, wall4);
        grid[leftX][leftY].draw(this);
        grid[rightX][rightY].draw(this);


        // 记录当前的位置，供下一次恢复使用
        lastLeftX = leftX;
        lastLeftY = leftY;
        lastRightX = rightX;
        lastRightY = rightY;

        updatePosition(leftX, leftY, true);
        updatePosition(rightX, rightY, false);
    }

    private void updatePosition(int x, int y, boolean isleft) {
        if (y == 0 && x < BOARD_WIDTH - 1) {
            x++;
        } else if (x == BOARD_WIDTH - 1 && y < BOARD_HEIGHT - 1) {
            y++;
        } else if (y == BOARD_HEIGHT - 1 && x > 0) {
            x--;
        } else if (x == 0 && y > 0) {
            y--;
        }


        if (isleft) {
            leftX = x;
            leftY = y;
        } else {
            rightX = x;
            rightY = y;
        }
    }



    private void addTimeBonusToScore() {
        int bonusScore = (int) (timeBonus / FPS);
        score += bonusScore;
        timeBonus = 0;
    }

    private void spawnNextBall() {
        if (!ballQueue.isEmpty()) {
            String nextBallColor = ballQueue.remove(0);
            PImage ballImage = getBallImageByColor(nextBallColor);
            int ballType = getBallTypeByColor(nextBallColor);

            // 从随机的Spawner生成球
            if (!spawners.isEmpty()) {
                Spawner spawner = spawners.get(random.nextInt(spawners.size()));
                Ball newBall = new Ball(spawner.getX(), spawner.getY(), ballImage, ballType);
                drawables.add(newBall);
                //System.out.println("Spawned Ball of type " + ballType + " at (" + newBall.getX() + ", " + newBall.getY() + ")");
            } else {
                //System.err.println("No spawners available to spawn ball.");
            }
        }
    }

    private int getBallTypeByColor(String color) {
        switch (color.toLowerCase()) {
            case "blue":
                return 2;
            case "orange":
                return 1;
            case "green":
                return 3;
            case "yellow":
                return 4;
            case "grey":
            default:
                return 0;
        }
    }

    public static PImage getBallImageByType(int type) {
        switch (type) {
            case 1:
                return ball1;
            case 2:
                return ball2;
            case 3:
                return ball3;
            case 4:
                return ball4;
            case 0:
            default:
                return ball0;
        }
    }

    private String getColorByType(int type) {
        switch (type) {
            case 1:
                return "orange";
            case 2:
                return "blue";
            case 3:
                return "green";
            case 4:
                return "yellow";
            case 0:
            default:
                return "grey";
        }
    }


    public static Tile[][] getGrid() {
        return grid;
    }

    private void displayMessage(String message) {
        fill(0);
        textSize(30);

        text(message, WIDTH - textWidth(message) / 2 - 20, 30);
    }

    private void drawBallTypes() {
        int ballSize = 30;
        int spacing = 10;
        int startX = 10;
        int startY = 10;

        // 假设您有一个包含所有球类型图像的数组
        PImage[] ballImages = {ball0, ball1, ball2, ball3, ball4};

        for (int i = 0; i < ballImages.length; i++) {
            image(ballImages[i], startX + i * (ballSize + spacing), startY, ballSize, ballSize);
        }
    }


    private void drawGrid() {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Tile tile = grid[x][y];
                if (tile != null && tile.isEmpty() && !tile.isCovered()) {
                    tile.draw(this);
                }
            }
        }

    }

    public void increaseScore() {
        score += 10; // Adjust the score increment as needed
    }

    public void decreaseScore() {
        score -= 5; // Adjust the score decrement as needed
    }

    public void respawnBall(Ball ball) {
        ballsToRespawn.add(ball);
        String color = getColorByType(ball.getType());
        ballQueue.add(color);

        if (ballQueue.size() == 1 && spawnTimer > 0) {
            spawnTimer = 0; // 立即触发生成
        }
    }

    private void resetBallPosition(Ball ball) {
        if (!spawners.isEmpty()) {
            Spawner spawner = spawners.get(random.nextInt(spawners.size()));
            ball.setX(spawner.getX());
            ball.setY(spawner.getY());
            // Reset velocity
            float baseSpeed = 2.0f;
            if (FPS == 60) {
                baseSpeed /= 2;
            }
            ball.setVx(random.nextBoolean() ? baseSpeed : -baseSpeed);
            ball.setVy(random.nextBoolean() ? baseSpeed : -baseSpeed);
        }
    }

    public List<Drawable> getDrawables() {
        return drawables;
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
