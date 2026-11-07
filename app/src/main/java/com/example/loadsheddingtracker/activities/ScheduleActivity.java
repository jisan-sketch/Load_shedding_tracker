package com.example.loadsheddingtracker.activities;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.loadsheddingtracker.R;
import com.example.loadsheddingtracker.adapters.ScheduleAdapter;
import com.example.loadsheddingtracker.models.ScheduleModel;
import com.example.loadsheddingtracker.providers.ScheduleContract;

import java.util.ArrayList;
import java.util.List;

// LECTURE TOPIC: Custom ListView, Custom Adapter & ContentProvider Integration
// 
// EXPLANATION FOR VIVA:
// 1. Queries data from our own ScheduleContentProvider using getContentResolver().query().
// 2. Extracts data from Cursor into a List<ScheduleModel>.
// 3. Feeds the custom ScheduleAdapter which implements the convertView reuse pattern with ViewHolder.
// 4. Demonstrates inserting new schedule rows using getContentResolver().insert().
public class ScheduleActivity extends AppCompatActivity {

    private static final String TAG = "ScheduleActivity";

    private ListView lvTodaySchedule;
    private TextView tvScheduleHeader;
    private TextView tvScheduleAreaSubtitle;
    private Button btnAddSampleSchedule;

    private ScheduleAdapter mAdapter;
    private final List<ScheduleModel> mScheduleList = new ArrayList<>();
    private String mCurrentArea = "Khulna";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        // Read area passed via explicit Intent extra
        if (getIntent().hasExtra("EXTRA_AREA")) {
            mCurrentArea = getIntent().getStringExtra("EXTRA_AREA");
        }

        tvScheduleHeader = findViewById(R.id.tv_schedule_header);
        tvScheduleAreaSubtitle = findViewById(R.id.tv_schedule_area_subtitle);
        lvTodaySchedule = findViewById(R.id.lv_today_schedule);
        btnAddSampleSchedule = findViewById(R.id.btn_add_sample_schedule);

        ImageButton btnBack = findViewById(R.id.btn_schedule_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }

        tvScheduleHeader.setText("Today's Schedule: " + mCurrentArea);
        tvScheduleAreaSubtitle.setText("Loaded from ScheduleContentProvider for " + mCurrentArea);

        // Initialize custom adapter with item_schedule layout
        mAdapter = new ScheduleAdapter(this, R.layout.item_schedule, mScheduleList);
        lvTodaySchedule.setAdapter(mAdapter);

        // Load schedules from ContentProvider
        loadSchedulesFromContentProvider();

        // LECTURE TOPIC: ContentResolver insert() demonstration
        btnAddSampleSchedule.setOnClickListener(v -> insertSampleScheduleRow());
    }

    /**
     * LECTURE TOPIC: ContentResolver query() on our custom ContentProvider
     */
    private void loadSchedulesFromContentProvider() {
        mScheduleList.clear();

        // Query URI: content://com.example.loadsheddingtracker.provider/schedules
        Uri uri = ScheduleContract.CONTENT_URI;
        String selection = ScheduleContract.ScheduleEntry.COLUMN_AREA + "=?";
        String[] selectionArgs = new String[]{mCurrentArea};

        Cursor cursor = getContentResolver().query(
                uri,
                null,
                selection,
                selectionArgs,
                ScheduleContract.ScheduleEntry._ID + " ASC"
        );

        if (cursor != null) {
            try {
                int idCol = cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry._ID);
                int areaCol = cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_AREA);
                int startCol = cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_START_TIME);
                int endCol = cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_END_TIME);
                int statusCol = cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_STATUS);
                int dayCol = cursor.getColumnIndexOrThrow(ScheduleContract.ScheduleEntry.COLUMN_DAY_OF_WEEK);

                while (cursor.moveToNext()) {
                    long id = cursor.getLong(idCol);
                    String area = cursor.getString(areaCol);
                    String start = cursor.getString(startCol);
                    String end = cursor.getString(endCol);
                    String status = cursor.getString(statusCol);
                    String day = cursor.getString(dayCol);

                    mScheduleList.add(new ScheduleModel(id, area, start, end, status, day));
                }
            } catch (Exception e) {
                Log.e(TAG, "Cursor parsing error: " + e.getMessage());
            } finally {
                cursor.close(); // Always close Cursor to avoid memory leaks
            }
        }

        // If no records for this specific area, insert default fallback rows
        if (mScheduleList.isEmpty()) {
            mScheduleList.add(new ScheduleModel(1, mCurrentArea, "10:00 AM", "11:30 AM", "Scheduled", "Today"));
            mScheduleList.add(new ScheduleModel(2, mCurrentArea, "04:00 PM", "05:30 PM", "Scheduled", "Today"));
            mScheduleList.add(new ScheduleModel(3, mCurrentArea, "09:00 PM", "10:00 PM", "Restored", "Today"));
        }

        mAdapter.notifyDataSetChanged();
    }

    /**
     * LECTURE TOPIC: ContentResolver insert() to add a new schedule row
     */
    private void insertSampleScheduleRow() {
        ContentValues values = new ContentValues();
        values.put(ScheduleContract.ScheduleEntry.COLUMN_AREA, mCurrentArea);
        values.put(ScheduleContract.ScheduleEntry.COLUMN_START_TIME, "06:00 PM");
        values.put(ScheduleContract.ScheduleEntry.COLUMN_END_TIME, "07:30 PM");
        values.put(ScheduleContract.ScheduleEntry.COLUMN_STATUS, "Scheduled");
        values.put(ScheduleContract.ScheduleEntry.COLUMN_DAY_OF_WEEK, "Evening Slot");

        Uri insertedUri = getContentResolver().insert(ScheduleContract.CONTENT_URI, values);
        Toast.makeText(this, "Inserted row via ContentResolver: " + insertedUri, Toast.LENGTH_SHORT).show();

        // Refresh list to show newly inserted row
        loadSchedulesFromContentProvider();
    }
}

