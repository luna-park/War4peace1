package org.lunapark.dev.war4peace1.managers;

import android.util.Log;

import org.lunapark.dev.war4peace1.ai.WaveAlgorithm;
import org.lunapark.dev.war4peace1.ai.WaveCell;
import org.lunapark.dev.war4peace1.objects.Body2d;
import org.lunapark.dev.war4peace1.objects.Bot;
import org.lunapark.dev.war4peace1.objects.Character;
import org.lunapark.dev.war4peace1.objects.CharacterData;
import org.lunapark.dev.war4peace1.objects.Point2d;

import java.util.Random;
import java.util.Stack;

import static org.lunapark.dev.war4peace1.utils.Consts.BOT_RANGE;
import static org.lunapark.dev.war4peace1.utils.Consts.CHARACTER_HEIGHT;
import static org.lunapark.dev.war4peace1.utils.Consts.CHARACTER_WIDTH;

/**
 * Artificial intelligence
 * Created by znak on 29.03.2017.
 */

public class BotFather {

    private CharacterData data;
    private WorldManager worldManager;
    private Body2d sight, // Body2d для проверки попадает ли игрок в зону прямой видимости
            seeker;
    private WaveAlgorithm waveAlgorithm;
    private Random random;

    public BotFather(WorldManager worldManager) {
        this.worldManager = worldManager;
        waveAlgorithm = new WaveAlgorithm();
        random = new Random();
        data = new CharacterData();
        sight = new Body2d();
        seeker = new Body2d();

        seeker.height = CHARACTER_HEIGHT;
        seeker.width = CHARACTER_WIDTH;
        seeker.x = 0;
        seeker.z = 0;

        sight.height = 0.5f;
        sight.width = 0.5f;
        sight.x = 0;
        sight.z = 0;
    }

    // Попадает ли игрок в зону прямой видимости
    private boolean checkStraightSight(float dx, float dz, float x, float z, float playerX, float playerZ) {
        float step = 0.5f;
        for (float i = 0; i < BOT_RANGE; i += step) {
            sight.x = x + i * dx;
            sight.z = z + i * dz;
            // Check intersect with player
            boolean intersect = worldManager.intersectCharacter(sight, playerX, playerZ);
            if (intersect) return true;
            // Check intersect with walls
            intersect = worldManager.checkWallIntersect(sight);
            if (intersect) return false;
        }
        return false;
    }

    public CharacterData getData(Bot bot, Character player) {

        float playerX = player.getBase().getPosX();
        float playerZ = player.getBase().getPosZ();

        float x = bot.getBase().getPosX();
        float z = bot.getBase().getPosZ();
        int dx = 0;
        int dz = 0;
        if (worldManager.getDistance2d(x, z, playerX, playerZ) < BOT_RANGE) {

            Bot.BotState botState = bot.getState();

            if (botState == Bot.BotState.IDLE) {
                createSeekPath(bot, playerX, playerZ);
                bot.setState(Bot.BotState.SEEK);
            }

            if (botState == Bot.BotState.SEEK) {
//                seek(bot);

            }
            dx = bot.getDx();
            dz = bot.getDz();
            data.canShoot = checkStraightSight(dx, dz, x, z, playerX, playerZ);

        } else {

            bot.setState(Bot.BotState.IDLE);
            data.canShoot = false;
            if (!bot.busy()) {
                int rand = random.nextInt(10);
                switch (rand) {
                    case 0:
                        dx = 1;
                        dz = 0;
                        break;
                    case 1:
                        dx = -1;
                        dz = 0;
                        break;
                    case 2:
                        dx = 0;
                        dz = 1;
                        break;
                    case 3:
                        dx = 0;
                        dz = -1;
                        break;
//                    case 4:
//                        dx = 0;
//                        dz = 0;
//                        break;
                }

            } else {
                dx = bot.getDx();
                dz = bot.getDz();
            }
        }

        data.deltaX = dx;
        data.deltaZ = dz;
        return data;
    }

    // TODO Create bot path
    private void createSeekPath(Bot bot, float playerX, float playerZ) {
        Stack<Point2d> seekPath = bot.getSeekPath();
        float botX = bot.getBase().getPosX();
        float botZ = bot.getBase().getPosZ();
        float distance;

        int gridSize = waveAlgorithm.getSize();
        WaveCell[][] grid = waveAlgorithm.getGrid();
        boolean playerFound = false;
        boolean botFound = false;
        // Place bot in center of grid
        int mid = gridSize / 2;
        int iBotX = Math.round(botX);
        int iBotZ = Math.round(botZ);

//        grid[mid][mid].value = 0;
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                WaveCell waveCell = grid[i][j];
                int x = iBotX - mid + i;
                int z = iBotZ - mid + j;
                int value = -2;
                seeker.x = x;
                seeker.z = z;

                if (worldManager.checkWallIntersect(seeker)) value = -1;
                if (worldManager.intersectCharacter(seeker, playerX, playerZ) && !playerFound) {
                    value = -3;
                    playerFound = true;
                }
                if (worldManager.intersectCharacter(seeker, botX, botZ) && !botFound) {
                    value = 0;
                    botFound = true;
                }

                waveCell.value = value;
            }
        }

        Log.e("GRID", "-------------------- Wave grid with walls ---------------");
        for (int i = 0; i < gridSize; i++) {
            String s = "";
            for (int j = 0; j < gridSize; j++) {
                s += grid[i][j].value + " ";
            }
            Log.e("GRID", s);
        }

//        do {
//            distance = worldManager.getDistance2d(x, z, playerX, playerZ);
//            Point2d point = new Point2d();
//            float pX = x;
//            float pZ = z;
//
//            if (x > playerX && x < playerX + CHARACTER_WIDTH) {
//                pX -= BOT_SEEK_STEP;
//            } else if (x < playerX && x > playerX - CHARACTER_WIDTH) {
//                pX += BOT_SEEK_STEP;
//            } else if (z > playerZ && z < playerZ + CHARACTER_HEIGHT) {
//                pZ -= BOT_SEEK_STEP;
//            } else if (z < playerX && z > playerZ - CHARACTER_HEIGHT) {
//                pZ += BOT_SEEK_STEP;
//            }
//
//            point.x = pX;
//            point.z = pZ;
//
//            seekPath.add(point);
//
//        } while (distance > BOT_MIN_RANGE);


    }

    // TODO Bot follow the path
    private void seek(Bot bot) {
        Stack<Point2d> seekPath = bot.getSeekPath();
        Point2d point2d = seekPath.peek();
        int dx = 0;
        int dz = 0;

        //            if (x > playerX + 1) {
//                dx = -1;
//                dz = 0;
//            }
//
//            if (x < playerX - 1) {
//                dx = 1;
//                dz = 0;
//            }
//
//            if (z > playerZ + 1) {
//                dx = 0;
//                dz = -1;
//            }
//
//            if (z < playerZ - 1) {
//                dx = 0;
//                dz = 1;
//            }
        data.deltaX = dx;
        data.deltaZ = dz;
    }

    private boolean checkPoint(float x, float z) {
        seeker.x = x;
        seeker.z = z;
        return !worldManager.checkWallIntersect(seeker);
    }
}
