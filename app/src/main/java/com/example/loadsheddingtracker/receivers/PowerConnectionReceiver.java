package com.example.loadsheddingtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.loadsheddingtracker.utils.NotificationHelper;

// LECTURE TOPIC: BroadcastReceiver (System Broadcasts)
// Listens for system events: ACTION_POWER_CONNECTED, ACTION_POWER_DISCONNECTED, and ACTION_BATTERY_LOW.
// Gives context-aware power warnings perfectly suited for a load-shedding app.
public class PowerConnectionReceiver extends BroadcastReceiver {

    private static final String TAG = "PowerReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null || intent.getAction() == null) return;

        String action = intent.getAction();
        Log.d(TAG, "Broadcast received action: " + action);

        switch (action) {
            case Intent.ACTION_POWER_DISCONNECTED:
                Toast.makeText(
                        context,
                        "⚡ Electricity Disconnected! Potential load-shedding started.",
                        Toast.LENGTH_LONG
                ).show();
                NotificationHelper.showUpcomingOutageNotification(context, "Your Current Area");
                break;

            case Intent.ACTION_POWER_CONNECTED:
                Toast.makeText(
                        context,
                        "🔌 Power Restored/Connected! Grid charging resumed.",
                        Toast.LENGTH_LONG
                ).show();
                NotificationHelper.showPowerRestoredNotification(context, "Your Current Area");
                break;

            case Intent.ACTION_BATTERY_LOW:
                Toast.makeText(
                        context,
                        "🔋 Warning: Battery Low! Save your work before the next scheduled power cut.",
                        Toast.LENGTH_LONG
                ).show();
                break;
        }
    }
}

