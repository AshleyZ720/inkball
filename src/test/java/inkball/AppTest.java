package inkball;

import org.junit.jupiter.api.BeforeEach;
import processing.core.PApplet;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import processing.core.PConstants;
import processing.data.JSONObject;
import processing.data.JSONArray;
import processing.core.PVector;
import processing.core.PApplet;
import processing.event.KeyEvent;
import processing.event.MouseEvent;

import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.ArrayList;


public class AppTest {
    private App app;
    // Define mouse event constants
    private static final int MOUSE_PRESSED = 400;
    private static final int MOUSE_RELEASED = 401;
    private static final int MOUSE_DRAGGED = 402;

    // Define mouse button constants
    private static final int BUTTON_LEFT = 37;
    private static final int BUTTON_RIGHT = 39;

    // Define keyboard event constants
    private static final int KEY_PRESS = 0;
    private static final int SPACE_KEY = 32;

    @BeforeEach
    public void setUp() {
        app = new App();
        app.noLoop();
        PApplet.runSketch(new String[]{"App"}, app);
        app.setup();
        app.loadLevel(0);
        app.delay(1000); // Give some time for setup

        App.random.setSeed(12345);
    }

    @Test
    // Tests whether a level is loaded correctly and essential elements are initialized.
    public void testLoadLevel() {
        app.loadLevel(0);
        assertNotNull(app.levelTimer);
        assertNotNull(app.spawnTimer);
        assertNotNull(app.ballQueue);
        //assertNull(app.ballsToRespawn);
        // Add more assertions based on expected state after loading a level
    }

    @Test
    // Tests the scenario where loading a level triggers game over.
    public void testLoadLevelGameOver() {
        app.loadLevel(3);
        assertTrue(app.gameOver);
        //assertNull(app.ballsToRespawn);
        // Add more assertions based on expected state after loading a level
    }

    @Test
    // Tests if spawning a ball correctly increases the drawable objects count.
    public void testSpawnNextBall() {
        int initialDrawablesCount = app.getDrawables().size();
        app.spawnNextBall();
        assertTrue(app.getDrawables().size() > initialDrawablesCount);
    }


    @Test
    // Test the spawnNextBall() method, when ballQueue is not empty but spawners is empty, no new ball should be added.
    public void testSpawnNextBall_BallQueueNotEmpty_SpawnersEmpty() {
        app.ballQueue.add("orange"); // Add an orange ball to the queue

        // Make sure the spawners list is empty
        app.spawners.clear();

        // Get the initial number of drawables
        int initialDrawablesCount = app.getDrawables().size();

        // Call spawnNextBall()
        app.spawnNextBall();

        // Verify that the ball is not added to the drawables list
        assertEquals(initialDrawablesCount, app.getDrawables().size(), "No new ball should be added when spawners are empty.");
    }


    @Test
    // Tests the scenario where no balls should be spawned if the ballQueue is empty.
    public void testSpawnNextBall_BallQueueEmpty() {
        // Make sure ballQueue is empty
        app.ballQueue.clear();

        // Get the initial number of drawables
        int initialDrawablesCount = app.getDrawables().size();

        // Call spawnNextBall()
        app.spawnNextBall();

        // Verify that the ball is not added to the drawables list
        assertEquals(initialDrawablesCount, app.getDrawables().size(), "No new ball should be added when ballQueue is empty.");
    }


    @Test
    // Tests if the score is correctly increased when a ball is captured by a matching hole.
    public void testIncreaseScore() {
        int initialScore = app.score;
        app.increaseScore(1); // Assuming 1 is a valid ball type
        assertTrue(app.score > initialScore);
    }

    @Test
    // Tests if the score decreases correctly when a ball is captured by a wrong hole.
    public void testDecreaseScore() {
        app.score = 100; // Set initial score
        int initialScore = app.score;
        app.decreaseScore(1); // Assuming 1 is a valid ball type
        assertTrue(app.score < initialScore);
    }

