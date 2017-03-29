package org.lunapark.dev.war4peace1.managers;

import org.lunapark.dev.war4peace1.objects.Character;
import org.lunapark.dev.war4peace1.objects.CharacterData;

/**
 * Created by znak on 29.03.2017.
 */

public class ArtificalIntelligence {

    private CharacterData data;
    private WorldManager worldManager;

    public ArtificalIntelligence(WorldManager worldManager) {
        this.worldManager = worldManager;
        data = new CharacterData();
    }

    public CharacterData getData(Character character, float playerX, float playerZ) {
        float x = character.getBase().getPosX();
        float z = character.getBase().getPosZ();
        int dx = 0;
        int dz = 0;
        if (worldManager.getDistance2d(x, z, playerX, playerZ) < 10) {
            data.canShoot = true;
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

        } else {
            data.canShoot = false;
        }
        data.deltaX = dx;
        data.deltaZ = dz;
        return data;
    }
}
