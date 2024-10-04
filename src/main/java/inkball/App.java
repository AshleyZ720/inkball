package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import java.io.*;
import java.util.*;

public class App extends PApplet {

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
    private Tile[][] grid;
    private PImage wall0, wall1, wall2, wall3, wall4;
    private PImage ball0, ball1, ball2, ball3, ball4;
    private PImage spawner, hole;
    private int levelTimer = 300; // Example timer in frames (10 seconds at 30 FPS)
    private boolean levelEnded = false;
    private boolean levelWon = false;
    private PImage tileBaseImage;

    public static Random random = new Random();
	
	// Feel free to add any additional methods or attributes you want. Please put classes in different files.

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
        loadLevel("level1.txt");
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

        for (int y = 0; y < rows.length; y++) {
            String row = rows[y];
            for (int x = 0; x < row.length(); x++) {
                char tileType = row.charAt(x);
                // Check for composite types like H3, B4
                if ((tileType == 'H' || tileType == 'B') && x + 1 < row.length()) {
                    char typeNumber = row.charAt(x + 1);
                    if (Character.isDigit(typeNumber)) {
                        // Handle Hn, Bn cases
                        String compositeType = "" + tileType + typeNumber;
                        int type = getTileType(compositeType);
                        PImage overlayImg = getTileOverlayImage(compositeType); // Load the right image
                        grid[x][y] = new Tile(x, y, type, tileBaseImage, overlayImg);
                        // If it's a hole (H), cover 2x2 area
                        if (tileType == 'H') {
                            grid[x][y] = new Tile(x, y, type, tileBaseImage, overlayImg);
                            grid[x + 1][y] = new Tile(x + 1, y, type, null, null);
                            x += 1;// Skip the next character since it's part of the composite type
                        }

                        continue;
                    }
                } else if (Character.isDigit(tileType) && x - 1 > 0) {
                    char typeBefore = row.charAt(x - 1);
                    if (typeBefore == 'B') {
                        int type = getTileType(Character.toString(tileType));
                        grid[x][y] = new Tile(x, y, type, tileBaseImage, null);
                        continue;
                    }
                }
                if (x - 1 > 0 && y - 1 > 0) {
                    char diagonalBefore = rows[y - 1].charAt(x - 1);
                    if (diagonalBefore == 'H') {
                        int type = getTileType(Character.toString(tileType));
                        grid[x][y] = new Tile(x, y, type, null, null);
                        continue;
                    }
                }
                if (y - 1 > 0) {
                    char typeAbove = rows[y - 1].charAt(x);
                    if (typeAbove  == 'H') {
                        int type = getTileType(Character.toString(tileType));
                        grid[x][y] = new Tile(x, y, type, null, null);
                        continue;
                    }
                }
                PImage overlayImg = getTileOverlayImage(Character.toString(tileType)); // Load the right image
                int type = getTileType(Character.toString(tileType));
                grid[x][y] = new Tile(x, y, type, tileBaseImage, overlayImg);
            }
        }
        levelEnded = false;
        levelTimer = 300; // Reset timer
    }

    private int getTileType(String tileType) {
        switch (tileType) {
            case "X": return Tile.WALL;
            case "S": return Tile.SPAWNER;
            case "H0": case "H1": case "H2": case "H3": case "H4": return Tile.HOLE;
            case "B0": case "B1": case "B2": case "B3": case "B4": return Tile.BALL;
            default: return Tile.EMPTY;
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
        // create a new player-drawn line object
    }
	
	@Override
    public void mouseDragged(MouseEvent e) {
        // add line segments to player-drawn line object if left mouse button is held
		
		// remove player-drawn line object if right mouse button is held 
		// and mouse position collides with the line
    }

    @Override
    public void mouseReleased(MouseEvent e) {
		
    }

    /**
     * Draw all elements in the game by current frame.
     */
	@Override
    public void draw() {
        

        //----------------------------------
        //display Board for current level:
        //----------------------------------
        //TODO

        //----------------------------------
        //display score
        //----------------------------------
        //TODO
        
		//----------------------------------
        //----------------------------------
		//display game end message
        background(255);
        drawGrid();

        if (!levelEnded) {
            // Decrease timer
            levelTimer--;
            if (levelTimer <= 0) {
                levelEnded = true;
                levelWon = false;
                displayMessage("TIME'S UP");
            }
        } else if (levelWon) {
            // Move to the next level
            displayMessage("LEVEL COMPLETE");
            // Load the next level logic here
        }

        // Display the timer
        fill(0);
        textSize(24);
        text("Timer: " + levelTimer / FPS, 10, HEIGHT - 30);
    }

    private void displayMessage(String message) {
        fill(0);
        textSize(32);
        text(message, WIDTH / 2 - textWidth(message) / 2, 50);
    }

    private void drawGrid() {
        for (int x = 0; x < BOARD_WIDTH; x++) {
            for (int y = 0; y < BOARD_HEIGHT; y++) {
                Tile tile = grid[x][y];
                if (tile != null) {
                    tile.draw(this);
                }
            }
        }
    }

    public static void main(String[] args) {
        PApplet.main("inkball.App");
    }

}
