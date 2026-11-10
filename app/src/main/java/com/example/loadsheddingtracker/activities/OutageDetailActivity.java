package com.example.loadsheddingtracker.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.utils.FirebaseHelper;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

// LECTURE TOPIC: Explicit Intent Extras, Firestore/RTDB Update and Delete, and ACTION_SEND
// 
// EXPLANATION FOR VIVA:
// 1. Reads bundled data passed from previous Activity via getIntent().getStringExtra(...)
// 2. Demonstrates Firestore document update: db.collection("outage_history").document(docId).update("notes", newNotes)
// 3. Demonstrates Firestore document delete: db.collection("outage_history").document(docId).delete()
// 4. Demonstrates Implicit Intent with ACTION_SEND to share outage details with third-party apps
public class OutageDetailActivity extends AppCompatActivity {

    private TextView tvArea;
    private TextView tvTimes;
    private EditText etNotes;
    private TextView tvReporter;
    private TextView tvSource;

    private Button btnUpdate;
    private Button btnDelete;
    private Button btnShare;

    private String mDocId;
    private String mArea;
    private String mStartTime;
    private String mEndTime;
    private int mDuration;
    private String mNotes;
    private String mReporterEmail;
    private String mSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outage_detail);

        tvArea = findViewById(R.id.tv_detail_area);
        tvTimes = findViewById(R.id.tv_detail_times);
        etNotes = findViewById(R.id.et_detail_notes);
        tvReporter = findViewById(R.id.tv_detail_reporter);
        tvSource = findViewById(R.id.tv_detail_source);

        btnUpdate = findViewById(R.id.btn_detail_update);
        btnDelete = findViewById(R.id.btn_detail_delete);
        btnShare = findViewById(R.id.btn_detail_share);

        android.widget.ImageButton btnBack = findViewById(R.id.btn_detail_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // LECTURE TOPIC: Reading data passed via Explicit Intent with getIntent()
        Intent intent = getIntent();
        if (intent != null) {
            mDocId = intent.getStringExtra("EXTRA_DOC_ID");
            mArea = intent.getStringExtra("EXTRA_AREA");
            mStartTime = intent.getStringExtra("EXTRA_START_TIME");
            mEndTime = intent.getStringExtra("EXTRA_END_TIME");
            mDuration = intent.getIntExtra("EXTRA_DURATION", 60);
            mNotes = intent.getStringExtra("EXTRA_NOTES");
            mReporterEmail = intent.getStringExtra("EXTRA_EMAIL");
            mSource = intent.getStringExtra("EXTRA_SOURCE");
        }

        // Populate Views
        tvArea.setText(mArea != null ? mArea + " Outage" : "Outage Incident");
        tvTimes.setText("Time: " + (mStartTime != null ? mStartTime : "10:00 AM") + " - " +
                (mEndTime != null ? mEndTime : "11:30 AM") + " (" + mDuration + " min)");
        etNotes.setText(mNotes != null ? mNotes : "");
        tvReporter.setText("Reported by: " + (mReporterEmail != null ? mReporterEmail : "Anonymous"));
        tvSource.setText("Data Source: " + ("RTDB".equals(mSource) ? "Firebase Realtime Database (/reports)" : "Cloud Firestore (outage_history)"));

        // Update Button listener
        btnUpdate.setOnClickListener(v -> performUpdate());

        // Delete Button listener
        btnDelete.setOnClickListener(v -> performDelete());

        // Share Button listener (Implicit Intent ACTION_SEND)
        btnShare.setOnClickListener(v -> shareOutageDetails());
    }

    /**
     * LECTURE TOPIC: Updating a document in Cloud Firestore or Realtime Database
     */
    private void performUpdate() {
        String updatedNotes = etNotes.getText().toString().trim();
        if (TextUtils.isEmpty(updatedNotes)) {
            etNotes.setError("Notes cannot be empty");
            return;
        }

        if ("RTDB".equals(mSource)) {
            // Update in Realtime Database
            if (mDocId != null) {
                FirebaseDatabase.getInstance().getReference("reports").child(mDocId).child("description").setValue(updatedNotes)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Updated in Realtime Database!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Notes updated locally in demo mode", Toast.LENGTH_SHORT).show();
            }
        } else {
            // Update in Cloud Firestore
            if (mDocId != null) {
                FirebaseFirestore.getInstance().collection("outage_history").document(mDocId)
                        .update("notes", updatedNotes)
                        .addOnSuccessListener(aVoid -> Toast.makeText(this, "Updated in Cloud Firestore!", Toast.LENGTH_SHORT).show())
                        .addOnFailureListener(e -> Toast.makeText(this, "Update failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Notes updated locally in demo mode", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * LECTURE TOPIC: Deleting a document from Cloud Firestore or Realtime Database
     */
    private void performDelete() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(this)
                .setTitle("Delete Incident Record")
                .setMessage("Are you sure you want to permanently delete this outage record?")
                .setPositiveButton("Delete", (dialog, which) -> executeDelete())
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void executeDelete() {
        if ("RTDB".equals(mSource)) {
            if (mDocId != null) {
                FirebaseDatabase.getInstance().getReference("reports").child(mDocId).removeValue()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Deleted from Realtime Database", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Record deleted (demo mode)", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            if (mDocId != null) {
                FirebaseFirestore.getInstance().collection("outage_history").document(mDocId).delete()
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(this, "Deleted from Cloud Firestore", Toast.LENGTH_SHORT).show();
                            finish();
                        })
                        .addOnFailureListener(e -> Toast.makeText(this, "Delete failed: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } else {
                Toast.makeText(this, "Record deleted (demo mode)", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * LECTURE TOPIC: Implicit Intent - ACTION_SEND with text/plain
     */
    private void shareOutageDetails() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");

        String content = "⚠️ Load-shedding Incident in " + mArea + "\n" +
                "Duration: " + mDuration + " min (" + mStartTime + " - " + mEndTime + ")\n" +
                "Notes: " + etNotes.getText().toString().trim() + "\n" +
                "Tracked via Load-shedding Tracker App.";

        shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Power Cut in " + mArea);
        shareIntent.putExtra(Intent.EXTRA_TEXT, content);

        startActivity(Intent.createChooser(shareIntent, "Share Outage Details via:"));
    }
}

