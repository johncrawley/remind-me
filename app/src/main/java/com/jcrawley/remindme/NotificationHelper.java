package com.jcrawley.remindme;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.jcrawley.remindme.service.TimerService;

public class NotificationHelper {

    private final Context context;
    private final TimerService timerService;
    final static int NOTIFICATION_ID = 1001;
    private PendingIntent pendingIntent;
    final static String NOTIFICATION_CHANNEL_ID = "com.jcrawley.musicplayer-notification";


    public NotificationHelper(Context context, TimerService timerService) {
        this.context = context;
        this.timerService = timerService;
    }


    public void init(){
        setupNotificationChannel();
        setupNotificationClickForActivity();
    }

    private void setupNotificationChannel(){
        String channelName = "j-crawley-remind-me-notification-channel";
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName,  NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        channel.setShowBadge(false);
        notificationManager.createNotificationChannel(channel);
    }


    void setupNotificationClickForActivity(){
        Intent resultIntent = new Intent(context, MainActivity.class);
        resultIntent.setAction(Intent.ACTION_MAIN);
        resultIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_IMMUTABLE);
    }


    public Notification createNotification(String heading, String channelName) {

        final NotificationCompat.Builder notification = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setContentTitle(heading)
                .setContentText(channelName)
                .setSilent(true)
                .setBadgeIconType(NotificationCompat.BADGE_ICON_SMALL)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setNumber(-1)
                .setOnlyAlertOnce(true)
                .setContentIntent(pendingIntent)
                .setShowWhen(false)
                .setOngoing(true);
        return notification.build();
    }
}