package org.lunapark.dev.war4peace1.utils;

/**
 * War 4 peace. Episode 1
 * Constants
 * Created by znak on 25.03.2017.
 */

public class Consts {
    // System
//    public static final int SCREEN_WIDTH = 800;
//    public static final int SCREEN_WIDTH_HALF = SCREEN_WIDTH / 2;
//    public static final int SCREEN_HEIGHT = 480;
    // World
    public static final int FLOOR_SIZE = 10;
    public static final int FLOOR_SEGMENTS = 5;
    public static final int FLOOR_DISTANCE = FLOOR_SIZE * FLOOR_SEGMENTS;
    public static final int FLOOR_FAR_DISTANCE = FLOOR_DISTANCE / 2;

    // Player
    public static final float CHARACTER_WIDTH = 1;
    public static final float CHARACTER_HEIGHT = 1;
    public static final int SPEED_PLAYER = 10;
    public static final int SPEED_LEGS = 500;

    // Camera
    public static final int CAMERA_Y = 15; // Height 15 (1)
    public static final int CAMERA_X_ANGLE = -90; // Look down -90 (-30)
    public static final int CAMERA_SPEED = 10;

    // Gun fire
    public static final int FIRE_RATE = 100; // milliseconds
    public static final int MAX_BULLETS = 100; // maximum bullets
    public static final int BULLET_SPEED = 5;
    public static final int BULLET_DISTANCE = 10; // after this bullet

    // Bot
    public static final int BOT_RANGE = 20;
}
