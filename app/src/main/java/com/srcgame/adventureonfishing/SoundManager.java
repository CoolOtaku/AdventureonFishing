package com.srcgame.adventureonfishing;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    private final Context context;

    private final SoundPool soundPool;
    private final HashMap<Integer, Integer> soundMap;

    public SoundManager(Context context) {
        this.context = context;
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(10)
                .setAudioAttributes(audioAttributes)
                .build();

        soundMap = new HashMap<>();
    }

    public void loadSound(int resId) {
        int soundId = soundPool.load(context, resId, 1);
        soundMap.put(resId, soundId);
    }

    public void playSound(int resId) {
        Integer soundId = soundMap.get(resId);
        if (soundId != null) {
            soundPool.play(soundId, 1, 1, 1, 0, 1f);
        }
    }

    public void release() {
        soundPool.release();
    }
}