    @Test
    // Tests if the ball is correctly respawned and added to the queue.
    public void testRespawnBall() {
        // Set up a scenario where the ballQueue is empty, so adding the first ball will make its size 1
        app.ballQueue.clear();

        // Set spawnTimer to a value greater than 0 to cover the condition where spawnTimer > 0
        app.spawnTimer = 5;  // Non-zero value for spawnTimer

        // Create a test ball
        Ball testBall = new Ball(0, 0, null, 1);

        // Call respawnBall
        app.respawnBall(testBall);

        // Check if the ball was added to the respawn queue
        assertTrue(app.ballsToRespawn.contains(testBall), "Ball should be added to ballsToRespawn list");

        // Check if the ballQueue has exactly one element
        assertEquals(1, app.ballQueue.size(), "Ball queue should have exactly one ball after respawn");

        // Check if spawnTimer is set to 0 when the queue size is 1 and spawnTimer > 0
        assertEquals(0, app.spawnTimer, "Spawn timer should be reset to 0 when ballQueue.size() == 1 and spawnTimer > 0");
    }

    @Test
    // Tests if the correct color string is returned based on the ball type.
    public void testGetColorByType() {
        assertEquals("orange", app.getColorByType(1));
        assertEquals("blue", app.getColorByType(2));
        assertEquals("green", app.getColorByType(3));
        assertEquals("yellow", app.getColorByType(4));
        assertEquals("grey", app.getColorByType(0));
    }

    @Test
    // Tests if the correct ball type is returned based on the color string.
    public void testGetTypeByColor() {
        assertEquals(1, app.getTypeByColor("orange"));
        assertEquals(2, app.getTypeByColor("blue"));
        assertEquals(3, app.getTypeByColor("green"));
        assertEquals(4, app.getTypeByColor("yellow"));
        assertEquals(0, app.getTypeByColor("grey"));
    }

    @Test
    // Tests if a non-null image is returned for each ball color.
    public void testGetBallImageByColor() {
        assertNotNull(app.getBallImageByColor("blue"));
        assertNotNull(app.getBallImageByColor("orange"));
        assertNotNull(app.getBallImageByColor("green"));
        assertNotNull(app.getBallImageByColor("yellow"));
        assertNotNull(app.getBallImageByColor("grey"));
    }

    @Test
    // Tests loading of the level layout and ensures the grid is initialized.
    public void testLoadLevelLayout() {
        app.loadLevelLayout("level3.txt");
        assertNotNull(App.getGrid());
        // Add more assertions based on the expected state of the grid
    }

    @Test
    // Tests if the speed boost is correctly applied to a ball by a speed tile.
    public void testSpeedTileBehavior() {
        // Create a ball and a speed tile
        Ball ball = new Ball(50, 50, null, 1);
        ball.setVy(-2.0f);
        SpeedTile speedTile = new SpeedTile(50, 50, null, '^');

        // Apply speed boost
        speedTile.applySpeedBoost(ball);

        // Check if the ball's velocity has changed
        assertTrue(ball.getVy() < -2.0f);
    }

    @Test
    // Tests if the win condition is correctly triggered when no balls are left on the board.
    public void testWinCondition() {
        // Load the first level
        app.loadLevel(0);

        // Clear the ball queue and remove any existing balls
        app.ballQueue.clear();
        app.getDrawables().removeIf(d -> d instanceof Ball);

        // Print the state before checking win condition
        long ballCount = app.getDrawables().stream().filter(d -> d instanceof Ball).count();
        app.noBallsOnBoard = (ballCount == 0);
        System.out.println("Ball queue size: " + app.ballQueue.size());
        System.out.println("Ball queue size: " + app.ballQueue.size());

        // Trigger the win condition check
        app.checkWinCondition();

        // Print the state after checking the win condition
        System.out.println("Level won: " + app.levelWon);

        // Assert that the level is won
        assertTrue(app.levelWon, "Ball queue size: " + app.ballQueue.size() + "Drawable size: " + + ballCount + "Level won: " + app.levelWon);
    }

