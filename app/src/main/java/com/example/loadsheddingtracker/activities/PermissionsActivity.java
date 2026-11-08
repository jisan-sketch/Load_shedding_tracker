package com.example.loadsheddingtracker.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.loadsheddingtracker.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// LECTURE TOPIC: Modern Runtime Permissions with ActivityResultContracts.RequestMultiplePermissions
// 
// EXPLANATION FOR VIVA:
// 1. Android 6.0+ (API 23) requires dangerous permissions to be requested at runtime rather than install time.
// 2. Android 13+ (API 33) introduced POST_NOTIFICATIONS as a runtime permission.
// 3. ActivityResultContracts.RequestMultiplePermissions replaces the old requestPermissions() callback.
// 4. If denied, show a rationale dialog explaining why the app needs the permission.
public class PermissionsActivity extends AppCompatActivity {

    private TextView tvNotificationsStatus;
    private TextView tvContactsStatus;
    private TextView tvLocationStatus;
    private TextView tvCallStatus;
    private Button btnRequestPermissions;

    // LECTURE TOPIC: ActivityResultLauncher for Multiple Permissions
    private final ActivityResultLauncher<String[]> mMultiplePermissionsLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            (Map<String, Boolean> result) -> {
                // Refresh status displays for each permission
                updatePermissionStatusUI();

                boolean allGranted = true;
                for (Map.Entry<String, Boolean> entry : result.entrySet()) {
                    if (!entry.getValue()) {
                        allGranted = false;
                        break;
                    }
                }

                if (allGranted) {
                    Toast.makeText(this, "All requested permissions granted!", Toast.LENGTH_SHORT).show();
                } else {
                    showPermissionRationaleDialog();
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permissions);

        tvNotificationsStatus = findViewById(R.id.tv_perm_status_notifications);
        tvContactsStatus = findViewById(R.id.tv_perm_status_contacts);
        tvLocationStatus = findViewById(R.id.tv_perm_status_location);
        tvCallStatus = findViewById(R.id.tv_perm_status_call);
        btnRequestPermissions = findViewById(R.id.btn_request_permissions);

        android.widget.ImageButton btnBack = findViewById(R.id.btn_permissions_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        updatePermissionStatusUI();

        btnRequestPermissions.setOnClickListener(v -> launchPermissionsRequest());
    }

    private void launchPermissionsRequest() {
        List<String> permissionsToRequest = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.READ_CONTACTS);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            permissionsToRequest.add(Manifest.permission.CALL_PHONE);
        }

        if (!permissionsToRequest.isEmpty()) {
            mMultiplePermissionsLauncher.launch(permissionsToRequest.toArray(new String[0]));
        } else {
            Toast.makeText(this, "All permissions are already granted!", Toast.LENGTH_SHORT).show();
        }
    }

    private void updatePermissionStatusUI() {
        // 1. POST_NOTIFICATIONS
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            setLabelStatus(
                    tvNotificationsStatus,
                    ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
            );
        } else {
            tvNotificationsStatus.setText("Granted (API < 33)");
            tvNotificationsStatus.setTextColor(Color.parseColor("#2E7D32"));
        }

        // 2. READ_CONTACTS
        setLabelStatus(
                tvContactsStatus,
                ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
        );

        // 3. ACCESS_FINE_LOCATION
        setLabelStatus(
                tvLocationStatus,
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        );

        // 4. CALL_PHONE
        setLabelStatus(
                tvCallStatus,
                ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
        );
    }

    private void setLabelStatus(TextView tv, boolean isGranted) {
        if (isGranted) {
            tv.setText("✓ GRANTED");
            tv.setTextColor(Color.parseColor("#2E7D32")); // Green
        } else {
            tv.setText("✗ DENIED");
            tv.setTextColor(Color.parseColor("#D32F2F")); // Red
        }
    }

    private void showPermissionRationaleDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Permissions Needed")
                .setMessage("Some permissions were denied. You can still use the app:\n\n" +
                        "• Notifications: For outage countdown warnings.\n" +
                        "• Contacts: To pick family members to alert.\n" +
                        "• Location: To auto-suggest your current district.\n\n" +
                        "Manual fallback options (like choosing areas manually and ACTION_DIAL) remain fully functional.")
                .setPositiveButton("OK", null)
                .show();
    }
}

