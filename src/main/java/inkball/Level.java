package inkball;

import processing.core.PApplet;
import processing.core.PImage;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Level {
    public String layout;
    public int time;
    public float spawnInterval;
    public float scoreIncreaseModifier;
    public float scoreDecreaseModifier;
    public List<String> balls;

    public Level(JSONObject levelData) {
        this.layout = levelData.getString("layout");
        this.time = levelData.getInt("time");
        this.spawnInterval = levelData.getFloat("spawn_interval");
        this.scoreIncreaseModifier = levelData.getFloat("score_increase_from_hole_capture_modifier");
        this.scoreDecreaseModifier = levelData.getFloat("score_decrease_from_wrong_hole_modifier");

        JSONArray ballsArray = levelData.getJSONArray("balls");
        balls = new ArrayList<>();
        for (int i = 0; i < ballsArray.size(); i++) {
            balls.add(ballsArray.getString(i));
        }
    }
}

