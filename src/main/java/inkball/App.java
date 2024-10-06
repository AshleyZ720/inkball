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
    public static int HEIGHT = 640; //BOARD_HEIGHT*CELLSIZE+TOPBAR;
    public static final int BOARD_WIDTH = WIDTH/CELLSIZE;
    public static final int BOARD_HEIGHT = 20;

    public static final int INITIAL_PARACHUTES = 1;

    public static final int FPS = 30;

    public String configPath;
    private static Tile[][] grid;
    private List<Drawable> drawables;
    private PImage wall0, wall1, wall2, wall3, wall4;
    private PImage ball0, ball1, ball2, ball3, ball4;
    private PImage spawner, hole;
    private int levelTimer = 300; // Example timer in frames (10 seconds at 30 FPS)
    private boolean levelEnded = false;
    private boolean levelWon = false;
    private PImage tileBaseImage;

    public static Random random = new Random();

    private List<PlayerLine> playerLines;
    private PlayerLine currentLine;

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
        frameRate(FPS);
        //See PApplet javadoc:
        //loadJSONObject(configPath)
        // the image is loaded from relative path: "src/main/resources/inkball/..."
		/*try {
            result = loadImage(URLDecoder.decode(this.getClass().getResource(filename+".png").getPath(), StandardCharsets.UTF_8.name()));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }*/
        tileBaseImage = loadImage("src/main/resources/inkball/tile.png");
        loadImages();
        loadLevel("level2.txt");
        playerLines = new ArrayList<>();
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

    public void loadLevel(String fileName) {
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
                            grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                            grid[x + 1][y] = new Tile(x + 1, y, new Hole(x + 1, y, overlayImg, typeNumber - '0'), null, null); // 右边的格子
                            grid[x][y + 1] = new Tile(x, y + 1, new Hole(x, y + 1, overlayImg, typeNumber - '0'), null, null); // 下边的格子
                            grid[x + 1][y + 1] = new Tile(x + 1, y + 1, new Hole(x + 1, y + 1, overlayImg, typeNumber - '0'), null, null);
//                            grid[x + 1][y] = new Tile(x + 1, y, null, null, null); // 空的 Tile
//                            grid[x][y + 1] = new Tile(x, y + 1, null, null, null); // 空的 Tile
//                            grid[x + 1][y + 1] = new Tile(x + 1, y + 1, null, null, null);
                            grid[x + 1][y].setCovered(true);
                            grid[x][y + 1].setCovered(true);
                            grid[x + 1][y + 1].setCovered(true);
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
                } else {
                    Drawable drawable = getDrawableForTileType(Character.toString(tileType), x, y, overlayImg, '0');
                    grid[x][y] = new Tile(x, y, drawable, tileBaseImage, overlayImg);
                    if (drawable != null) {
                        drawables.add(drawable);
                    }
                }


            }
        }
        levelEnded = false;
        levelTimer = 300;
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
        // Handle 'r' key press to restart the level
        if (key == 'r' || key == 'R') {
            if (levelEnded) {
                levelEnded = false;
                levelWon = false;
                loadLevel("src/main/resources/inkball/level1.txt"); // Reload the current level
            }
        }
    }

    /**
     * Receive key released signal from the keyboard.
     */
    @Override
    public void keyReleased(){

    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == LEFT) {
            currentLine = new PlayerLine();
            currentLine.addPoint(e.getX(), e.getY());
        } else if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
            removeLine(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (e.getButton() == LEFT) {
            if (currentLine != null) {
                currentLine.addPoint(e.getX(), e.getY());
            }
        } else if (e.getButton() == RIGHT || (e.getButton() == LEFT && e.isControlDown())) {
            removeLine(e.getX(), e.getY());
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
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

    /**
     * Draw all elements in the game by current frame.
     */
    @Override
    public void draw() {
        background(255);
        drawGrid();

        List<Ball> balls = new ArrayList<>();
        List<Collidable> collidables = new ArrayList<>();
        List<Drawable> nonBallDrawables = new ArrayList<>();

        // First pass: Separate balls, collidables, and other drawables
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




        // 处理级别结束和计时
        if (!levelEnded) {
            levelTimer--;
            if (levelTimer <= 0) {
                levelEnded = true;
                levelWon = false;
                displayMessage("TIME'S UP");
            }
        } else if (levelWon) {
            displayMessage("LEVEL COMPLETE");
        }

        // 显示计时器
        fill(0);
        textSize(24);
        text("Timer: " + levelTimer / FPS, 10, HEIGHT - 30);
    }


    public static Tile[][] getGrid() {
        return grid;
    }

    private void displayMessage(String message) {
        fill(0);
        textSize(32);
        text(message, WIDTH / 2 - textWidth(message) / 2, 50);
    }

    private void drawGrid() {
        // Step 1: Draw every empty tile
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Tile tile = grid[x][y];
                if (tile != null && tile.isEmpty() && !tile.isCovered()) {
                    tile.draw(this); // Draw empty tile
                }
            }
        }

        // Step 2: Draw other tiles (except for balls)
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Tile tile = grid[x][y];
                if (tile != null && !tile.isEmpty() && !tile.isCovered() && !(tile.getDrawable() instanceof Ball)) {
                    tile.draw(this); // Draw drawable tile (not ball)
                }
            }
        }


    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