    @Test
    // Tests if the game correctly pauses and unpauses when the spacebar is pressed.
    public void testPauseUnpause() {
        app.levelEnded = false;

        // Simulate the first press of the spacebar (pause the game)
        app.key = ' '; // Manually set the key variable
        app.keyPressed(new KeyEvent(app, 0, KEY_PRESS, 0, ' ', SPACE_KEY));
        System.out.println("After first spacebar press, paused: " + app.paused);
        assertTrue(app.paused, "Game should be paused after first spacebar press.");

        // Simulate pressing the spacebar a second time (unpause the game)
        app.key = ' '; // Manually set the key variable
        app.keyPressed(new KeyEvent(app, 0, KEY_PRESS, 0, ' ', SPACE_KEY));
        System.out.println("After second spacebar press, paused: " + app.paused);
        assertFalse(app.paused, "Game should be unpaused after second spacebar press.");
    }


    @Test
    // Tests if a player line is correctly created and added to the playerLines list.
    public void testPlayerLineCreation() {
        app.levelEnded = false;

        // Ensure playerLines is initialized
        if (app.playerLines == null) {
            app.playerLines = new ArrayList<>();
        }

        // Adjust for TOP_MARGIN (as in your game logic)
        int adjustedYStart = 50 + App.TOP_MARGIN;
        int adjustedYEnd = 100 + App.TOP_MARGIN;

        // Simulate mouse press event at (50, 50) adjusted for TOP_MARGIN
        MouseEvent mousePressEvent = new MouseEvent(
                null,                         // Native object (can be null)
                System.currentTimeMillis(),    // Current time in milliseconds
                MOUSE_PRESSED,                 // Action (pressing the mouse)
                0,                             // Modifiers (no modifiers for this test)
                50,                            // X-coordinate
                adjustedYStart,                // Y-coordinate adjusted for TOP_MARGIN
                PConstants.LEFT,               // Left mouse button (37 for LEFT)
                1                              // Click count (single click)
        );
        app.mousePressed(mousePressEvent);

        // Assert that currentLine has been initialized after the mouse press
        assertNotNull(app.currentLine, "currentLine should not be null after mouse pressed.");

        // Simulate mouse drag event from (50, 50) to (100, 100) adjusted for TOP_MARGIN
        MouseEvent mouseDragEvent = new MouseEvent(
                null,                         // Native object (can be null)
                System.currentTimeMillis(),    // Current time in milliseconds
                MOUSE_DRAGGED,                 // Action (dragging the mouse)
                0,                             // Modifiers (no modifiers for this test)
                100,                           // X-coordinate of drag end
                adjustedYEnd,                  // Y-coordinate of drag end adjusted for TOP_MARGIN
                PConstants.LEFT,               // Left mouse button (37 for LEFT)
                1                              // Click count (single click)
        );
        app.mouseDragged(mouseDragEvent);

        // Assert that the current line has at least one point
        assertNotNull(app.currentLine, "currentLine should still exist during drag.");
        assertFalse(app.currentLine.getPoints().isEmpty(), "currentLine should contain points during drag.");

        // Simulate mouse release event to complete the line creation
        MouseEvent mouseReleaseEvent = new MouseEvent(
                null,                         // Native object (can be null)
                System.currentTimeMillis(),    // Current time in milliseconds
                MOUSE_RELEASED,                // Action (releasing the mouse)
                0,                             // Modifiers (no modifiers for this test)
                100,                           // X-coordinate of release
                adjustedYEnd,                  // Y-coordinate of release adjusted for TOP_MARGIN
                PConstants.LEFT,               // Left mouse button (37 for LEFT)
                1                              // Click count (single click)
        );
        app.mouseReleased(mouseReleaseEvent);

        // Ensure that the line has been added to playerLines after releasing the mouse
        assertFalse(app.playerLines.isEmpty(), "PlayerLines should contain a line after drawing.");
        assertEquals(1, app.playerLines.size(), "There should be exactly one PlayerLine created.");

        // Ensure that the created line has points
        PlayerLine createdLine = app.playerLines.get(0);
        assertFalse(createdLine.getPoints().isEmpty(), "The created PlayerLine should have points.");
        assertTrue(createdLine.getPoints().size() > 1, "The PlayerLine should contain multiple points after dragging.");
    }


