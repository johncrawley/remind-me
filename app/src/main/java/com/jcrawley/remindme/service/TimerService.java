package com.jcrawley.remindme.service;

import static com.jcrawley.remindme.NotificationHelper.NOTIFICATION_ID;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.MainView;
import com.jcrawley.remindme.NotificationHelper;
import com.jcrawley.remindme.Settings;
import com.jcrawley.remindme.TimerPreferences;
import com.jcrawley.remindme.R;

public class TimerService extends Service {

    private final IBinder binder = new LocalBinder();
    private CountdownTimer countdownTimer;
    private NotificationHelper notificationHelper;
    private TimerPreferences timerPreferences;
    private String currentTimesUpMessage;


    public TimerService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
        timerPreferences = new TimerPreferences(this);
        countdownTimer = new CountdownTimer(getApplicationContext());
        setupPreferences();
        countdownTimer.setNotificationHelper(notificationHelper);
        moveToForeground();
    }


    private void moveToForeground(){
        notificationHelper.init(this);
        String initialMessage = getString(R.string.notification_initial_message);
        Notification notification = createNotificationForCurrentTime(initialMessage);
        startForeground(NOTIFICATION_ID, notification);
    }


    private void setupPreferences(){
        timerPreferences = new TimerPreferences(this);
        Settings settings = timerPreferences.getSettings();
        currentTimesUpMessage = settings.getTimesUpMessage();
        countdownTimer.setTime(settings.getMinutes(), settings.getSeconds());
    }


    public void savePreferences(int minutes, int seconds, String message){
        timerPreferences.saveSettings(seconds, minutes, message);
        countdownTimer.setTime(minutes, seconds);
        currentTimesUpMessage = message;
    }


    public String getTimesUpMessage(){
        return currentTimesUpMessage;
    }


    private Notification createNotificationForCurrentTime(String status){
        return notificationHelper.createNotification(status, countdownTimer.getCurrentTime());
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


    public void setView(MainView view){
        countdownTimer.setAndUpdateView(view, this);
    }


    public void resetTime(){
        countdownTimer.resetTime();
    }


    public void startStop(){
        countdownTimer.startStop();
    }


    public void setTime(String minutesStr, String secondsStr){
        if (countdownTimer.isInitialized()) {
            return;
        }
        int minutes = getIntFor(minutesStr);
        int seconds = getIntFor(secondsStr);
        countdownTimer.setTime(minutes, seconds);
    }


    private int getIntFor(String str){
        return str.isEmpty() ? 0 : Integer.parseInt(str);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }





}

