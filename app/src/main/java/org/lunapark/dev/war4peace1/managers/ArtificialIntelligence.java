package org.lunapark.dev.war4peace1.managers;

import org.lunapark.dev.war4peace1.objects.Body2d;
import org.lunapark.dev.war4peace1.objects.Character;
import org.lunapark.dev.war4peace1.objects.CharacterData;

import fr.arnaudguyon.smartgl.opengl.Object3D;

import static org.lunapark.dev.war4peace1.utils.Consts.BOT_RANGE;

/**
 * Artificial intelligence
 * Created by znak on 29.03.2017.
 */

public class ArtificialIntelligence {

    private CharacterData data;
    private WorldManager worldManager;
    private Body2d sight;

    public ArtificialIntelligence(WorldManager worldManager) {
        this.worldManager = worldManager;
        data = new CharacterData();
        sight = new Body2d();
        sight.height = 0.5f;
        sight.width = 0.5f;
        sight.x = 0;
        sight.z = 0;
    }

    private boolean checkStraightSight(float dx, float dz, float x, float z, float playerX, float playerZ) {
        float step = 0.5f;
        for (float i = 0; i < BOT_RANGE; i+= step) {
            sight.x = x + i * dx;
            sight.z = z + i * dz;
            // Check intersect with player
            boolean intersect = worldManager.intersectPlayer(sight, playerX, playerZ);
            if (intersect) return true;
            // Check intersect with walls
            intersect = worldManager.checkWallIntersect(sight);
            if (intersect) return false;
        }
        return false;
    }

    public CharacterData getData(Character character, Character player) {

        float playerX = player.getBase().getPosX();
        float playerZ = player.getBase().getPosZ();

        float x = character.getBase().getPosX();
        float z = character.getBase().getPosZ();
        int dx = 0;
        int dz = 0;
        if (worldManager.getDistance2d(x, z, playerX, playerZ) < BOT_RANGE) {

            if (x > playerX + 1) {
                dx = -1;
                dz = 0;
            }

            if (x < playerX - 1) {
                dx = 1;
                dz = 0;
            }

            if (z > playerZ + 1) {
                dx = 0;
                dz = -1;
            }

            if (z < playerZ - 1) {
                dx = 0;
                dz = 1;
            }

            data.canShoot = checkStraightSight(dx, dz, x, z, playerX, playerZ);

        } else {
            data.canShoot = false;
        }
        data.deltaX = dx;
        data.deltaZ = dz;
        return data;
    }
}