    @Test
    // Tests if pressing 'R' resets the level when the game is not over.
    public void testKeyPressedRGameNotOver() {
        app.levelEnded = true;
        app.currentLevelIndex = 0;
        app.score = 100;
        app.key = 'r';
        app.keyPressed(new KeyEvent(app, 0, KEY_PRESS, 0, 'r', 82));
        assertFalse(app.levelEnded);
        assertEquals(0, app.currentLevelIndex);
        assertEquals(0, app.score);
        assertFalse(app.playingWinAnimation);
        assertTrue(app.playerLines.isEmpty());
    }

    @Test
    // Tests if pressing 'R' resets the game and sets the game over state back to false.
    public void testKeyPressedRGameOver() {
        // Simulate the condition where the game is over
        app.levelEnded = true;
        app.gameOver = true;

        // Set a non-zero level and score to ensure they are reset
        app.currentLevelIndex = 5;  // Set to a non-zero value
        app.score = 100;            // Set to a non-zero value

        // Simulate pressing the 'r' key
        app.key = 'r';
        app.keyPressed(new KeyEvent(app, 0, KEY_PRESS, 0, 'r', 82));

        // Assertions to check if the 'if (gameOver)' block was executed
        assertFalse(app.levelEnded, "Level ended should be false after pressing 'r'.");
        assertEquals(0, app.currentLevelIndex, "Level index should be reset to 0 after pressing 'r' when game is over.");
        assertEquals(0, app.score, "Score should be reset to 0 after pressing 'r' when game is over.");
        assertFalse(app.playingWinAnimation, "Playing win animation should be false after pressing 'r'.");
        assertTrue(app.playerLines.isEmpty(), "Player lines should be cleared after pressing 'r'.");
        assertFalse(app.gameOver, "GameOver should be reset to false after pressing 'r'.");
    }

    @Test
    // Tests if a right-click removes the player line when clicked on it.
    public void testMousePressedRight() {
        app.levelEnded = false;
        app.paused = false;
        app.playerLines.clear();
        PlayerLine testLine = new PlayerLine();
        testLine.addPoint(50, 50); // The point (50, 50) should match the position for right-click to remove the line
        app.playerLines.add(testLine);

        // Ensure there is a line before mousePressed
        assertFalse(app.playerLines.isEmpty(), "PlayerLines should not be empty before mousePressed.");

        // Simulate right-click at the exact location of the line
        int adjustedY = 50 + App.TOP_MARGIN;  // Adjust for TOP_MARGIN
        app.mousePressed(new MouseEvent(app, 0, MOUSE_PRESSED, 0, 50, adjustedY, BUTTON_RIGHT, 10));

        // Check if playerLines is empty after right-click (meaning the line was removed)
        assertTrue(app.playerLines.isEmpty(), "PlayerLines should be empty after right-click.");
    }

    @Test
    // Tests if holding control while left-clicking removes a player line.
    public void testMousePressedLeftWithControl() {
        app.levelEnded = false;
        app.playerLines = new ArrayList<>();
        PlayerLine testLine = new PlayerLine();
        testLine.addPoint(50, 50);
        app.playerLines.add(testLine);

        // Create a custom MouseEvent to simulate a left click while holding down the Control key
        MouseEvent customEvent = new MouseEvent(app, 0, MOUSE_PRESSED, 0, 50, 120, BUTTON_LEFT, 1) {
            @Override
            public boolean isControlDown() {
                return true;
            }
        };

        app.mousePressed(customEvent);
        assertTrue(app.playerLines.isEmpty(), "Player lines should be cleared when Control is pressed");
    }

