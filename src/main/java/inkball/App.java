package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.core.PVector;
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


/**
 * Main application class for the Inkball game.
 * This class extends PApplet to utilize Processing's graphics capabilities.
 */
public class App extends PApplet{

    // Constants for game settings
    public static final int CELLSIZE = 32;
    public static final int CELLHEIGHT = 32;
    public static final int CELLAVG = 32;
    public static final int TOPBAR = 0;
    public static int WIDTH = 576;
    public static int HEIGHT = 648;
    public static final int BOARD_WIDTH = WIDTH / CELLSIZE;
    public static final int BOARD_HEIGHT = 18;
    public static final int TOP_MARGIN = 70;
    public static final int INITIAL_PARACHUTES = 1;
    public static final float FPS = 30.0f;

    // Game configuration and state
    JSONObject config;
    JSONArray levels;
    int currentLevelIndex = 0;
    private Level currentLevel;
    float timeBonus = 0;

    public String configPath;
    private static Tile[][] grid;
    private List<Drawable> drawables;

    // Image resources
    private PImage wall0, wall1, wall2, wall3, wall4;
    private PImage speedTile1, speedTile2, speedTile3, speedTile4;
    private static PImage ball0, ball1, ball2, ball3, ball4;
    private PImage spawner, hole;
    private PImage tileBaseImage;

    // Game state flags
    float levelTimer = 0;
    boolean levelEnded = false;
    boolean levelWon = false;
    boolean playingWinAnimation = false;
    boolean noBallsOnBoard = false;
    boolean gameOver = false;
    boolean paused = false;

    // Animation variables
    private float animationTimer = 0;
    private float animationInterval = 0.067f * FPS; // Every 0.067 seconds
    private int animationStep = 0;

    // Animation coordinates
    int leftX, leftY, rightX, rightY;
    private int lastLeftX = -1, lastLeftY = -1;
    private int lastRightX = -1, lastRightY = -1;

    public static Random random = new Random();

    // Game elements
    List<PlayerLine> playerLines;
    PlayerLine currentLine;
    Map<String, Integer> scoreIncreaseMap;
    Map<String, Integer> scoreDecreaseMap;

    // Ball sliding animation
    private boolean isSliding = false;
    private int slideProgress = 0;
    private String slidingBallColor = null;

    // Game score and ball management
    int score = 0;
    private int lastLevelScore = 0;
    List<Ball> ballsToRespawn = new ArrayList<>();
    List<Spawner> spawners = new ArrayList<>();
    private List<SpeedTile> speedTiles = new ArrayList<>();
    List<String> ballQueue = new ArrayList<>(); // Ungenerated Team Columns
    float spawnTimer;

    /**
     * Constructor for the App class.
     * Initializes the configuration path.
     */
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

            config = loadJSONObject("config.json");

            levels = config.getJSONArray("levels");
            playerLines = new ArrayList<>();

            scoreIncreaseMap = new HashMap<>();
            JSONObject scoreIncreaseJson = config.getJSONObject("score_increase_from_hole_capture");


            Set<String> increaseKeys = new HashSet<>();
            for (Object key : scoreIncreaseJson.keys()) {
                if (key instanceof String) {
                    increaseKeys.add((String) key);
                }
            }

            for (String color : increaseKeys) {
                int scoreValue = scoreIncreaseJson.getInt(color);
                scoreIncreaseMap.put(color.toLowerCase(), scoreValue);
            }

            scoreDecreaseMap = new HashMap<>();
            JSONObject scoreDecreaseJson = config.getJSONObject("score_decrease_from_wrong_hole");

            Set<String> decreaseKeys = new HashSet<>();
            for (Object key : scoreDecreaseJson.keys()) {
                if (key instanceof String) {
                    decreaseKeys.add((String) key);
                }
            }

            for (String color : decreaseKeys) {
                int scoreValue = scoreDecreaseJson.getInt(color);
                scoreDecreaseMap.put(color.toLowerCase(), scoreValue);
            }


//            scoreIncreaseMap = new HashMap<>();
//            JSONObject scoreIncreaseJson = config.getJSONObject("score_increase_from_hole_capture");
//            Set<String> increaseKeys = scoreIncreaseJson.keys();
//            for (String color : increaseKeys) {
//                int scoreValue = scoreIncreaseJson.getInt(color);
//                scoreIncreaseMap.put(color.toLowerCase(), scoreValue);
//            }
//
//            scoreDecreaseMap = new HashMap<>();
//            JSONObject scoreDecreaseJson = config.getJSONObject("score_decrease_from_wrong_hole");
//            Set<String> decreaseKeys = scoreDecreaseJson.keys();
//            for (String color : decreaseKeys) {
//                int scoreValue = scoreDecreaseJson.getInt(color);
//                scoreDecreaseMap.put(color.toLowerCase(), scoreValue);
//            }

