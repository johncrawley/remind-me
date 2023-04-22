package com.jcrawley.remindme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import androidx.core.app.NotificationCompat;


public class NotificationHelper {

    private final Context context;
    public final static int NOTIFICATION_ID = 1001;
    private PendingIntent pendingIntent;
    final static String NOTIFICATION_CHANNEL_ID = "com.jcrawley.musicplayer-notification";
    private NotificationManager notificationManager;


    public NotificationHelper(Context context) {
        this.context = context;
    }


    public void init(){
        setupNotificationChannel();
        setupNotificationClickForActivity();
    }
    NotificationChannel channel;

    private void setupNotificationChannel(){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "j-crawley-remind-me-notification-channel",  NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        channel.enableVibration(true);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }


    void setupNotificationClickForActivity(){
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
    }


    private Notification buildNotification(String status, String timeStr, boolean isTimeUp){
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(status)
                .setContentText(timeStr)
                .setSilent(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.baseline_alarm_24)
                .setNumber(-1)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setOngoing(true);
        if(isTimeUp){
            Uri alarmSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            notification.setSound(alarmSoundUri)
                    .setSilent(false)
                    .setVibrate(new long[]{1,0,0,0,1,1});
        }
        return notification.build();
    }


    public Notification createNotification(String status, String timeStr) {
        return buildNotification(status, timeStr, false);
    }


    public void updateNotification(String status, String timeStr) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(status, timeStr));
    }


    public void sendTimesUpNotification(String message){
       // setChannelOptionsForTimesUp();
        notificationManager.notify(NOTIFICATION_ID, buildNotification("Times up", message, true));
    }


}