    @Test
    // Tests if the time bonus is correctly added to the score when a level is won.
    public void testTimeBonusCalculation() {
        // Set up a level with some time left
        app.loadLevel(0);
        app.score = 100;
        app.levelTimer = 100;
        app.timeBonus = app.levelTimer;

        // Trigger win condition
        app.levelWon = true;
        app.addTimeBonusToScore();

        // Check if time bonus was added to score
        assertTrue(app.score > 100);
    }

    @Test
    // Tests if the game correctly triggers game over when the last level is reached.
    public void testGameOverCondition() {
        // Set up a game over condition
        app.currentLevelIndex = app.levels.size();
        app.loadLevel(app.currentLevelIndex);

        // Check if game is over
        assertTrue(app.gameOver);
    }

    @Test
    // Tests if balls are correctly dequeued after spawning a new ball.
    public void testBallQueueManagement() {
        // Add balls to the queue
        app.ballQueue.clear();
        app.ballQueue.add("blue");
        app.ballQueue.add("grey");

        // Spawn a ball
        app.spawnNextBall();

        // Check if the queue size decreased
        assertEquals(1, app.ballQueue.size());
    }

    @Test
    // Tests if the level resets correctly after pressing the 'r' key.
    public void testLevelReset() {
        // Set up a level
        app.loadLevel(0);
        app.score = 100;
        // Record the state before resetting
        float levelTimerBefore = app.levelTimer;
        float spawnTimerBefore = app.spawnTimer;
        List<String> ballQueueBefore = new ArrayList<>(app.ballQueue); // Clone the ballQueue to compare later


        // Reset the level
        app.keyPressed(new processing.event.KeyEvent(null, 0, processing.event.KeyEvent.PRESS, 0, 'r', 82));

        // Player lines should be cleared
        assertTrue(app.playerLines.isEmpty());

        // Win animation should not be playing
        assertFalse(app.playingWinAnimation);

        // Check if the levelTimer, spawnTimer, and ballQueue are reset after the level reset
        assertEquals(levelTimerBefore, app.levelTimer);  // Expect new levelTimer after reset
        assertEquals(spawnTimerBefore, app.spawnTimer);  // Expect new spawnTimer after reset
        assertEquals(ballQueueBefore, app.ballQueue);    // Expect a different ballQueue after reset

    }

    @Test
    // Tests if the game window size is set correctly.
    public void testSettings() {
        // Test if the window size is set correctly
        assertEquals(App.WIDTH, app.width);
        assertEquals(App.HEIGHT, app.height);
    }

    @Test
    // Tests if essential components are initialized during setup.
    public void testSetup() {
        assertNotNull(app.config);
        assertNotNull(app.levels);
        assertNotNull(app.playerLines);
        assertNotNull(app.scoreIncreaseMap);
        assertNotNull(app.scoreDecreaseMap);
    }

    @Test
    public void testKeyReleased() {
        // This method is empty in the current implementation
        // We can test that it doesn't throw any exceptions
        assertDoesNotThrow(() -> app.keyReleased());
    }

    @Test
    // Tests if the UI elements are drawn correctly (by checking for exceptions).
    public void testDrawUI() {
        // Test drawing UI elements
        app.score = 100;
        app.levelTimer = 60 * App.FPS;
        app.draw(); // This will call drawUI internally
        // Since we can't easily check the drawn elements, we'll just ensure it doesn't throw exceptions
        assertDoesNotThrow(() -> app.draw());
    }

    @Test
    // Tests if the ball queue is drawn correctly (by checking for exceptions).
    public void testDrawBallQueue() {
        app.ballQueue.add("blue");
        app.ballQueue.add("red");
        app.draw(); // This will call drawBallQueue internally
        assertDoesNotThrow(() -> app.draw());
    }

