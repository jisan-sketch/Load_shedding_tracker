package com.example.loadsheddingtracker.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.adapters.OutageRecyclerAdapter;
import com.example.loadsheddingtracker.models.Outage;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;
import java.util.List;

// LECTURE TOPIC: Cloud Firestore Read Data & RecyclerView LayoutManager Switching
// 
// EXPLANATION FOR VIVA:
// 1. Cloud Firestore reads data via queries: collection("outage_history").whereEqualTo("area", area).orderBy("timestamp").
// 2. Demonstrates two read approaches:
//    - One-time query with .get() (e.g. for offline caching or initial loads).
//    - Real-time snapshot stream with .addSnapshotListener() for live document updates.
// 3. LayoutManager Variety: Switches between LinearLayoutManager (1 column) and GridLayoutManager (2 columns)
//    at runtime via a button toggle, showing how RecyclerView decouples layout policy from data adapters.
public class OutageHistoryActivity extends AppCompatActivity {

    private static final String TAG = "OutageHistoryActivity";

    private RecyclerView rvOutageHistory;
    private ProgressBar pbHistory;
    private TextView tvEmpty;
    private TextView tvSubtitle;
    private Button btnToggleLayout;

    private OutageRecyclerAdapter mAdapter;
    private final List<Outage> mOutageList = new ArrayList<>();
    private String mCurrentArea = "Khulna";

    // LayoutManager state
    private boolean mIsGridLayout = false;
    private LinearLayoutManager mLinearLayoutManager;
    private GridLayoutManager mGridLayoutManager;

    // Firestore listener registration for unregistering in onStop()
    private ListenerRegistration mFirestoreListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_outage_history);

        if (getIntent().hasExtra("EXTRA_AREA")) {
            mCurrentArea = getIntent().getStringExtra("EXTRA_AREA");
        }

        rvOutageHistory = findViewById(R.id.rv_outage_history);
        pbHistory = findViewById(R.id.pb_history);
        tvEmpty = findViewById(R.id.tv_empty_history);
        tvSubtitle = findViewById(R.id.tv_history_subtitle);
        btnToggleLayout = findViewById(R.id.btn_toggle_layout_manager);

        android.widget.ImageButton btnBack = findViewById(R.id.btn_outage_history_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvSubtitle.setText("Firestore Query: area = '" + mCurrentArea + "' ordered by timestamp");

        // LECTURE TOPIC: LayoutManager variety
        mLinearLayoutManager = new LinearLayoutManager(this);
        mGridLayoutManager = new GridLayoutManager(this, 2); // 2 columns grid

        rvOutageHistory.setLayoutManager(mLinearLayoutManager);
        mAdapter = new OutageRecyclerAdapter(this, mOutageList);
        rvOutageHistory.setAdapter(mAdapter);

        // Toggle button listener to switch LayoutManager at runtime
        btnToggleLayout.setOnClickListener(v -> toggleLayoutManager());
    }

    @Override
    protected void onStart() {
        super.onStart();
        // LECTURE TOPIC: Cloud Firestore Querying & Real-time Snapshot Listener in onStart
        attachFirestoreListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop Firestore real-time listener in onStop to avoid memory leaks
        if (mFirestoreListener != null) {
            mFirestoreListener.remove();
            mFirestoreListener = null;
        }
    }

    /**
     * LECTURE TOPIC: Switching between LinearLayoutManager and GridLayoutManager at runtime
     */
    private void toggleLayoutManager() {
        if (mIsGridLayout) {
            rvOutageHistory.setLayoutManager(mLinearLayoutManager);
            btnToggleLayout.setText("Switch to Grid View");
            mIsGridLayout = false;
        } else {
            rvOutageHistory.setLayoutManager(mGridLayoutManager);
            btnToggleLayout.setText("Switch to List View");
            mIsGridLayout = true;
        }
        mAdapter.notifyDataSetChanged();
    }

    /**
     * LECTURE TOPIC: Cloud Firestore Querying & Real-time Snapshot Listener
     */
    private void attachFirestoreListener() {
        pbHistory.setVisibility(View.VISIBLE);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Build query: collection("outage_history").whereEqualTo("area", mCurrentArea).orderBy("timestamp", Query.Direction.DESCENDING)
        Query query = db.collection("outage_history")
                .whereEqualTo("area", mCurrentArea)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        // Attach real-time listener via .addSnapshotListener()
        mFirestoreListener = query.addSnapshotListener((snapshots, e) -> {
            pbHistory.setVisibility(View.GONE);
            if (e != null) {
                Log.e(TAG, "SnapshotListener error: " + e.getMessage(), e);
                String errorMsg = e.getMessage() != null ? e.getMessage() : "Error loading outages";
                Toast.makeText(OutageHistoryActivity.this, errorMsg, Toast.LENGTH_LONG).show();
                return;
            }

            if (snapshots != null) {
                mOutageList.clear();
                for (DocumentSnapshot doc : snapshots.getDocuments()) {
                    Outage item = doc.toObject(Outage.class);
                    if (item != null) {
                        item.id = doc.getId();
                        mOutageList.add(item);
                    }
                }

                if (mOutageList.isEmpty()) {
                    tvEmpty.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.GONE);
                }
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    private void loadSampleDemoHistory() {
        mOutageList.clear();
        mOutageList.add(new Outage("doc_1", mCurrentArea, "09:00 AM", "10:30 AM", "uid1", 90, System.currentTimeMillis() - 86400000, "Scheduled morning rotation cut on 33kV line."));
        mOutageList.add(new Outage("doc_2", mCurrentArea, "02:00 PM", "03:15 PM", "uid2", 75, System.currentTimeMillis() - 172800000, "Substation transformer cooling maintenance."));
        mOutageList.add(new Outage("doc_3", mCurrentArea, "07:30 PM", "08:30 PM", "uid3", 60, System.currentTimeMillis() - 259200000, "Evening peak deficit load curtailment."));
        mOutageList.add(new Outage("doc_4", mCurrentArea, "11:00 AM", "12:00 PM", "uid4", 60, System.currentTimeMillis() - 345600000, "Transmission line tree trimming maintenance."));

        tvEmpty.setVisibility(View.GONE);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister Firestore listener to prevent memory leaks
        if (mFirestoreListener != null) {
            mFirestoreListener.remove();
        }
    }
}

