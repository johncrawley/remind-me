package com.jcrawley.remindme.service;

import static com.jcrawley.remindme.NotificationHelper.NOTIFICATION_ID;


import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.MainView;
import com.jcrawley.remindme.Settings;
import com.jcrawley.remindme.TimerPreferences;

public class TimerService extends Service {

    private final IBinder binder = new LocalBinder();
    private CountdownTimer countdownTimer;
    private TimerPreferences timerPreferences;


    public TimerService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        countdownTimer = new CountdownTimer(getApplicationContext());
        setupPreferences();
        startForeground(NOTIFICATION_ID, countdownTimer.getInitialNotification());
    }


    public void savePreferences(int minutes, int seconds, String message){
        timerPreferences.saveSettings(seconds, minutes, message);
        countdownTimer.setTime(minutes, seconds, message);
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
    }


    private void setupPreferences(){
        timerPreferences = new TimerPreferences(this);
        Settings settings = timerPreferences.getSettings();
        countdownTimer.setTime(settings.getMinutes(), settings.getSeconds(), settings.getTimesUpMessage());
    }

}

