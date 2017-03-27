package org.lunapark.dev.war4peace1.managers;

import android.content.Context;
import android.graphics.Bitmap;

import java.util.ArrayList;

import fr.arnaudguyon.smartgl.opengl.Texture;

/**
 * Texture manager
 * Created by znak on 21.03.2017.
 */

public class TextureManager {

    private ArrayList<Texture> textures = new ArrayList<>();

    public Texture createTexture(int colorBg) {
        int tx = 1;
        Bitmap bitmap = Bitmap.createBitmap(tx + 1, tx + 1, Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(colorBg); // Закрашиваем цветом
        Texture texture = new Texture(tx, tx, bitmap);
        textures.add(texture);
        return texture;
    }

    public Texture createTexture(Context context, int resId) {
        Texture texture = new Texture(context, resId);

        textures.add(texture);
        return texture;
    }

    public void dispose() {
        if (textures != null) {
            for (Texture t : textures) {
                if (t != null) t.release();
            }
            textures.clear();
            textures = null;
        }
    }
}
