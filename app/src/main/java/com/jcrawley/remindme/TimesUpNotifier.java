package com.jcrawley.remindme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;


public class TimesUpNotifier {

    private final Context context;
    private final MainViewModel viewModel;
    private Notification.Builder notificationBuilder;
    private NotificationManager notificationManager;
    public static final String CHANNEL_ID = "com.jcrawley.ANDROID";
    public static final String CHANNEL_NAME = "TIMES UP CHANNEL";


    public TimesUpNotifier(Context context, MainViewModel viewModel){
        this.context = context;
        this.viewModel = viewModel;
        createChannels();
        createNotification();
    }


    public void createNotification(){
        String title = getStr(R.string.notification_title);
        String enteredMessage = viewModel.reminderMessage.trim();
        String message = enteredMessage.isEmpty() ? getStr(R.string.notification_default_message) : enteredMessage;
        notificationBuilder = getAndroidChannelNotification(title, message);
        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE);
        notificationBuilder.setContentIntent(contentIntent);
    }


    private String getStr(int resId){
        return context.getResources().getString(resId);
    }


    public void createChannels() {
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

    private final int NOTIFICATION_ID = 101;


    public void issueNotification(){
        notificationBuilder.setWhen(System.currentTimeMillis());
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build());
    }


    public void dismissNotification(){
        notificationManager.cancel(NOTIFICATION_ID);
    }

}
