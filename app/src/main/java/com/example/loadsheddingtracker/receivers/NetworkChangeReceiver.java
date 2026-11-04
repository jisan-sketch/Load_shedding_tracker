package com.example.loadsheddingtracker.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

// LECTURE TOPIC: Dynamic BroadcastReceiver
// Registered dynamically in an Activity (registerReceiver in onStart/onResume and unregisterReceiver in onStop/onPause).
// Notifies the UI in real-time when the network connectivity state changes.
public class NetworkChangeReceiver extends BroadcastReceiver {

    private static final String TAG = "NetworkChangeReceiver";

    public interface NetworkCallback {
        void onNetworkChanged(boolean isConnected);
    }

    private final NetworkCallback callback;

    public NetworkChangeReceiver(NetworkCallback callback) {
        this.callback = callback;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        boolean isConnected = false;

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            isConnected = (activeNetwork != null && activeNetwork.isConnectedOrConnecting());
        }

        Log.d(TAG, "Network state changed. Connected: " + isConnected);
        if (callback != null) {
            callback.onNetworkChanged(isConnected);
        }
    }
}

