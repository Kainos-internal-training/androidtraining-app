package com.example.dawidr.androidtestproject.Database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.dawidr.androidtestproject.Database.Tables.WorkItemTable;

public class OpenHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    private Context context;

    OpenHelper(final Context context) {
        super(context, DataConstants.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onOpen(final SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            db.execSQL("PRAGMA foreign_keys=ON;");

            Cursor c = db.rawQuery("PRAGMA foreign_keys", null);
            c.moveToFirst();
            if (!c.isClosed()) {
                c.close();
            }
        }
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {

        WorkItemTable.onCreate(db);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {

        WorkItemTable.onUpgrade(db, oldVersion, newVersion);
    }
}