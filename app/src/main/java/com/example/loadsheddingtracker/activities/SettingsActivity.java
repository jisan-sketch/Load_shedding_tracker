package com.example.loadsheddingtracker.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;

/**
 * LECTURE TOPIC: Activity Navigation, Explicit & Implicit Intents, Dialogs & Settings
 * Clean settings screen organizing secondary workflows:
 * 1. App Permissions (Notification, Contacts, Location)
 * 2. Emergency Family Contact (Content Provider integration)
 * 3. Helpline & Utility Portal (Implicit Intents)
 * 4. Viva Topic Map & About (Course documentation)
 * 5. Sign Out with Confirmation Dialog
 */
public class SettingsActivity extends AppCompatActivity {

    private ImageButton btnBack;
    private LinearLayout rowPermissions;
    private LinearLayout rowFamilyContact;
    private LinearLayout rowHelpline;
    private LinearLayout rowPortal;
    private LinearLayout rowAboutViva;
    private MaterialButton btnLogout;
    private TextView tvSelectedContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Bind views
        btnBack = findViewById(R.id.btn_settings_back);
        rowPermissions = findViewById(R.id.row_permissions);
        rowFamilyContact = findViewById(R.id.row_family_contact);
        rowHelpline = findViewById(R.id.row_helpline);
        rowPortal = findViewById(R.id.row_portal);
        rowAboutViva = findViewById(R.id.row_about_viva);
        btnLogout = findViewById(R.id.btn_settings_logout);
        tvSelectedContact = findViewById(R.id.tv_settings_selected_contact);

        // 1. Back button
        btnBack.setOnClickListener(v -> finish());

        // 2. EXPLICIT INTENT: Open Permissions screen
        rowPermissions.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, PermissionsActivity.class);
            startActivity(intent);
        });

        // 3. EXPLICIT INTENT: Open Contact Picker (Content Provider demonstration)
        rowFamilyContact.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, ContactPickerActivity.class);
            startActivity(intent);
        });

        // 4. IMPLICIT INTENT 1: ACTION_DIAL to call electricity helpline 16999
        rowHelpline.setOnClickListener(v -> {
            Intent dialIntent = new Intent(Intent.ACTION_DIAL);
            dialIntent.setData(Uri.parse("tel:" + DashboardActivity.HELPLINE_NUMBER));
            startActivity(dialIntent);
        });

        // 5. IMPLICIT INTENT 2: ACTION_VIEW to open utility web portal
        rowPortal.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(DashboardActivity.POWER_CO_URL));
            startActivity(browserIntent);
        });

        // 6. EXPLICIT INTENT: Open About & Viva Topic Map
        rowAboutViva.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, AboutActivity.class);
            startActivity(intent);
        });

        // 7. Sign Out with Material Confirmation Dialog
        btnLogout.setOnClickListener(v -> showLogoutDialog());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Read saved emergency contact from SharedPreferences
        SharedPreferences prefs = getSharedPreferences("AppSettings", MODE_PRIVATE);
        String savedContactName = prefs.getString("emergency_contact_name", null);
        String savedContactNumber = prefs.getString("emergency_contact_number", null);
        if (savedContactName != null && savedContactNumber != null) {
            tvSelectedContact.setText(savedContactName + " (" + savedContactNumber + ")");
        } else {
            tvSelectedContact.setText("Tap to choose contact via Content Provider");
        }
    }

    private void showLogoutDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out of Load-shedding Tracker?")
                .setPositiveButton("Sign Out", (dialog, which) -> {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(SettingsActivity.this, "Signed out successfully", Toast.LENGTH_SHORT).show();

                    Intent loginIntent = new Intent(SettingsActivity.this, LoginActivity.class);
                    loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(loginIntent);
                    finish();
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }
}

