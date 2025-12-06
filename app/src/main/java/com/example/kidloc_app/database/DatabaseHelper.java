package com.example.kidloc_app.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "kidloc.db";
    private static final int DATABASE_VERSION = 1;

    // Location logs table
    private static final String TABLE_LOGS = "location_logs";
    private static final String COL_ID = "id";
    private static final String COL_LATITUDE = "latitude";
    private static final String COL_LONGITUDE = "longitude";
    private static final String COL_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create location logs table
        String createLogs = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGS + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_LATITUDE + " TEXT, " +
                COL_LONGITUDE + " TEXT, " +
                COL_TIMESTAMP + " TEXT)";
        db.execSQL(createLogs);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For now, drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LOGS);
        onCreate(db);
    }

    // Method to save location log
    public void saveLocationLog(String latitude, String longitude, String timestamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COL_LATITUDE, latitude);
        values.put(COL_LONGITUDE, longitude);
        values.put(COL_TIMESTAMP, timestamp);
        db.insert(TABLE_LOGS, null, values);
        db.close();
    }

    // Method to retrieve logs
    public List<LocationLog> getAllLocationLogs() {
        List<LocationLog> logs = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_LOGS, null, null, null, null, null, COL_ID + " DESC");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String lat = cursor.getString(cursor.getColumnIndexOrThrow(COL_LATITUDE));
                String lon = cursor.getString(cursor.getColumnIndexOrThrow(COL_LONGITUDE));
                String time = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIMESTAMP));
                logs.add(new LocationLog(lat, lon, time));
            }
            cursor.close();
        }
        db.close();
        return logs;
    }
}
