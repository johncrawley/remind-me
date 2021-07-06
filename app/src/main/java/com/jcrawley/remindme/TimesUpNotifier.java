package com.jcrawley.remindme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


/**
 * Created by john on 05/01/18.
 *
 * Handles the Creation of a notification
 */

public class TimesUpNotifier {

    private final Context context;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;
    public static final String CHANNEL_ID = "com.jcrawley.ANDROID";
    public static final String CHANNEL_NAME = "ANDROID CHANNEL";


    public TimesUpNotifier(Context context){
        this.context = context;
        createChannels();
        createNotification();
    }


    public void createNotification(){
        notificationBuilder = getAndroidChannelNotification("Immersion", "Immersion Reminder!");
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        notificationBuilder.setContentIntent(contentIntent);
    }


    public void createChannels() {

        // create android channel
        NotificationChannel androidChannel = new NotificationChannel(CHANNEL_ID,
                CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT);
        androidChannel.enableLights(true);
        androidChannel.enableVibration(true);
        androidChannel.setLightColor(Color.GREEN);
        androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(androidChannel);
    }


    public Notification.Builder getAndroidChannelNotification(String title, String body) {
        return new Notification.Builder(context.getApplicationContext(), CHANNEL_ID)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(android.R.drawable.stat_notify_more)
                .setAutoCancel(true);
    }


    private NotificationManager getManager() {
        if (notificationManager == null) {
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return notificationManager;
    }


    public void issueNotification(){
        notificationBuilder.setWhen(System.currentTimeMillis());
        int notificationId = 101;
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

}
