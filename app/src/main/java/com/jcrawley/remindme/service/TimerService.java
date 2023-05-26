package com.jcrawley.remindme.service;

import static com.jcrawley.remindme.view.NotificationHelper.NOTIFICATION_ID;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.remindme.view.MainView;
import com.jcrawley.remindme.preferences.Settings;
import com.jcrawley.remindme.preferences.TimerPreferences;

public class TimerService extends Service {

    private final IBinder binder = new LocalBinder();
    private CountdownTimer countdownTimer;
    private TimerPreferences timerPreferences;
    private WakeLockHelper wakeLockHelper;


    public TimerService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        wakeLockHelper = new WakeLockHelper(this);
        countdownTimer = new CountdownTimer(getApplicationContext(), wakeLockHelper);
        setupPreferences();
        startForeground(NOTIFICATION_ID, countdownTimer.getInitialNotification());
    }



    public void savePreferences(int minutes, int seconds, String message){
        timerPreferences.saveSettings(seconds, minutes, message);
        countdownTimer.setTime(minutes, seconds, message);
    }

    public void savePreferences(Settings settings){
        timerPreferences.saveSettings(settings);
        countdownTimer.setTime(settings.getMinutes(), settings.getSeconds(), settings.getTimesUpMessage());
    }


    public void setView(MainView view){
        countdownTimer.setAndUpdateView(view);
    }


    public void resetTime(){
        countdownTimer.resetTime();
    }


    public void startStop(){
        countdownTimer.startStop();
    }



    public class LocalBinder extends Binder {
        public TimerService getService() {
            return TimerService.this;
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }


    /*
        Service.START_STICKY - service is restarted if terminated, intent passed in has null value
        Service.START_NOT_STICKY - service is not restarted
        Service.START_REDELIVER_INTENT - service is restarted if terminated, original intent is passed in
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return Service.START_NOT_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLockHelper.destroy();
    }


    private void setupPreferences(){
        timerPreferences = new TimerPreferences(this);
        Settings settings = timerPreferences.getSettings();
        countdownTimer.setTime(settings.getMinutes(), settings.getSeconds(), settings.getTimesUpMessage());
    }



}

