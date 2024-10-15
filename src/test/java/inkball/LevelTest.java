package inkball;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.data.JSONArray;
import processing.data.JSONObject;

import static org.junit.jupiter.api.Assertions.*;

public class LevelTest {

    private JSONObject mockLevelData;
    private Level level;

    @BeforeEach
    public void setUp() {
        mockLevelData = new JSONObject();
        mockLevelData.setString("layout", "test_layout");
        mockLevelData.setInt("time", 120);
        mockLevelData.setFloat("spawn_interval", 2.5f);
        mockLevelData.setFloat("score_increase_from_hole_capture_modifier", 1.5f);
        mockLevelData.setFloat("score_decrease_from_wrong_hole_modifier", 0.5f);

        JSONArray ballsArray = new JSONArray();
        ballsArray.append("red");
        ballsArray.append("blue");
        mockLevelData.setJSONArray("balls", ballsArray);

        level = new Level(mockLevelData);
    }

    @Test
    // Tests the constructor of the Level class with valid mock data and ensures all fields are properly initialized.
    public void testConstructor() {
        assertNotNull(level);
        assertEquals("test_layout", level.layout);
        assertEquals(120, level.time);
        assertEquals(2.5f, level.spawnInterval, 0.001);
        assertEquals(1.5f, level.scoreIncreaseModifier, 0.001);
        assertEquals(0.5f, level.scoreDecreaseModifier, 0.001);
        assertEquals(2, level.balls.size());
        assertTrue(level.balls.contains("red"));
        assertTrue(level.balls.contains("blue"));
    }

    @Test
    // Tests the behavior of the Level class when the balls array is empty.
    public void testEmptyBallsList() {
        JSONObject emptyBallsData = new JSONObject();
        emptyBallsData.setString("layout", "empty_layout");
        emptyBallsData.setInt("time", 60);
        emptyBallsData.setFloat("spawn_interval", 1.0f);
        emptyBallsData.setFloat("score_increase_from_hole_capture_modifier", 1.0f);
        emptyBallsData.setFloat("score_decrease_from_wrong_hole_modifier", 1.0f);
        emptyBallsData.setJSONArray("balls", new JSONArray());

        Level emptyBallsLevel = new Level(emptyBallsData);
        assertTrue(emptyBallsLevel.balls.isEmpty());
    }

    @Test
    // Tests the behavior of the Level class when the balls array contains a large number of entries.
    public void testLargeBallsList() {
        JSONObject largeBallsData = new JSONObject();
        largeBallsData.setString("layout", "large_layout");
        largeBallsData.setInt("time", 300);
        largeBallsData.setFloat("spawn_interval", 0.5f);
        largeBallsData.setFloat("score_increase_from_hole_capture_modifier", 2.0f);
        largeBallsData.setFloat("score_decrease_from_wrong_hole_modifier", 0.1f);

        JSONArray largeBallsArray = new JSONArray();
        for (int i = 0; i < 100; i++) {
            largeBallsArray.append("ball" + i);
        }
        largeBallsData.setJSONArray("balls", largeBallsArray);

        Level largeBallsLevel = new Level(largeBallsData);
        assertEquals(100, largeBallsLevel.balls.size());
    }
}