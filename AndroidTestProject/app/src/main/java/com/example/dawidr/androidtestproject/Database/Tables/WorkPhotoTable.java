package com.example.dawidr.androidtestproject.Database.Tables;

import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

public final class WorkPhotoTable {

    public static final String TABLE_NAME = "WorkPhoto";

    public static class WorkPhotoColumns {
        public static final String WORK_ITEM_ID = "work_item_id";
        public static final String PATH = "path";
    }

    public static void onCreate(SQLiteDatabase db) {
        StringBuilder sb = new StringBuilder();

        sb.append("CREATE TABLE " + WorkPhotoTable.TABLE_NAME + " (");
        sb.append(BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, ");
        sb.append(WorkPhotoColumns.WORK_ITEM_ID + " INTEGER NOT NULL, ");
        sb.append(WorkPhotoColumns.PATH + " TEXT, ");
        sb.append("FOREIGN KEY(" + WorkPhotoColumns.WORK_ITEM_ID + ") REFERENCES " + WorkItemTable.TABLE_NAME + "("
                + BaseColumns._ID + ")");
        sb.append(");");
        db.execSQL(sb.toString());
    }

    public static void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + WorkPhotoTable.TABLE_NAME);
        WorkPhotoTable.onCreate(db);
    }
}