package com.jcrawley.remindme.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.MainView;
import com.jcrawley.remindme.NotificationHelper;

public class TimerService extends Service {

    private final IBinder binder = new LocalBinder();
    private final CountdownTimer countdownTimer;
    private final NotificationHelper notificationHelper;
    final static int NOTIFICATION_ID = 10011;


    public TimerService() {
        countdownTimer = new CountdownTimer(10);
        notificationHelper = new NotificationHelper(this, this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        moveToForeground();
    }


    private void moveToForeground(){
        notificationHelper.init();
        Notification notification = notificationHelper.createNotification("Timer Ready", "");
        startForeground(NOTIFICATION_ID, notification);
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
        countdownTimer.setAndUpdateView(view);
    }


    public void resetTime(){
        countdownTimer.resetTime();
    }


    public void startStop(){
        countdownTimer.startStop();
    }


    public void setTime(int minutes, int seconds){
        countdownTimer.setTime(minutes, seconds);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
    }





}

