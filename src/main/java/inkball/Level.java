package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * The {@code Level} class represents a game level in the Inkball game.
 * Each level contains a layout, time limit, spawn interval for balls,
 * score modifiers for correct and incorrect captures, and a list of ball colors to spawn.
 */
public class Level {

    /** The layout of the level, typically represented as a grid. */
    public String layout;

    /** The time available to complete the level, in seconds. */
    public int time;

    /** The interval between ball spawns, in seconds. */
    public float spawnInterval;

    /** Modifier for increasing the score when a ball is captured in the correct hole. */
    public float scoreIncreaseModifier;

    /** Modifier for decreasing the score when a ball is captured in the wrong hole. */
    public float scoreDecreaseModifier;

    /** List of ball colors to be spawned during the level. */
    public List<String> balls;

    /**
     * Constructs a {@code Level} object from a given JSON object containing level data.
     * The JSON object contains the layout, time limit, spawn interval, score modifiers,
     * and a list of balls to spawn during the level.
     *
     * @param levelData The JSON object containing the level data.
     */
    public Level(JSONObject levelData) {
        // Extract layout, time, and spawn interval
        this.layout = levelData.getString("layout");
        this.time = levelData.getInt("time");
        this.spawnInterval = levelData.getFloat("spawn_interval");

        // Extract score modifiers for ball captures
        this.scoreIncreaseModifier = levelData.getFloat("score_increase_from_hole_capture_modifier");
        this.scoreDecreaseModifier = levelData.getFloat("score_decrease_from_wrong_hole_modifier");

        // Extract the list of ball colors to spawn during the level
        JSONArray ballsArray = levelData.getJSONArray("balls");
        balls = new ArrayList<>();
        for (int i = 0; i < ballsArray.size(); i++) {
            balls.add(ballsArray.getString(i));
        }
    }
}
