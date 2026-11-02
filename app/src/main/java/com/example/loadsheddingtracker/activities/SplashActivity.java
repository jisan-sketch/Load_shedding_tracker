package com.example.loadsheddingtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

// LECTURE TOPIC: Activity Lifecycle & FrameLayout Splash Screen
// Demonstrates a timed splash screen using FrameLayout and Handler.postDelayed,
// followed by conditional navigation (auto-login check or login screen).
public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY_MS = 2000; // 2 seconds delay

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the FrameLayout splash screen layout
        setContentView(R.layout.activity_splash);

        // Schedule delayed navigation on the main Looper
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            decideNextScreen();
        }, SPLASH_DELAY_MS);
    }

    private void decideNextScreen() {
        // LECTURE TOPIC: Firebase Auth - Auto-login check (currentUser)
        boolean hasUser = false;
        if (FirebaseHelper.isFirebaseConfigured(this)) {
            FirebaseAuth auth = FirebaseHelper.getAuth(this);
            if (auth != null) {
                FirebaseUser currentUser = auth.getCurrentUser();
                hasUser = (currentUser != null);
            }
        }

        Intent nextIntent;
        if (hasUser) {
            // User already authenticated: navigate directly to Dashboard
            nextIntent = new Intent(SplashActivity.this, DashboardActivity.class);
        } else {
            // No active session: proceed to LoginActivity
            nextIntent = new Intent(SplashActivity.this, LoginActivity.class);
        }

        startActivity(nextIntent);
        finish(); // Finish SplashActivity so back button doesn't return to splash
    }
}

