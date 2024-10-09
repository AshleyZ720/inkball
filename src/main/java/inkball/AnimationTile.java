package inkball;

import processing.core.PApplet;
import processing.core.PImage;

public class AnimationTile {
    public int x;
    public int y;
    public float remainingTime; // 剩余时间
    private PImage originalOverlayImage; // 保存原始的overlayImage

    public AnimationTile(int x, int y, float duration, PImage originalOverlayImage) {
        this.x = x;
        this.y = y;
        this.remainingTime = duration;
        this.originalOverlayImage = originalOverlayImage;
    }

    public void update() {
        remainingTime -= 1; // 每帧减少1
    }

    public boolean isExpired() {
        return remainingTime <= 0;
    }

    public PImage getOriginalOverlayImage() {
        return originalOverlayImage;
    }
}

