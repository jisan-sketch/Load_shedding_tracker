package com.example.loadsheddingtracker.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.models.Outage;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

// LECTURE TOPIC: Cloud Firestore - Add Data (collection.add & collection.document.set)
// 
// EXPLANATION FOR VIVA:
// 1. db.collection("outage_history").add(model) creates a document with an auto-generated ID.
// 2. Alternatively, db.collection("outage_history").document(customId).set(model) sets data at a specific ID.
// 3. Demonstrates interaction with GridLayout buttons to dynamically set duration minutes.
public class LogOutageActivity extends AppCompatActivity {

    private EditText etArea;
    private EditText etStartTime;
    private EditText etEndTime;
    private EditText etDuration;
    private EditText etNotes;
    private Button btnSave;
    private ProgressBar pbLog;

    // GridLayout preset buttons
    private Button btn30, btn60, btn90, btn120;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_outage);

        etArea = findViewById(R.id.et_log_area);
        etStartTime = findViewById(R.id.et_log_start_time);
        etEndTime = findViewById(R.id.et_log_end_time);
        etDuration = findViewById(R.id.et_log_duration);
        etNotes = findViewById(R.id.et_log_notes);
        btnSave = findViewById(R.id.btn_save_firestore_outage);
        pbLog = findViewById(R.id.pb_log_outage);

        android.widget.ImageButton btnBack = findViewById(R.id.btn_log_outage_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Pre-fill initial area if passed
        String initialArea = getIntent().getStringExtra("EXTRA_AREA");
        if (initialArea != null) {
            etArea.setText(initialArea);
        }

        // Setup GridLayout duration preset buttons
        btn30 = findViewById(R.id.btn_duration_30);
        btn60 = findViewById(R.id.btn_duration_60);
        btn90 = findViewById(R.id.btn_duration_90);
        btn120 = findViewById(R.id.btn_duration_120);

        btn30.setOnClickListener(v -> etDuration.setText("30"));
        btn60.setOnClickListener(v -> etDuration.setText("60"));
        btn90.setOnClickListener(v -> etDuration.setText("90"));
        btn120.setOnClickListener(v -> etDuration.setText("120"));

        btnSave.setOnClickListener(v -> saveOutageToFirestore());
    }

    private void saveOutageToFirestore() {
        String area = etArea.getText().toString().trim();
        String startTime = etStartTime.getText().toString().trim();
        String endTime = etEndTime.getText().toString().trim();
        String durationStr = etDuration.getText().toString().trim();
        String notes = etNotes.getText().toString().trim();

        if (TextUtils.isEmpty(area)) {
            etArea.setError("Area is required");
            etArea.requestFocus();
            return;
        }

        int duration = 60;
        try {
            duration = Integer.parseInt(durationStr);
        } catch (NumberFormatException ignored) {}

        pbLog.setVisibility(View.VISIBLE);
        btnSave.setEnabled(false);

        String userId = "anonymous";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            userId = auth.getCurrentUser().getUid();
        }

        Outage outage = new Outage(
                null,
                area,
                startTime,
                endTime,
                userId,
                duration,
                System.currentTimeMillis(),
                notes
        );

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        // LECTURE TOPIC: Cloud Firestore add() method
        db.collection("outage_history").add(outage)
                .addOnSuccessListener(documentReference -> {
                    pbLog.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(LogOutageActivity.this, "Outage logged to Firestore with ID: " + documentReference.getId(), Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    pbLog.setVisibility(View.GONE);
                    btnSave.setEnabled(true);
                    Toast.makeText(LogOutageActivity.this, "Failed to log outage: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }
}