    @Test
    // Tests if the game over message is displayed correctly.
    public void testDisplayMessage() {
        app.gameOver = true;
        app.draw(); // This will call displayMessage internally
        assertDoesNotThrow(() -> app.draw());
    }

    @Test
    // Tests if the grid is drawn correctly (by checking for exceptions).
    public void testDrawGrid() {
        app.draw(); // This will call drawGrid internally
        assertDoesNotThrow(() -> app.draw());
    }

    @Test
    // Tests if the correct overlay image is returned for different tile types.
    public void testGetTileOverlayImage() {
        assertNotNull(app.getTileOverlayImage("X"));
        assertNotNull(app.getTileOverlayImage("1"));
        assertNotNull(app.getTileOverlayImage("S"));
        assertNotNull(app.getTileOverlayImage("H0"));
        assertNotNull(app.getTileOverlayImage("B1"));
    }

    @Test
    // Tests if the correct drawable object is returned for different tile types.
    public void testGetDrawableForTileType() {
        assertNotNull(app.getDrawableForTileType("X", 0, 0, null, '0'));
        assertNotNull(app.getDrawableForTileType("S", 0, 0, null, '0'));
        assertNotNull(app.getDrawableForTileType("H1", 0, 0, null, '1'));
        assertNotNull(app.getDrawableForTileType("B2", 0, 0, null, '2'));
    }

    @Test
    // Tests if the correct ball image is returned based on the ball type.
    public void testGetBallImageByType() {
        assertNotNull(App.getBallImageByType(0));
        assertNotNull(App.getBallImageByType(1));
        assertNotNull(App.getBallImageByType(2));
        assertNotNull(App.getBallImageByType(3));
        assertNotNull(App.getBallImageByType(4));
    }

    @Test
    // Tests if a player line is removed correctly when clicked.
    public void testRemoveLine() {
        PlayerLine line = new PlayerLine();
        line.addPoint(50, 50);
        app.playerLines.add(line);
        app.removeLine(50, 50);
        assertTrue(app.playerLines.isEmpty());
    }

    @Test
    // Tests if the win animation is initialized correctly.
    public void testInitializeWinAnimation() {
        app.initializeWinAnimation();
        assertEquals(0, app.leftX);
        assertEquals(0, app.leftY);
        assertEquals(App.BOARD_WIDTH - 1, app.rightX);
        assertEquals(App.BOARD_HEIGHT - 1, app.rightY);
    }

    @Test
    // Tests if performing a step in the win animation updates the positions correctly.
    public void testPerformWinAnimationStep() {
        app.initializeWinAnimation();
        app.performWinAnimationStep();
        // Check if the animation step changes the position
        assertTrue(app.leftX > 0 || app.leftY > 0 || app.rightX < App.BOARD_WIDTH - 1 || app.rightY < App.BOARD_HEIGHT - 1);
    }

    @Test
    // Tests if the updatePosition method correctly updates x and y based on conditions.
    public void testUpdatePosition() {
        // Test case 1: y == 0 and x < BOARD_WIDTH - 1
        app.updatePosition(0, 0, true);  // x=0, y=0 (start at top-left corner)
        assertEquals(1, app.leftX);  // Expected x to increment by 1
        assertEquals(0, app.leftY);  // y should stay the same

        // Test case 2: x == BOARD_WIDTH - 1 and y < BOARD_HEIGHT - 1
        app.updatePosition(App.BOARD_WIDTH - 1, 0, false);  // x=BOARD_WIDTH-1, y=0 (top-right corner)
        assertEquals(App.BOARD_WIDTH - 1, app.rightX);  // x should stay the same
        assertEquals(1, app.rightY);  // Expected y to increment by 1

        // Test case 3: y == BOARD_HEIGHT - 1 and x > 0
        app.updatePosition(App.BOARD_WIDTH - 1, App.BOARD_HEIGHT - 1, true);  // bottom-right corner
        assertEquals(App.BOARD_WIDTH - 2, app.leftX);  // Expected x to decrement by 1
        assertEquals(App.BOARD_HEIGHT - 1, app.leftY);  // y should stay the same

        // Test case 4: x == 0 and y > 0
        app.updatePosition(0, App.BOARD_HEIGHT - 1, false);  // bottom-left corner
        assertEquals(0, app.rightX);  // x should stay the same
        assertEquals(App.BOARD_HEIGHT - 2, app.rightY);  // Expected y to decrement by 1
    }