            // Load the first level
            loadLevel(currentLevelIndex);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads an image from resources.
     * @param filename The name of the image file without extension
     * @return The loaded PImage
     * @throws UnsupportedEncodingException If there's an encoding error
     */
    PImage loadImageFromResources(String filename) throws UnsupportedEncodingException {
        String imagePath = URLDecoder.decode(
                this.getClass().getResource("/inkball/" + filename + ".png").getPath(),
                StandardCharsets.UTF_8.name()
        );
        return loadImage(imagePath);
    }

    /**
     * Loads all game images.
     */
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
            speedTile1 = loadImageFromResources("speedTile1");
            speedTile2 = loadImageFromResources("speedTile2");
            speedTile3= loadImageFromResources("speedTile3");
            speedTile4 = loadImageFromResources("speedTile4");
            spawner = loadImage("src/main/resources/inkball/entrypoint.png");
            hole = loadImage("src/main/resources/inkball/hole0.png");
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error loading images: " + e.getMessage());
        }
    }

    /**
     * Loads a level by index.
     * @param levelIndex The index of the level to load
     */
    public void loadLevel(int levelIndex) {
        if (levelIndex >= levels.size()) {
            gameOver = true;
            displayMessage("=== ENDED ===");
            return;
        }

        JSONObject levelData = levels.getJSONObject(levelIndex);
        currentLevel = new Level(levelData);

        // Reset level state
        levelTimer = currentLevel.time * FPS; // Convert seconds to frames
        spawnTimer = currentLevel.spawnInterval * FPS; // Convert seconds to frames

        // Load level layout
        loadLevelLayout(currentLevel.layout);

        // Initialize ungenerated ball queue
        ballQueue = new ArrayList<>(currentLevel.balls);

        // Other initializations
        ballsToRespawn.clear();

        levelEnded = false;
        levelWon = false;

        spawnNextBall();
    }

    /**
     * Loads the level layout from a file.
     * @param fileName The name of the layout file
     */
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
                        PImage overlayImg = getTileOverlayImage(compositeType);

                        Drawable drawable;
                        if (tileType == 'H') {
                            drawable = new Hole(x, y, overlayImg, typeNumber - '0');

                            grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                            grid[x + 1][y] = new Tile(x + 1, y, null, tileBaseImage, null);
                            grid[x][y + 1] = new Tile(x, y + 1, null, tileBaseImage, null);
                            grid[x + 1][y + 1] = new Tile(x + 1, y + 1, null, tileBaseImage, null);
                            grid[x + 1][y].setCovered(true);
                            grid[x][y + 1].setCovered(true);
                            grid[x + 1][y + 1].setCovered(true);
                        } else {
                            drawable = new Ball(x, y, overlayImg, typeNumber - '0');
                            grid[x + 1][y] = new Tile(x + 1, y, null, tileBaseImage, null);
                            grid[x][y] = new Tile(x, y, null, tileBaseImage, null);
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

                } else if (tileType == '^' || tileType == 'v' || tileType == '<' || tileType == '>') {
                    SpeedTile speedTile = new SpeedTile(x, y, overlayImg, tileType);
                    grid[x][y] = new Tile(x, y, speedTile, tileBaseImage, overlayImg);

                    drawables.add(speedTile);
                    speedTiles.add(speedTile);

                } else {
                    Drawable drawable = getDrawableForTileType(Character.toString(tileType), x, y, overlayImg, '0');
                    grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                    if (drawable != null) {
                        drawables.add(drawable);
                    }
                }


            }
        }
    }

    /**
     * Handles key press events.
     * @param event The KeyEvent object
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

            if (gameOver) {
                currentLevelIndex = 0;
                gameOver = false;
                score = 0;
                loadLevel(currentLevelIndex);
            } else {
                score = lastLevelScore;
                loadLevel(currentLevelIndex);
            }
            playingWinAnimation = false;
            playerLines.clear();
            currentLine = null;
        }

    }

    /**
     * Handles key release events.
     */
    @Override
    public void keyReleased(){}

    /**
     * Handles mouse press events.
     * @param e The MouseEvent object
     */
    public void mousePressed(MouseEvent e) {
        if (levelEnded) return;
        if (e.getButton() == LEFT) {
            if (e.isControlDown()) {
                removeLine(e.getX(), e.getY() - TOP_MARGIN);
            } else {
                currentLine = new PlayerLine();
                currentLine.addPoint(e.getX(), e.getY() - TOP_MARGIN);
            }
        } else if (e.getButton() == RIGHT) {
            removeLine(e.getX(), e.getY() - TOP_MARGIN);
        }
    }

    /**
     * Handles mouse drag events.
     * @param e The MouseEvent object
     */
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

    /**
     * Handles mouse release events.
     * @param e The MouseEvent object
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        if (levelEnded) return;
        if (e.getButton() == LEFT && currentLine != null) {
            playerLines.add(currentLine);
            currentLine = null;
        }
    }

    /**
     * Removes a player line at the specified coordinates.
     * @param x The x-coordinate
     * @param y The y-coordinate
     */
    void removeLine(float x, float y) {
        Iterator<PlayerLine> iterator = playerLines.iterator();
        while (iterator.hasNext()) {
            PlayerLine line = iterator.next();
            if (line.containsPoint(x, y)) {
                iterator.remove();
                break;
            }
        }
    }

    /**
     * Draws the user interface elements.
     */
    private void drawUI() {
        textAlign(LEFT, TOP);
        textSize(24);
        fill(0);

        text("Score: " + score, 445, 5);

        text("Time: " +  (int)Math.floor(levelTimer / (double)FPS), 450, 35);

        if (gameOver) {
            displayMessage("=== ENDED ===");

        } else if (paused) {
            displayMessage("*** PAUSED ***");
        }
    }

    /**
     * Draws the ball queue in the UI.
     */
    private void drawBallQueue() {
        // Draw black background rectangle
        int ballSize = 24;
        int ballSpacing = 10;

        int ballsToDisplay = 5;
        int rectWidth = 180;
        int rectHeight = 44;
        int rectX = 10;
        int rectY = 13;

        // Draw black background frame
        fill(0);
        noStroke();
        rect(rectX, rectY, rectWidth, rectHeight);

        // Save current drawing state
        pushStyle();
        pushMatrix();

        // Set clipping area
        clip(rectX, rectY, rectWidth, rectHeight);

        // Move coordinate system to top-left corner of background frame
        translate(rectX, rectY);

        // Update sliding progress
        if (isSliding) {
            if (slideProgress < ballSize + ballSpacing) {
                slideProgress += 2; // Adjust sliding speed
            } else {
                slideProgress = ballSize + ballSpacing;
                isSliding = false;
                slidingBallColor = null;
            }
        }

        // Draw sliding ball
        if (slidingBallColor != null) {
            PImage ballImage = getBallImageByColor(slidingBallColor);
            float xPosition = ballSpacing - slideProgress;
            float yPosition = ballSpacing;
            image(ballImage, xPosition, yPosition, ballSize, ballSize);
        }

        // Draw remaining balls
        List<String> ballsToDraw = ballQueue;
        int maxDisplay = Math.min(ballsToDisplay, ballsToDraw.size());
        if (isSliding) {
            for (int i = 0; i < maxDisplay; i++) {
                String ballColor = ballsToDraw.get(i);
                PImage ballImage = getBallImageByColor(ballColor);
                float xPosition = ballSpacing + (i + 1) * (ballSize + ballSpacing) - slideProgress;
                float yPosition = ballSpacing;
                image(ballImage, xPosition, yPosition, ballSize, ballSize);
            }
        } else {
            for (int i = 0; i < maxDisplay; i++) {
                String ballColor = ballsToDraw.get(i);
                PImage ballImage = getBallImageByColor(ballColor);
                float xPosition = ballSpacing + i * (ballSize + ballSpacing);
                float yPosition = ballSpacing;
                image(ballImage, xPosition, yPosition, ballSize, ballSize);
            }
        }

        // Restore drawing state
        popMatrix();
        popStyle();
        noClip();

        if (!ballQueue.isEmpty()) {
            fill(0); // Use black text, as it's now outside the black area
            textSize(24);
            textAlign(LEFT, CENTER);
            float textX = rectX + rectWidth + 10; // Place text 10 pixels to the right of the rectangle
            float textY = rectY + rectHeight / 2; // Vertically center-align the text
            text(String.format("%.1f", spawnTimer / FPS), textX, textY);
        }
    }


    /**
     * Main draw method, called every frame.
     */
    @Override
    public void draw() {
        background(200);
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

        // Check collisions and speed boost
        for (Ball ball : balls) {
            for (Collidable collidable : collidables) {
                if (collidable.checkCollision(ball)) {
                    break; // Ball has collided, move to next ball
                }
            }
            for (SpeedTile speedTile : speedTiles) {
                //System.out.println("ball:" + ball.getX() + ", " + ball.getY());
                //System.out.println("speedtile" + (speedTile.getX() * App.CELLSIZE + App.CELLSIZE / 2) + ", " + (speedTile.getY() * App.CELLSIZE + App.CELLSIZE / 2));
                float distanceX = Math.abs(ball.getX() - (speedTile.getX() * App.CELLSIZE + App.CELLSIZE / 2));
                float distanceY = Math.abs(ball.getY() - (speedTile.getY() * App.CELLSIZE + App.CELLSIZE / 2));
                float dist = PApplet.sqrt(distanceX * distanceX + distanceY * distanceY);

                if (dist <= 25) {
                    speedTile.applySpeedBoost(ball);

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
                    spawnNextBall();
                    spawnTimer = currentLevel.spawnInterval * FPS;
                    noBallsOnBoard = false;
                } else {
                    spawnTimer--;
                }
            }

            checkWinCondition();

            if (levelTimer <= 0 && !levelWon) {
                levelEnded = true;
                displayMessage("=== TIME'S UP ===");
            }
        } else {
            if (levelWon) {
                if (!playingWinAnimation) {
                    lastLevelScore = score + 1;
                    addTimeBonusToScore();
                    playingWinAnimation = true;
                    initializeWinAnimation();

                }
            } else if (levelEnded) {
                lastLevelScore = score + 1;
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
                levelTimer = 0;
                playingWinAnimation = false;
                levelEnded = false;
                levelWon = false;
                currentLevelIndex++;
                loadLevel(currentLevelIndex);
            }
        }

        popMatrix();

    }

    /**
     * Checks if the win condition for the current level is met.
     */
    public void checkWinCondition() {
        if (ballQueue.isEmpty() && noBallsOnBoard) {
            levelEnded = true;
            levelWon = true;
            timeBonus = levelTimer;
            playerLines.clear();
            spawners.clear();
            //System.out.println("Victory condition met. Level won!");
        }
    }


    /**
     * Initializes the win animation.
     */
    void initializeWinAnimation() {
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

    /**
     * Performs a step in the win animation.
     */
    void performWinAnimationStep() {

        grid[leftX][leftY] = new Tile(leftX, leftY, null, wall4, wall4);
        grid[rightX][rightY] = new Tile(rightX, rightY, null, wall4, wall4);
        grid[leftX][leftY].draw(this);
        grid[rightX][rightY].draw(this);


        updatePosition(leftX, leftY, true);
        updatePosition(rightX, rightY, false);
    }

    /**
     * Updates the position for the win animation.
     * @param x Current x-coordinate
     * @param y Current y-coordinate
     * @param isLeft True if updating left position, false for right
     */
    void updatePosition(int x, int y, boolean isLeft) {
        if (y == 0 && x < BOARD_WIDTH - 1) {
            x++;
        } else if (x == BOARD_WIDTH - 1 && y < BOARD_HEIGHT - 1) {
            y++;
        } else if (y == BOARD_HEIGHT - 1 && x > 0) {
            x--;
        } else if (x == 0 && y > 0) {
            y--;
        }

        if (isLeft) {
            leftX = x;
            leftY = y;
        } else {
            rightX = x;
            rightY = y;
        }
    }

    /**
     * Adds the time bonus to the score.
     */
    void addTimeBonusToScore() {
        int bonusScore = (int) (timeBonus / FPS);
        score += bonusScore;
        timeBonus = 0;
    }

    /**
     * Spawns the next ball in the queue.
     */
    void spawnNextBall() {
        if (!ballQueue.isEmpty()) {
            String nextBallColor = ballQueue.remove(0);
            PImage ballImage = getBallImageByColor(nextBallColor);
            int ballType = getTypeByColor(nextBallColor);

            isSliding = true;
            slideProgress = 0;
            slidingBallColor = nextBallColor;

            // Spawn ball from a random Spawner
            if (!spawners.isEmpty()) {
                Spawner spawner = spawners.get(random.nextInt(spawners.size()));
                Ball newBall = new Ball(spawner.getX(), spawner.getY(), ballImage, ballType);
                drawables.add(newBall);
            }
        }
    }

    /**
     * Displays a message on the screen.
     * @param message The message to display
     */
    private void displayMessage(String message) {
        textAlign(RIGHT, TOP);
        fill(0);
        textSize(24);

        text(message, WIDTH - textWidth(message) / 2 - 60, 25);
    }

    /**
     * Draws the game grid.
     */
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

    /**
     * Increases the score based on the ball type.
     * @param type The type of the ball
     */
    public void increaseScore(int type) {
        String color = getColorByType(type);
        int baseScore = scoreIncreaseMap.getOrDefault(color, 0);
        float modifier = currentLevel.scoreIncreaseModifier;
        score += baseScore * modifier;
    }

    /**
     * Decreases the score based on the ball type.
     * @param type The type of the ball
     */
    public void decreaseScore(int type) {
        String color = getColorByType(type);
        int baseScore = scoreDecreaseMap.getOrDefault(color, 0);
        float modifier = currentLevel.scoreDecreaseModifier;
        score -= baseScore * modifier;
    }

    /**
     * Respawns a ball.
     * @param ball The ball to respawn
     */
    public void respawnBall(Ball ball) {
        ballsToRespawn.add(ball);
        String color = getColorByType(ball.getType());
        ballQueue.add(color);

        if (ballQueue.size() == 1 && spawnTimer > 0) {
            spawnTimer = 0; // Trigger immediate spawn
        }
    }

    /**
     * Gets the list of drawable objects.
     * @return List of Drawable objects
     */
    public List<Drawable> getDrawables() {
        return drawables;
    }

    /**
     * Gets the appropriate image for a tile type.
     * @param tileType The type of the tile
     * @return The corresponding PImage
     */
    PImage getTileOverlayImage(String tileType) {
        switch (tileType) {
            case "X": return wall0;
            case "1": return wall1;
            case "2": return wall2;
            case "3": return wall3;
            case "4": return wall4;
            case "^": return speedTile1;
            case "v": return speedTile2;
            case "<": return speedTile3;
            case ">": return speedTile4;
            case "S": return spawner;
            case "H0": return hole; // Extend this for different hole images
            case "H1": case "H2": case "H3": case "H4": return loadImage("src/main/resources/inkball/hole" + tileType.charAt(1) + ".png");
            case "B0": return ball0;
            case "B1": case "B2": case "B3": case "B4": return loadImage("src/main/resources/inkball/ball" + tileType.charAt(1) + ".png");
            default: return null;
        }
    }

    /**
     * Creates a Drawable object based on the tile type.
     * @param tileType The type of the tile
     * @param x The x-coordinate of the tile
     * @param y The y-coordinate of the tile
     * @param overlayImage The image to overlay on the tile
     * @param typeNumber The number associated with the tile type
     * @return The corresponding Drawable object
     */
    Drawable getDrawableForTileType(String tileType, int x, int y, PImage overlayImage, char typeNumber) {
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

    /**
     * Gets the ball image based on the specified color.
     * @param color The color of the ball
     * @return The corresponding ball image
     */
    public PImage getBallImageByColor(String color) {
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
            default:
                return ball0;
        }
    }

    /**
     * Gets the type number based on the specified color.
     * @param color The color of the ball
     * @return The corresponding type number
     */
    public int getTypeByColor(String color) {
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

    /**
     * Gets the color name based on the specified type number.
     * @param type The type number of the ball
     * @return The corresponding color name
     */
    public String getColorByType(int type) {
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

    /**
     * Gets the ball image based on the specified type number.
     * @param type The type number of the ball
     * @return The corresponding ball image
     */
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

    /**
     * Gets the current game grid.
     * @return The 2D array representing the game grid
     */
    public static Tile[][] getGrid() {
        return grid;
    }

    /**
     * The main method to start the application.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }
}
