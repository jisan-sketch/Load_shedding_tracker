package com.example.loadsheddingtracker.providers;

import android.net.Uri;
import android.provider.BaseColumns;

// LECTURE TOPIC: Content Provider Contract Class
// Defines the URI authority, paths, and table schema constants for the custom Content Provider.
public final class ScheduleContract {

    // Private constructor to prevent instantiation
    private ScheduleContract() {}

    // Authority matches the declaration in AndroidManifest.xml
    public static final String AUTHORITY = "com.example.loadsheddingtracker.provider";

    // Base content URI
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    // Path for the schedules table
    public static final String PATH_SCHEDULES = "schedules";

    // Complete URI for querying schedules: content://com.example.loadsheddingtracker.provider/schedules
    public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_SCHEDULES).build();

    // Table and column definitions implementing BaseColumns (provides _ID)
    public static final class ScheduleEntry implements BaseColumns {
        public static final String TABLE_NAME = "schedules";

        public static final String COLUMN_AREA = "area";
        public static final String COLUMN_START_TIME = "start_time";
        public static final String COLUMN_END_TIME = "end_time";
        public static final String COLUMN_STATUS = "status"; // Scheduled, Restored, Warning
        public static final String COLUMN_DAY_OF_WEEK = "day_of_week"; // e.g. Sunday, Monday

        // MIME types for directory and single item
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd." + AUTHORITY + "." + PATH_SCHEDULES;
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd." + AUTHORITY + "." + PATH_SCHEDULES;
    }
}

