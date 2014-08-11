package com.example.dawidr.androidtestproject.Database.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public class WorkItemTable {

    public static final String TABLE_NAME = "WorkItem";

    public static class WorkItemColumns implements BaseColumns {
        public static final String TITLE = "title";
        public static final String CURRENT_DATE = "current_date";
        public static final String GPS_LONGITUDE = "gps_longitude";
        public static final String GPS_LATITUDE = "gps_latitude";
        public static final String TYPE = "type";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE " + WorkItemTable.TABLE_NAME + " (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(WorkItemColumns.TITLE + " TEXT, ");
        sb.append(WorkItemColumns.CURRENT_DATE + " TEXT, ");
        sb.append(WorkItemColumns.GPS_LONGITUDE + " TEXT, ");
        sb.append(WorkItemColumns.GPS_LATITUDE + " REAL, ");
        sb.append(WorkItemColumns.TYPE + " INTEGER");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkItemTable.TABLE_NAME);
        WorkItemTable.onCreate(db);
    }
}