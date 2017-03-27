package org.lunapark.dev.war4peace1.managers;

import android.content.Context;

import fr.arnaudguyon.smartgl.opengl.Object3D;
import fr.arnaudguyon.smartgl.opengl.RenderPassObject3D;
import fr.arnaudguyon.smartgl.opengl.Texture;
import fr.arnaudguyon.smartgl.tools.WavefrontModel;

/**
 * Object3D manager
 * Created by znak on 21.03.2017.
 */

public class ObjectManager {

    private final Context context;
    private final RenderPassObject3D renderPassObject3D;

    public ObjectManager(Context context, RenderPassObject3D renderPassObject3D) {
        this.context = context;
        this.renderPassObject3D = renderPassObject3D;
    }

    public Object3D createObject(int objFile, Texture texture) {
        WavefrontModel model = new WavefrontModel.Builder(context, objFile)
                .addTexture("", texture)
                .create();
        Object3D object3D = model.toObject3D();
        renderPassObject3D.addObject(object3D);
        return object3D;
    }


    public Object3D createObject(int objFile, Texture texture1, String textureName1) {
        WavefrontModel model = new WavefrontModel.Builder(context, objFile)
                .addTexture(textureName1, texture1)
                .create();
        Object3D object3D = model.toObject3D();
        renderPassObject3D.addObject(object3D);
        return object3D;
    }

    public Object3D createObject(int objFile, Texture texture1, String textureName1,
                                 Texture texture2, String textureName2) {
        WavefrontModel model = new WavefrontModel.Builder(context, objFile)
                .addTexture(textureName1, texture1)
                .addTexture(textureName2, texture2)
                .create();
        Object3D object3D = model.toObject3D();
        renderPassObject3D.addObject(object3D);
        return object3D;
    }

    public Object3D createObject(int objFile, Texture texture1, String textureName1,
                                 Texture texture2, String textureName2, Texture texture3, String textureName3) {
        WavefrontModel model = new WavefrontModel.Builder(context, objFile)
                .addTexture(textureName1, texture1)
                .addTexture(textureName2, texture2)
                .addTexture(textureName3, texture3)
                .create();
        Object3D object3D = model.toObject3D();
        renderPassObject3D.addObject(object3D);
        return object3D;
    }

}
