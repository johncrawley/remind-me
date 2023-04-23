package com.jcrawley.remindme.service;

import static com.jcrawley.remindme.NotificationHelper.NOTIFICATION_ID;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.IBinder;

import com.jcrawley.remindme.CountdownTimer;
import com.jcrawley.remindme.MainActivity;
import com.jcrawley.remindme.MainView;
import com.jcrawley.remindme.NotificationHelper;
import com.jcrawley.remindme.R;

public class TimerService extends Service {

    private final IBinder binder = new LocalBinder();
    private CountdownTimer countdownTimer;
    private NotificationHelper notificationHelper;


    public TimerService() {
    }


    @Override
    public void onCreate() {
        super.onCreate();
        notificationHelper = new NotificationHelper(this);
        countdownTimer = new CountdownTimer(getApplicationContext(), 10);
        countdownTimer.setNotificationHelper(notificationHelper);
        moveToForeground();
    }


    private void moveToForeground(){
        notificationHelper.init();
        String initialMessage = getString(R.string.notification_initial_message);
        Notification notification = createNotificationForCurrentTime(initialMessage);
        startForeground(NOTIFICATION_ID, notification);
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

