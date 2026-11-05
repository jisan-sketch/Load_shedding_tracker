package com.example.loadsheddingtracker.services;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.example.loadsheddingtracker.utils.NotificationHelper;

import java.util.Locale;

// LECTURE TOPIC: Android Services & Foreground Service
// Demonstrates:
// 1. Started Service lifecycle (onCreate, onStartCommand, onDestroy)
// 2. Foreground Service with persistent notification (startForeground)
// 3. Custom Broadcast Intent (sendBroadcast) to notify DashboardActivity of remaining countdown time
public class OutageCountdownService extends Service {

    private static final String TAG = "OutageCountdownService";

    public static final String ACTION_START = "com.example.loadsheddingtracker.service.START";
    public static final String ACTION_STOP = "com.example.loadsheddingtracker.service.STOP";
    public static final String ACTION_COUNTDOWN_TICK = "com.example.loadsheddingtracker.COUNTDOWN_TICK";

    public static final String EXTRA_SECONDS_LEFT = "extra_seconds_left";
    public static final String EXTRA_FORMATTED_TIME = "extra_formatted_time";

    private Handler mHandler;
    private Runnable mCountdownRunnable;
    private int mRemainingSeconds = 45 * 60; // 45 minutes countdown demonstration
    private boolean mIsRunning = false;

    @Override
    public void onCreate() {
        super.onCreate();
        // LECTURE TOPIC: Service Lifecycle - onCreate()
        // Called when the service is first created. Setup one-time resources here.
        Log.d(TAG, "Service onCreate: Initializing service resources");
        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // LECTURE TOPIC: Service Lifecycle - onStartCommand()
        // Called every time startService(intent) is called from an Activity.
        Log.d(TAG, "Service onStartCommand: Received intent with action " + (intent != null ? intent.getAction() : "null"));

        if (intent != null && ACTION_STOP.equals(intent.getAction())) {
            stopCountdown();
            stopSelf(); // Stops the service from within
            return START_NOT_STICKY;
        }

        // Start foreground service with persistent notification
        startForeground(
                NotificationHelper.NOTIFICATION_ID_FOREGROUND,
                NotificationHelper.buildForegroundServiceNotification(this, formatSeconds(mRemainingSeconds))
        );

        startCountdown();

        // START_STICKY tells system to recreate service if killed by OS memory pressure
        return START_STICKY;
    }

    private void startCountdown() {
        if (mIsRunning) return;
        mIsRunning = true;

        mCountdownRunnable = new Runnable() {
            @Override
            public void run() {
                if (mRemainingSeconds > 0) {
                    mRemainingSeconds--;
                    String formatted = formatSeconds(mRemainingSeconds);

                    // 1. Broadcast the updated time to DashboardActivity
                    Intent broadcastIntent = new Intent(ACTION_COUNTDOWN_TICK);
                    broadcastIntent.putExtra(EXTRA_SECONDS_LEFT, mRemainingSeconds);
                    broadcastIntent.putExtra(EXTRA_FORMATTED_TIME, formatted);
                    sendBroadcast(broadcastIntent);

                    // 2. Update persistent foreground notification every 5 seconds to reduce system load
                    if (mRemainingSeconds % 5 == 0) {
                        NotificationManager manager = getSystemService(NotificationManager.class);
                        if (manager != null) {
                            manager.notify(
                                    NotificationHelper.NOTIFICATION_ID_FOREGROUND,
                                    NotificationHelper.buildForegroundServiceNotification(OutageCountdownService.this, formatted)
                            );
                        }
                    }

                    // Schedule next tick in 1 second
                    mHandler.postDelayed(this, 1000);
                } else {
                    // Outage time reached: stop service and show restoration notification demo
                    stopSelf();
                }
            }
        };

        mHandler.post(mCountdownRunnable);
    }

    private void stopCountdown() {
        mIsRunning = false;
        if (mHandler != null && mCountdownRunnable != null) {
            mHandler.removeCallbacks(mCountdownRunnable);
        }
    }

    private String formatSeconds(int totalSeconds) {
        int minutes = totalSeconds / 60;
        int seconds = totalSeconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d min", minutes, seconds);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // LECTURE TOPIC: Service Lifecycle - onDestroy()
        // Called when service is stopped via stopService() or stopSelf(). Clean up resources.
        Log.d(TAG, "Service onDestroy: Stopping countdown and removing callbacks");
        stopCountdown();
        stopForeground(true);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // Return null for a Started Service (not a Bound Service)
        return null;
    }
}

