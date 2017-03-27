package org.lunapark.dev.war4peace1.objects;

import android.content.Context;
import android.graphics.Color;

import org.lunapark.dev.war4peace1.R;
import org.lunapark.dev.war4peace1.managers.ObjectManager;
import org.lunapark.dev.war4peace1.managers.TextureManager;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.Texture;

/**
 * Created by znak on 27.03.2017.
 */

public class Character {
    private Object3D base, legLeft, legRight, body;
//    private TextureManager textureManager;
//    private ObjectManager objectManager;


    public Character(Context context, ObjectManager objectManager, TextureManager textureManager) {
        Texture txLegs = textureManager.createTexture(context, R.drawable.camo);
        legLeft = objectManager.createObject(R.raw.plane_l, txLegs);
        legLeft.setScale(1.3f, 1, 0.25f);

        legRight = objectManager.createObject(R.raw.plane_r, txLegs);
        legRight.setScale(1.3f, 1, 0.25f);

        Texture txPlayer = textureManager.createTexture(Color.TRANSPARENT);
        base = objectManager.createObject(R.raw.plane, txPlayer);
        base.setPos(0, 1.3f, 0);

        Texture txBody = textureManager.createTexture(context, R.drawable.survivor);
        body = objectManager.createObject(R.raw.plane, txBody);
        body.setScale(2.5f, 1, 1);
    }

    public Object3D getBase() {
        return base;
    }

    public Object3D getBody() {
        return body;
    }

    public float getBaseRotY() {
        return base.getRotY();
    }

    public float getBodyRotY() {
        return body.getRotY();
    }

}
