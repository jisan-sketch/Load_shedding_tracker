package com.example.loadsheddingtracker.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.models.Report;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.UUID;

// LECTURE TOPIC: Firebase Realtime Database - Save Data (setValue & push)
// 
// EXPLANATION FOR VIVA:
// 1. DatabaseReference.push() generates a unique, timestamp-ordered key under '/reports'.
// 2. DatabaseReference.setValue(model) serializes the Java model class automatically into JSON.
// 3. The model must have a public no-arg constructor and public getters/setters.
public class ReportOutageActivity extends AppCompatActivity {

    private EditText etArea;
    private Spinner spCause;
    private EditText etDescription;
    private Button btnSubmit;
    private ProgressBar pbReport;

    private final String[] CAUSE_OPTIONS = new String[]{
            "Scheduled Load-shedding",
            "Transformer Overload / Explosion",
            "Grid Line Tripping",
            "Substation Maintenance",
            "Thunderstorm / Weather Damage",
            "Unannounced Power Cut"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report_outage);

        etArea = findViewById(R.id.et_report_area);
        spCause = findViewById(R.id.sp_report_cause);
        etDescription = findViewById(R.id.et_report_description);
        btnSubmit = findViewById(R.id.btn_submit_report);
        pbReport = findViewById(R.id.pb_report);

        android.widget.ImageButton btnBack = findViewById(R.id.btn_report_outage_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Pre-fill area if passed from Dashboard
        String initialArea = getIntent().getStringExtra("EXTRA_AREA");
        if (initialArea != null) {
            etArea.setText(initialArea);
        } else {
            etArea.setText("Khulna");
        }

        // Setup Cause Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                CAUSE_OPTIONS
        );
        spCause.setAdapter(spinnerAdapter);

        // Submit Button click
        btnSubmit.setOnClickListener(v -> submitOutageReport());
    }

    private void submitOutageReport() {
        String area = etArea.getText().toString().trim();
        String cause = spCause.getSelectedItem().toString();
        String desc = etDescription.getText().toString().trim();

        if (TextUtils.isEmpty(area)) {
            etArea.setError("Area is required");
            etArea.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(desc)) {
            etDescription.setError("Please describe the incident");
            etDescription.requestFocus();
            return;
        }

        pbReport.setVisibility(View.VISIBLE);
        btnSubmit.setEnabled(false);

        // Retrieve current user UID
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String userId = (auth.getCurrentUser() != null) ? auth.getCurrentUser().getUid() : "anonymous_user";

        DatabaseReference reportsRef = FirebaseDatabase.getInstance().getReference("reports");
        String id = reportsRef.push().getKey();
        long time = System.currentTimeMillis();

        Report report = new Report(id, area, cause, desc, userId, time);

        if (id != null) {
            reportsRef.child(id).setValue(report)
                    .addOnCompleteListener(task -> {
                        pbReport.setVisibility(View.GONE);
                        btnSubmit.setEnabled(true);
                        if (task.isSuccessful()) {
                            Toast.makeText(ReportOutageActivity.this, "Outage reported successfully!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String err = task.getException() != null ? task.getException().getMessage() : "Submission failed";
                            Toast.makeText(ReportOutageActivity.this, "Error: " + err, Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
}

