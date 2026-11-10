package com.example.loadsheddingtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.adapters.CommunityReportsAdapter;
import com.example.loadsheddingtracker.models.Report;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * LECTURE TOPIC: Firebase Realtime Database - Retrieve Data (addValueEventListener)
 *
 * Requirements:
 * 1. Attach ValueEventListener in onStart() and remove in onStop()
 * 2. Rebuild list from snapshot children using child.getValue(Report.class)
 * 3. Call adapter.notifyDataSetChanged()
 * 4. Handle onCancelled with Toast
 * 5. Delete: reference("reports").child(report.id).removeValue()
 */
public class LiveReportsActivity extends AppCompatActivity implements CommunityReportsAdapter.OnReportActionListener {

    private static final String TAG = "LiveReportsActivity";

    private RecyclerView rvReports;
    private ProgressBar pbReports;
    private TextView tvEmpty;

    private CommunityReportsAdapter mAdapter;
    private final List<Report> mReportList = new ArrayList<>();

    // Firebase References
    private DatabaseReference mReportsRef;
    private ValueEventListener mValueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_reports);

        rvReports = findViewById(R.id.rv_live_reports);
        pbReports = findViewById(R.id.pb_live_reports);
        tvEmpty = findViewById(R.id.tv_empty_reports);

        ImageButton btnBack = findViewById(R.id.btn_live_reports_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        // Setup RecyclerView with LinearLayoutManager
        rvReports.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new CommunityReportsAdapter(this, mReportList, this);
        rvReports.setAdapter(mAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // LECTURE TOPIC: Attach ValueEventListener in onStart()
        pbReports.setVisibility(View.VISIBLE);
        mReportsRef = FirebaseDatabase.getInstance().getReference("reports");

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mReportList.clear();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Report report = child.getValue(Report.class);
                    if (report != null) {
                        if (report.id == null || report.id.isEmpty()) {
                            report.id = child.getKey();
                        }
                        mReportList.add(report);
                    }
                }

                // Show most recent reports first
                Collections.reverse(mReportList);

                pbReports.setVisibility(View.GONE);
                if (mReportList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                pbReports.setVisibility(View.GONE);
                Log.e(TAG, "RTDB onCancelled: " + error.getMessage());
                Toast.makeText(LiveReportsActivity.this, "RTDB Error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        };

        mReportsRef.addValueEventListener(mValueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // LECTURE TOPIC: Remove listener in onStop() to avoid memory leaks
        if (mReportsRef != null && mValueEventListener != null) {
            mReportsRef.removeEventListener(mValueEventListener);
        }
    }

    @Override
    public void onDeleteReport(Report report) {
        // LECTURE TOPIC: Delete report: reference("reports").child(report.id).removeValue()
        if (report != null && report.id != null) {
            FirebaseDatabase.getInstance().getReference("reports").child(report.id).removeValue()
                    .addOnSuccessListener(aVoid -> Toast.makeText(this, "Report deleted successfully", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }
    }
}
