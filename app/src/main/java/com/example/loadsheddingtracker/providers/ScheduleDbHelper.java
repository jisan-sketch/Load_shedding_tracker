package com.example.loadsheddingtracker.providers;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

// LECTURE TOPIC: SQLite Database Helper
// Manages database creation, version upgrades, and seeds initial data so the app always displays schedules.
public class ScheduleDbHelper extends SQLiteOpenHelper {

    private static final String TAG = "ScheduleDbHelper";
    private static final String DATABASE_NAME = "loadshedding.db";
    private static final int DATABASE_VERSION = 1;

    // SQL statement to create the schedules table
    private static final String SQL_CREATE_SCHEDULES_TABLE =
            "CREATE TABLE " + ScheduleContract.ScheduleEntry.TABLE_NAME + " (" +
                    ScheduleContract.ScheduleEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ScheduleContract.ScheduleEntry.COLUMN_AREA + " TEXT NOT NULL, " +
                    ScheduleContract.ScheduleEntry.COLUMN_START_TIME + " TEXT NOT NULL, " +
                    ScheduleContract.ScheduleEntry.COLUMN_END_TIME + " TEXT NOT NULL, " +
                    ScheduleContract.ScheduleEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                    ScheduleContract.ScheduleEntry.COLUMN_DAY_OF_WEEK + " TEXT NOT NULL);";

    public ScheduleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Creating database table: " + ScheduleContract.ScheduleEntry.TABLE_NAME);
        db.execSQL(SQL_CREATE_SCHEDULES_TABLE);
        // Seed default sample data so screens are never empty on initial install
        seedSampleData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + ScheduleContract.ScheduleEntry.TABLE_NAME);
        onCreate(db);
    }

    /**
     * Seeds initial schedule records for Bangladesh areas (Khulna, Dhaka, Rajshahi, Sylhet, Barisal, etc.)
     */
    private void seedSampleData(SQLiteDatabase db) {
        insertRow(db, "Khulna", "10:00 AM", "11:30 AM", "Scheduled", "Today");
        insertRow(db, "Khulna", "03:00 PM", "04:30 PM", "Scheduled", "Today");
        insertRow(db, "Khulna", "08:00 PM", "09:00 PM", "Scheduled", "Today");
        insertRow(db, "Dhaka", "09:00 AM", "10:30 AM", "Restored", "Today");
        insertRow(db, "Dhaka", "02:00 PM", "03:00 PM", "Scheduled", "Today");
        insertRow(db, "Dhaka", "07:00 PM", "08:30 PM", "Scheduled", "Today");
        insertRow(db, "Rajshahi", "11:00 AM", "12:30 PM", "Scheduled", "Today");
        insertRow(db, "Rajshahi", "04:00 PM", "05:00 PM", "Scheduled", "Today");
        insertRow(db, "Sylhet", "01:00 PM", "02:30 PM", "Scheduled", "Today");
        insertRow(db, "Barisal", "12:00 PM", "01:30 PM", "Scheduled", "Today");
        insertRow(db, "Chattogram", "10:30 AM", "12:00 PM", "Restored", "Today");
        insertRow(db, "Chattogram", "05:00 PM", "06:30 PM", "Scheduled", "Today");
    }

    private void insertRow(SQLiteDatabase db, String area, String start, String end, String status, String day) {
        ContentValues cv = new ContentValues();
        cv.put(ScheduleContract.ScheduleEntry.COLUMN_AREA, area);
        cv.put(ScheduleContract.ScheduleEntry.COLUMN_START_TIME, start);
        cv.put(ScheduleContract.ScheduleEntry.COLUMN_END_TIME, end);
        cv.put(ScheduleContract.ScheduleEntry.COLUMN_STATUS, status);
        cv.put(ScheduleContract.ScheduleEntry.COLUMN_DAY_OF_WEEK, day);
        db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, cv);
    }
}

