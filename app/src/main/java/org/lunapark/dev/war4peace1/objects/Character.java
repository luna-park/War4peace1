package org.lunapark.dev.war4peace1.objects;

import org.lunapark.dev.war4peace1.R;
import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.SoundManager;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;

import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_RATE;
import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_SOURCE_ANGLE;
import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_SOURCE_RANGE;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_LEGS;

/**
 * Basic character
 * Created by znak on 27.03.2017.
 */

public class Character {
    private Object3D base, legLeft, legRight, body, bulletSpawn;
    private float legsAngle1, legsAngle2, legsMult = 1;
    private SoundManager soundManager;
    private ObjectManager objectManager;

    private long fireTime;


    public Character(ObjectManager objectManager, SoundManager soundManager) {
        this.objectManager = objectManager;
        this.soundManager = soundManager;
    }

    public void define(Texture txBody, Texture txLeg, Texture txFire, float length, float width, float h) {
        legLeft = objectManager.createObject(R.raw.plane_l, txLeg);
        legLeft.setScale(h, 1, 0.25f);
        legRight = objectManager.createObject(R.raw.plane_r, txLeg);
        legRight.setScale(h, 1, 0.25f);
        base = objectManager.createObject(R.raw.plane, txLeg);
        base.setScale(0.1f, 0.1f, 0.1f);
        base.setPos(0, h, 0);
        body = objectManager.createObject(R.raw.plane, txBody);
        body.setScale(length, 1, width);

        bulletSpawn = objectManager.createObject(R.raw.plane, txFire);
        bulletSpawn.setScale(0.2f, 0.2f, 0.2f);
        bulletSpawn.setVisible(false);
    }

    public void update(float deltaX, float deltaZ, float x, float y, float z, boolean animateLegs) {
        if (animateLegs) {
            if (deltaX != 0 || deltaZ != 0) {
                if ((legsAngle1 >= 140) || (legsAngle1 <= 40)) {
                    legsMult = -legsMult;
                    soundManager.playSoundMono(SoundManager.sfxStep);
                }
                float legsDelta = SPEED_LEGS * legsMult;
                legsAngle1 += legsDelta;
                legsAngle2 -= legsDelta;
            } else {
                legsAngle1 = 90;
                legsAngle2 = 90;
            }
        } else {
            legsAngle1 = 90;
            legsAngle2 = 90;
        }

        float playerRotY = base.getRotY();
        base.setPos(x, y, z);
        body.setPos(x, y, z);
        body.setRotation(0, playerRotY, 0);
        body.addRotY((90 - legsAngle1) / 10);

        float bodyRotY = body.getRotY();

        legLeft.setPos(x, y, z);
        legRight.setPos(x, y, z);
        legLeft.setRotation(0, playerRotY, legsAngle1);
        legRight.setRotation(0, playerRotY, legsAngle2);

        // update playerBulletSpawn
        float phi = FIRE_SOURCE_ANGLE + bodyRotY;
        float fireSourceX = x - (float) (FIRE_SOURCE_RANGE * Math.cos(Math.toRadians(phi)));
        float fireSourceZ = z + (float) (FIRE_SOURCE_RANGE * Math.sin(Math.toRadians(phi)));
        bulletSpawn.setPos(fireSourceX, y, fireSourceZ);
        bulletSpawn.setRotation(0, bodyRotY + 45, 0);

        long currentTime = System.currentTimeMillis();
        if ((currentTime - fireTime > FIRE_RATE) && bulletSpawn.isVisible()) {
            fireTime = currentTime;
            bulletSpawn.setVisible(false);
        }
    }

    public Object3D getBase() {
        return base;
    }

    public Object3D getBulletSpawn() {
        return bulletSpawn;
    }

    public void fire() {
        soundManager.playSoundMono(SoundManager.sfxShot);
        bulletSpawn.setVisible(true);
    }

    public void setRotY(float angle) {
        base.setRotation(0, angle, 0);
    }
}