    @Test
    // Tests if the initial state is drawn correctly when the game starts.
    public void testInitialDrawState() {
        app.levelEnded = false;
        app.paused = false;
        app.gameOver = false;
        app.ballQueue.add("blue");
        app.levelTimer = 100;

        // Call the draw method to test whether the initial state is drawn correctly
        app.draw();

        // Check if the game is not over
        assertFalse(app.levelEnded);

        // Check if the time is counting down
        assertTrue(app.levelTimer > 0);
    }

    @Test
    // Tests if balls are correctly generated and updated during gameplay.
    public void testBallGenerationAndUpdate() {
        app.levelEnded = false;
        app.paused = false;
        app.gameOver = false;
        app.ballQueue.add("blue"); // Add a ball to the queue
        app.spawnNextBall(); // Generate a ball

        // Check if a ball was spawned
        assertFalse(app.getDrawables().isEmpty());

        // Simulate update and drawing
        app.draw();
        List<Drawable> drawables = app.getDrawables();
        boolean ballExists = false;
        for (Drawable drawable : drawables) {
            if (drawable instanceof Ball) {
                ballExists = true;
                break;
            }
        }
        assertTrue(ballExists);
    }

    @Test
    // Tests if the game pauses correctly and stops ball movement.
    public void testPause() {
        app.levelEnded = false;
        app.paused = false;
        app.gameOver = false;
        app.ballQueue.add("blue"); // Add a ball
        app.spawnNextBall(); // Generate a ball

        app.paused = true;
        app.draw();
        for (Drawable drawable : app.getDrawables()) {
            if (drawable instanceof Ball) {
                Ball ball = (Ball) drawable;
                assertEquals(0, ball.getVx());
                assertEquals(0, ball.getVy()); // The ball should stop moving
            }
        }
    }

    @Test
    // Tests if the game handles the game over state correctly.
    public void testGameOverState() {
        app.levelEnded = false;
        app.paused = false;
        app.gameOver = true;
        app.ballQueue.clear();
        app.spawnNextBall(); // Generate a ball

        // Check if the game is handled correctly after it ends
        app.draw();
        assertTrue(app.playerLines.isEmpty());
    }

    @Test
    // Tests if the game ends when the level timer reaches 0.
    public void testTimeUpCondition() {
        app.levelEnded = false;
        app.paused = false;
        app.gameOver = false;
        app.levelTimer = 0; // Set the time to 0

        // Call the draw method, the simulation time ends
        app.draw();

        // Check if the game is considered failed after the time is up
        assertTrue(app.levelEnded);
        assertFalse(app.levelWon); // Game failed, time expired
    }

    @Test
    // Tests if the win animation plays and progresses to the next level after completion.
    public void testWinAnimation() {
        app.levelEnded = true;
        app.levelWon = true;
        app.playingWinAnimation = true;
        app.levelTimer = 1000;

        app.draw();

        // Check if the animation is playing
        assertTrue(app.playingWinAnimation);

        // After the simulation animation ends, check whether the status is updated
        app.levelTimer = 0;
        app.draw();
        assertFalse(app.playingWinAnimation);
        assertEquals(1, app.currentLevelIndex); // The next level is loaded
    }

    @Test
    // Tests if the main method runs without throwing exceptions.
    public void testMainMethod() {
        try {
            String[] args = {};
            App.main(args);  // Call the main method with no arguments
            assertTrue(true);  // If no exceptions are thrown, the test passes
        } catch (Exception e) {
            fail("Main method threw an exception: " + e.getMessage());
        }
    }

}
