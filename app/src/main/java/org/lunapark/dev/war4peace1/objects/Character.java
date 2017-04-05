package org.lunapark.dev.war4peace1.objects;

import org.lunapark.dev.war4peace1.R;
import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.SoundManager;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;

import static org.lunapark.dev.war4peace1.utils.Consts.FIRE_RATE;
import static org.lunapark.dev.war4peace1.utils.Consts.SPEED_LEGS;

/**
 * Basic character
 * Created by znak on 27.03.2017.
 */

public class Character {
    private Object3D base, legLeft, legRight, body, bulletSpawn, deadbody;
    private float legsAngle1, legsAngle2, legsMult = 1, playerRotY = 0;
    private SoundManager soundManager;
    private ObjectManager objectManager;

    private long fireTime;
    private int health;
    private int dx, dz;

    // Default bullet spawn
    private float bulletSpawnX = 1.4f;
    private float bulletSpawnZ = 0.26f;
    private float bulletSpawnRange = (float) Math.sqrt(bulletSpawnX * bulletSpawnX + bulletSpawnZ * bulletSpawnZ);
    private float bulletSpawnAngle = (float) Math.toDegrees(Math.atan(bulletSpawnZ / bulletSpawnX));

    public Character(ObjectManager objectManager, SoundManager soundManager) {
        this.objectManager = objectManager;
        this.soundManager = soundManager;
        health = 100;
    }

    public void define(Texture txBody, Texture txLeg, Texture txFire, float bodyScaleX, float height) {
        legLeft = objectManager.createObject(R.raw.plane_l, txLeg);
        legLeft.setScale(height, 1, 0.25f);
        legRight = objectManager.createObject(R.raw.plane_r, txLeg);
        legRight.setScale(height, 1, 0.25f);
        base = objectManager.createObject(R.raw.plane, txLeg);
        base.setScale(0.1f, 0.1f, 0.1f);
        base.setPos(0, height, 0);
        body = objectManager.createObject(R.raw.plane, txBody);
        body.setScale(bodyScaleX, 1, 1);

        bulletSpawn = objectManager.createObject(R.raw.plane, txFire);
        bulletSpawn.setScale(0.2f, 0.2f, 0.2f);
        bulletSpawn.setVisible(false);
    }

    public void defineDeadBody(Texture txDead, float deadScaleX) {
        deadbody = objectManager.createObject(R.raw.plane, txDead);
        deadbody.setScale(deadScaleX, 1, 1);
        deadbody.setVisible(false);
    }

    public void setBulletSpawn(float bulletSpawnX, float bulletSpawnZ) {
        bulletSpawnRange = (float) Math.sqrt(bulletSpawnX * bulletSpawnX + bulletSpawnZ * bulletSpawnZ);
        bulletSpawnAngle = (float) Math.toDegrees(Math.atan(bulletSpawnZ / bulletSpawnX));
    }

    public void update(float delta, int deltaX, int deltaZ, float x, float y, float z, boolean animateLegs) {
        dx = deltaX;
        dz = deltaZ;

        if (health > 0) {
            if (animateLegs) {
                if (deltaX != 0 || deltaZ != 0) {
                    if ((legsAngle1 >= 140) || (legsAngle1 <= 40)) {
                        legsMult = -legsMult;
//                        soundManager.playSoundMono(SoundManager.sfxStep);
                    }
                    float legsDelta = SPEED_LEGS * legsMult * delta;
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


            if (deltaX == 0) {
                if (deltaZ == 1) {
                    playerRotY = 90;
                }
                if (deltaZ == -1) {
                    playerRotY = -90;
                }
            }

            if (deltaZ == 0) {
                if (deltaX == 1) {
                    playerRotY = 180;
                }
                if (deltaX == -1) {
                    playerRotY = 0;
                }
            }

//        float playerRotY = base.getRotY();
            base.setRotation(0, playerRotY, 0);
            base.setPos(x, y, z);
            deadbody.setPos(x, 0, z);
            deadbody.setRotation(0, playerRotY, 0);
            body.setPos(x, y, z);
            body.setRotation(0, playerRotY, 0);
            body.addRotY((90 - legsAngle1) / 10);

            float bodyRotY = body.getRotY();

            legLeft.setPos(x, y, z);
            legRight.setPos(x, y, z);
            legLeft.setRotation(0, playerRotY, legsAngle1);
            legRight.setRotation(0, playerRotY, legsAngle2);

            // update playerBulletSpawn
            float phi = bulletSpawnAngle + bodyRotY;
            float fireSourceX = x - (float) (bulletSpawnRange * Math.cos(Math.toRadians(phi)));
            float fireSourceZ = z + (float) (bulletSpawnRange * Math.sin(Math.toRadians(phi)));
            bulletSpawn.setPos(fireSourceX, y, fireSourceZ);
            bulletSpawn.setRotation(0, bodyRotY + 45, 0);

            long currentTime = System.currentTimeMillis();
            if ((currentTime - fireTime > FIRE_RATE) && bulletSpawn.isVisible()) {
                fireTime = currentTime;
                bulletSpawn.setVisible(false);
            }
        } else {
            deadbody.setVisible(true);
            base.setVisible(false);
            legLeft.setVisible(false);
            legRight.setVisible(false);
            body.setVisible(false);
            bulletSpawn.setVisible(false);
        }
    }

    public Object3D getBase() {
        return base;
    }

    public Object3D getBulletSpawn() {
        return bulletSpawn;
    }

    public boolean fire() {
        if (!bulletSpawn.isVisible()) {
            soundManager.playSoundMono(SoundManager.sfxShot);
            bulletSpawn.setVisible(true);
            return true;
        } else {
            return false;
        }
    }

    public void damage() {
        if (health > 0) health -= 80;
    }

    public int getHealth() {
        return health;
    }

    public int getDx() {
        return dx;
    }

    public int getDz() {
        return dz;
    }
}
