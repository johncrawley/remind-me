package com.jcrawley.remindme;

import static com.jcrawley.remindme.TimesUpNotifier.CHANNEL_ID;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;

import androidx.core.app.NotificationCompat;


public class NotificationHelper {

    private final Context context;
    public final static int NOTIFICATION_ID = 1001;
    private PendingIntent pendingIntent;
    final static String NOTIFICATION_CHANNEL_ID = "com.jcrawley.musicplayer-notification";
    private final String CHANNEL_NAME;
    private NotificationManager notificationManager;


    public NotificationHelper(Context context, String channelName) {
        this.context = context;
        CHANNEL_NAME = channelName;
    }


    public void init(){
        setupNotificationChannel();
        setupNotificationClickForActivity();
    }


    private void setupNotificationChannel(){
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "j-crawley-remind-me-notification-channel",  NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }




    public void setChannelOptionsForTimesUp() {
        NotificationChannel notificationChannel = notificationManager.getNotificationChannel(NOTIFICATION_CHANNEL_ID);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(Color.GREEN);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
    }



    void setupNotificationClickForActivity(){
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
    }


    public Notification createNotification(String status, String timeStr) {

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
        return notification.build();
    }


    public void updateNotification(String status, String timeStr) {
        notificationManager.notify(NOTIFICATION_ID, createNotification(status, timeStr));
    }


    public void createTimesUpNotification(String message){
/*
        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(heading)
                .setContentText(channelName)
                .setSilent(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.baseline_alarm_24)
                .setNumber(-1)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setOngoing(true);
        return notification.build();

 */
    }
}