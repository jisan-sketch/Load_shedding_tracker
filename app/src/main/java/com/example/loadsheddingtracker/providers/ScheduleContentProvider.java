package com.example.loadsheddingtracker.providers;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

// LECTURE TOPIC: Custom Content Provider
// Implements query, insert, update, delete, getType, and UriMatcher backed by SQLiteOpenHelper.
public class ScheduleContentProvider extends ContentProvider {

    private static final String TAG = "ScheduleProvider";

    // URI matcher match codes
    private static final int CODE_SCHEDULES = 100;
    private static final int CODE_SCHEDULE_WITH_ID = 101;

    // Build the UriMatcher
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ScheduleContract.AUTHORITY;

        // content://com.example.loadsheddingtracker.provider/schedules
        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULES, CODE_SCHEDULES);
        // content://com.example.loadsheddingtracker.provider/schedules/#
        matcher.addURI(authority, ScheduleContract.PATH_SCHEDULES + "/#", CODE_SCHEDULE_WITH_ID);

        return matcher;
    }

    private ScheduleDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        Log.d(TAG, "ContentProvider onCreate: Initializing ScheduleDbHelper");
        mDbHelper = new ScheduleDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mDbHelper.getReadableDatabase();
        final int match = sUriMatcher.match(uri);
        Cursor retCursor;

        switch (match) {
            case CODE_SCHEDULES:
                retCursor = db.query(
                        ScheduleContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            case CODE_SCHEDULE_WITH_ID:
                String id = uri.getLastPathSegment();
                String idSelection = ScheduleContract.ScheduleEntry._ID + "=?";
                String[] idArgs = new String[]{id};
                retCursor = db.query(
                        ScheduleContract.ScheduleEntry.TABLE_NAME,
                        projection,
                        idSelection,
                        idArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown query uri: " + uri);
        }

        // Notify listener cursors of changes
        if (getContext() != null) {
            retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        }
        return retCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        if (match == CODE_SCHEDULES) {
            long id = db.insert(ScheduleContract.ScheduleEntry.TABLE_NAME, null, values);
            if (id > 0) {
                returnUri = ContentUris.withAppendedId(ScheduleContract.CONTENT_URI, id);
            } else {
                throw new android.database.SQLException("Failed to insert row into " + uri);
            }
        } else {
            throw new UnsupportedOperationException("Unknown insert uri: " + uri);
        }

        // Notify resolvers of dataset change
        if (getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        switch (match) {
            case CODE_SCHEDULES:
                rowsDeleted = db.delete(ScheduleContract.ScheduleEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case CODE_SCHEDULE_WITH_ID:
                String id = uri.getLastPathSegment();
                rowsDeleted = db.delete(
                        ScheduleContract.ScheduleEntry.TABLE_NAME,
                        ScheduleContract.ScheduleEntry._ID + "=?",
                        new String[]{id}
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown delete uri: " + uri);
        }

        if (rowsDeleted != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection,
                      @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case CODE_SCHEDULES:
                rowsUpdated = db.update(ScheduleContract.ScheduleEntry.TABLE_NAME, values, selection, selectionArgs);
                break;

            case CODE_SCHEDULE_WITH_ID:
                String id = uri.getLastPathSegment();
                rowsUpdated = db.update(
                        ScheduleContract.ScheduleEntry.TABLE_NAME,
                        values,
                        ScheduleContract.ScheduleEntry._ID + "=?",
                        new String[]{id}
                );
                break;

            default:
                throw new UnsupportedOperationException("Unknown update uri: " + uri);
        }

        if (rowsUpdated != 0 && getContext() != null) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case CODE_SCHEDULES:
                return ScheduleContract.ScheduleEntry.CONTENT_TYPE;
            case CODE_SCHEDULE_WITH_ID:
                return ScheduleContract.ScheduleEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri for getType: " + uri);
        }
    }
}

