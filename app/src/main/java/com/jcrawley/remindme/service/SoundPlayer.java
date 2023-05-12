package com.jcrawley.remindme.service;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import com.jcrawley.remindme.R;


public class SoundPlayer {

    private SoundPool soundPool;
    private int currentSoundId;

    public SoundPlayer(Context context){
        setupSoundPool();
        loadSound(context);
    }

    public void playSound(){
        soundPool.play(currentSoundId, 1f, 1f, 1, 1, 1f);
    }


    private void setupSoundPool(){
        AudioAttributes attributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ALARM)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        soundPool = new SoundPool.Builder()
                .setMaxStreams(1)
                .setAudioAttributes(attributes)
                .build();
    }


    private void loadSound(Context context){
        currentSoundId = soundPool.load(context,R.raw.alert_1, 1);
    }
}
