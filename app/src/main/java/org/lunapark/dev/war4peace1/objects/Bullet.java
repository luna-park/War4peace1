package org.lunapark.dev.war4peace1.objects;

import fr.arnaudguyon.smartgl.opengl.Object3D;

import static org.lunapark.dev.war4peace1.utils.Consts.BULLET_DISTANCE;
import static org.lunapark.dev.war4peace1.utils.Consts.BULLET_SPEED;

/**
 * Bullet
 * Created by znak on 25.03.2017.
 */

public class Bullet {

    private final Object3D object3D;
    private float angle, distance;
    private float bulletX, bulletY, bulletZ;
    private final Body2d body2d;

    public Bullet(Object3D object3D, Body2d body2d) {
        this.object3D = object3D;
        this.body2d = body2d;
        object3D.setVisible(false);
    }

    public void create(float x, float y, float z, float angle) {
        object3D.setVisible(true);
        object3D.setRotation(0, angle, 0);
        this.angle = (float) Math.toRadians(angle);
        bulletX = x;
        bulletY = y;
        bulletZ = z;
        distance = 0;
    }

    public void update(float delta) {
        if (object3D.isVisible()) {
            distance += delta * BULLET_SPEED;
            bulletX -= distance * Math.cos(angle);
            bulletZ += distance * Math.sin(angle);
            object3D.setPos(bulletX, bulletY, bulletZ);
        }

        if (distance > BULLET_DISTANCE) {
            object3D.setVisible(false);
            distance = 0;
        }
    }

    public boolean isVisible() {
        return object3D.isVisible();
    }

    public void hide() {
        object3D.setVisible(false);
    }

    public Body2d getBody2d() {
        body2d.x = object3D.getPosX();
        body2d.z = object3D.getPosZ();
        return body2d;
    }
}
