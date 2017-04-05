package org.lunapark.dev.war4peace1.managers;

import android.content.Context;

import org.lunapark.dev.war4peace1.R;
import org.lunapark.dev.war4peace1.objects.Body2d;

import java.util.ArrayList;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;

import static org.lunapark.dev.war4peace1.utils.Consts.CHARACTER_HEIGHT;
import static org.lunapark.dev.war4peace1.utils.Consts.CHARACTER_WIDTH;
import static org.lunapark.dev.war4peace1.utils.Consts.FLOOR_DISTANCE;
import static org.lunapark.dev.war4peace1.utils.Consts.FLOOR_FAR_DISTANCE;
import static org.lunapark.dev.war4peace1.utils.Consts.FLOOR_SEGMENTS;
import static org.lunapark.dev.war4peace1.utils.Consts.FLOOR_SIZE;

/**
 * Environment manager
 * Created by znak on 26.03.2017.
 */

public class WorldManager {

    private final ArrayList<Object3D> floor;
    private final ArrayList<Body2d> solids;
    private final TextureManager textureManager;
    private final ObjectManager objectManager;
    private final Context context;

    public WorldManager(Context context, TextureManager textureManager, ObjectManager objectManager) {
        this.textureManager = textureManager;
        this.objectManager = objectManager;
        this.context = context;
        floor = new ArrayList<>();
        solids = new ArrayList<>();
    }

    public void defineLevelFloor() {
        Texture txFloor = textureManager.createTexture(context, R.drawable.dark_ground);
        for (int i = 0; i < FLOOR_SEGMENTS; i++) {
            for (int j = 0; j < FLOOR_SEGMENTS; j++) {
                Object3D object3D = objectManager.createObject(R.raw.plane, txFloor);
                object3D.setScale(FLOOR_SIZE, 1, FLOOR_SIZE);
                object3D.setPos(i * FLOOR_SIZE, 0, j * FLOOR_SIZE);
                floor.add(object3D);
            }
        }
    }

    public void defineLevelWalls() {
        Texture txWall = textureManager.createTexture(context, R.drawable.bricks);
        for (int i = 0; i < 10; i++) {
            for (int j = 0; j < 10; j++) {
                Object3D wall = objectManager.createObject(R.raw.cube, txWall);
                float x = i * 8 + 4;
                float z = j * 4 + 12;
                float w = 2;
                float h = 2;
                wall.setScale(w, 2, h);
                wall.setPos(x, wall.getScaleY() / 2, z);
                Body2d body2d = new Body2d();
                body2d.x = x;
                body2d.z = z;
                body2d.width = w;
                body2d.height = h;
                solids.add(body2d);
            }
        }
    }

    public void updateFloor(float playerX, float playerZ) {
        for (int i = 0; i < floor.size(); i++) {
            Object3D object3D = floor.get(i);
            float ox = object3D.getPosX();
            float oy = object3D.getPosY();
            float oz = object3D.getPosZ();
            float distanceX = playerX - ox;
            float distanceZ = playerZ - oz;

            if (distanceX > FLOOR_FAR_DISTANCE) {
                object3D.setPos(ox + FLOOR_DISTANCE, oy, oz);
            }

            if (distanceX < -FLOOR_FAR_DISTANCE) {
                object3D.setPos(ox - FLOOR_DISTANCE, oy, oz);
            }

            if (distanceZ > FLOOR_FAR_DISTANCE) {
                object3D.setPos(ox, oy, oz + FLOOR_DISTANCE);
            }

            if (distanceZ < -FLOOR_FAR_DISTANCE) {
                object3D.setPos(ox, oy, oz - FLOOR_DISTANCE);
            }
        }
    }

    public float getDistance2d(float x1, float y1, float x2, float y2) {
        float mDX = x2 - x1;
        float mDY = y2 - y1;

        if (mDX == 0.0F && mDY == 0.0F) {
            return 0;
        } else {
            return (float) Math.sqrt(mDX * mDX + mDY * mDY);
        }
    }

    public float getDistance3d(float x1, float y1, float z1, float x2, float y2, float z2) {
        float mDX = x2 - x1;
        float mDY = y2 - y1;
        float mDZ = z2 - z1;

        if (mDX == 0.0F && mDY == 0.0F && mDZ == 0) {
            return 0;
        } else {
            return (float) Math.sqrt(mDX * mDX + mDY * mDY + mDZ * mDZ);
        }
    }


    public boolean checkWallIntersect(Body2d body2d) {

        for (int i = 0; i < solids.size(); i++) {
            Body2d wall = solids.get(i);
            if (intersect(wall, body2d)) {
                return true;
            }
        }
        return false;
    }

    public boolean checkWallwithCharIntersect(float pX, float pZ) {

        for (int i = 0; i < solids.size(); i++) {
            Body2d wall = solids.get(i);
            if (intersectCharacter(wall, pX, pZ)) {
                return true;
            }
        }
        return false;
    }


    public boolean intersect(Body2d o1, Body2d o2) {
        float dx = Math.abs(o1.x - o2.x);
        float dz = Math.abs(o1.z - o2.z);
        float a = o1.width;
        float b = o1.height;
        float c = o2.width;
        float d = o2.height;

        float dMax = (a + c) / 2;
        float eMax = (b + d) / 2;

        return (dx < dMax) && (dz < eMax);
    }

    public boolean intersectCharacter(Body2d o1, float characterX, float characterZ) {
        float dx = Math.abs(o1.x - characterX);
        float dz = Math.abs(o1.z - characterZ);
        float a = o1.width;
        float b = o1.height;

        float dMax = (a + CHARACTER_WIDTH) / 2;
        float eMax = (b + CHARACTER_HEIGHT) / 2;
        return (dx < dMax) && (dz < eMax);
    }
}
