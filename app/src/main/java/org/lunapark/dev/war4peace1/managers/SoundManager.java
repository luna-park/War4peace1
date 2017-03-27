package org.lunapark.dev.war4peace1.managers;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import org.lunapark.dev.war4peace1.R;

/**
 * Sound manager
 * Created by znak on 27.03.2017.
 */

public class SoundManager {

    private SoundPool soundPool;
    private Context context;
    public static int sfxShot, sfxImpact, sfxStep;

    public SoundManager(Context context) {
        this.context = context;
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {

            }
        });
        loadSounds();
    }

    private void loadSounds() {
        sfxShot = soundPool.load(context, R.raw.sfx_shot, 1);
        sfxImpact = soundPool.load(context, R.raw.sfx_impact, 1);
        sfxStep = soundPool.load(context, R.raw.sfx_step, 1);
    }

    public void playSoundMono(int id) {
        soundPool.play(id, 0.5f, 0.5f, 1, 0, 1);
    }

    public void dispose() {
        soundPool.release();
    }
}
