package com.example.loadsheddingtracker.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.activities.DashboardActivity;

// LECTURE TOPIC: Notifications & NotificationChannel
// Demonstrates creating notification channels (Android 8.0+), foreground service notifications,
// and actionable user notifications using NotificationCompat.Builder with PendingIntent (FLAG_IMMUTABLE).
public class NotificationHelper {

    public static final String CHANNEL_ID = "loadshedding_channel_id";
    public static final String CHANNEL_NAME = "Load-shedding Outage Alerts";
    public static final String CHANNEL_DESC = "Notifications for upcoming outages and power restoration";

    public static final int NOTIFICATION_ID_FOREGROUND = 1001;
    public static final int NOTIFICATION_ID_WARNING = 1002;
    public static final int NOTIFICATION_ID_RESTORED = 1003;

    /**
     * Creates the NotificationChannel required on Android 8.0 (API 26) and higher.
     */
    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESC);
            channel.enableVibration(true);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    /**
     * Builds the persistent notification for OutageCountdownService (Foreground Service).
     */
    public static Notification buildForegroundServiceNotification(Context context, String remainingText) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("Load-shedding Countdown Active")
                .setContentText("Next scheduled power cut in: " + remainingText)
                .setSmallIcon(R.drawable.ic_stat_bolt)
                .setContentIntent(pendingIntent)
                .setOngoing(true) // Marks this as a persistent foreground notification
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .build();
    }

    /**
     * Shows a "Power cut starts in 15 minutes" warning notification.
     * Clicking it opens DashboardActivity via a PendingIntent with FLAG_IMMUTABLE.
     */
    public static void showUpcomingOutageNotification(Context context, String area) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                1,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("⚠️ Power Cut Approaching in " + area)
                .setContentText("Scheduled load-shedding starts in 15 minutes. Please charge your devices now!")
                .setSmallIcon(R.drawable.ic_stat_bolt)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .build();

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_WARNING, notification);
        } catch (SecurityException se) {
            // Android 13+ requires POST_NOTIFICATIONS permission
        }
    }

    /**
     * Shows a "Power restored" notification.
     */
    public static void showPowerRestoredNotification(Context context, String area) {
        createNotificationChannel(context);

        Intent intent = new Intent(context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                2,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        Notification notification = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentTitle("⚡ Power Restored in " + area)
                .setContentText("Electricity supply has been restored to the grid.")
                .setSmallIcon(R.drawable.ic_stat_bolt)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID_RESTORED, notification);
        } catch (SecurityException se) {
            // Handled when permission is not granted
        }
    }
}

