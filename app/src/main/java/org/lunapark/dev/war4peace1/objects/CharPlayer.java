package org.lunapark.dev.war4peace1.objects;

import android.content.Context;
import android.graphics.Color;

import org.lunapark.dev.war4peace1.R;
import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.SoundManager;
import org.lunapark.dev.war4peace1.managers.TextureManager;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;

import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_SOURCE_ANGLE;
import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_SOURCE_RANGE;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_LEGS;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_PLAYER;

/**
 * Created by znak on 27.03.2017.
 */

public class CharPlayer {

    private Object3D player, legLeft, legRight, body;
    private float legsAngle1, legsAngle2, legsMult = 1;

    private TextureManager textureManager;
    private ObjectManager objectManager;
    private SoundManager soundManager;
//    private Context context;

    public CharPlayer(TextureManager textureManager, ObjectManager objectManager, SoundManager soundManager) {
        this.textureManager = textureManager;
        this.objectManager = objectManager;
        this.soundManager = soundManager;
//        this.context = context;
    }

    public void definePlayer(Texture txBody, float scaleBodyX, float scaleBodyZ,
                             Texture txLeg, float scaleLegX, float scaleLegZ) {
//        Texture txLegs = textureManager.createTexture(context, R.drawable.camo);
        legLeft = objectManager.createObject(R.raw.plane_l, txLeg);
        legLeft.setScale(scaleLegX, 1, scaleLegZ);

        legRight = objectManager.createObject(R.raw.plane_r, txLeg);
        legRight.setScale(scaleLegX, 1, scaleLegZ);

        Texture txPlayer = textureManager.createTexture(Color.TRANSPARENT);
        player = objectManager.createObject(R.raw.plane, txPlayer);
        player.setPos(0, scaleLegX, 0);

//        Texture txBody = textureManager.createTexture(context, R.drawable.survivor);
        body = objectManager.createObject(R.raw.plane, txBody);
        body.setScale(scaleBodyX, 1, scaleBodyZ);
    }

    public void updatePlayer(float delta, float dx, float dz) {
//        float playerX = player.getPosX();
//        float playerY = player.getPosY();
//        float playerZ = player.getPosZ();
//
//        float newX = playerX + dx * delta * SPEED_PLAYER;
//        float newZ = playerZ + dz * delta * SPEED_PLAYER;
//
//        if (!checkWallPlayerIntersect(newX, newZ)) {
//            playerX = newX;
//            playerZ = newZ;
//            if (dx != 0 || dz != 0) {
//                if ((legsAngle1 >= 140) || (legsAngle1 <= 40)) {
//                    legsMult = -legsMult;
//                    soundManager.playSoundMono(SoundManager.sfxStep);
//                }
//                float legsDelta = SPEED_LEGS * legsMult;
//                legsAngle1 += legsDelta;
//                legsAngle2 -= legsDelta;
//            } else {
//                legsAngle1 = 90;
//                legsAngle2 = 90;
//            }
//        } else {
//            legsAngle1 = 90;
//            legsAngle2 = 90;
//        }
//
//        float playerRotY = player.getRotY();
//        player.setPos(playerX, playerY, playerZ);
//        body.setPos(playerX, playerY, playerZ);
//        body.setRotation(0, playerRotY, 0);
//        body.addRotY((90 - legsAngle1) / 10);
//
//        bodyRotY = body.getRotY();
//
//        legLeft.setPos(playerX, playerY, playerZ);
//        legRight.setPos(playerX, playerY, playerZ);
//        legLeft.setRotation(0, playerRotY, legsAngle1);
//        legRight.setRotation(0, playerRotY, legsAngle2);
//
//        // update bulletSpawn
//        float phi = FIRE_SOURCE_ANGLE + bodyRotY;
//        float fireSourceX = playerX - (float) (FIRE_SOURCE_RANGE * Math.cos(Math.toRadians(phi)));
//        float fireSourceZ = playerZ + (float) (FIRE_SOURCE_RANGE * Math.sin(Math.toRadians(phi)));
//        bulletSpawn.setPos(fireSourceX, playerY, fireSourceZ);
//        bulletSpawn.setRotation(0, bodyRotY + 45, 0);
    }

    public Object3D getPlayer() {
        return player;
    }

    public Object3D getBody() {
        return body;
    }
}
