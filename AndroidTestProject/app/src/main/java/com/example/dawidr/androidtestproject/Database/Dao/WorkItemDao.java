package com.example.dawidr.androidtestproject.Database.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;
import com.example.dawidr.androidtestproject.Database.Tables.WorkItemTable;
import com.example.dawidr.androidtestproject.Database.Tables.WorkItemTable.WorkItemColumns;

import java.util.ArrayList;
import java.util.List;

public class WorkItemDao implements Dao<WorkItem> {

    private static final String INSERT =
            "insert into " + WorkItemTable.TABLE_NAME + "(" + WorkItemColumns.TITLE + ", " + WorkItemColumns.CURRENT_DATE
                    + ", " + WorkItemColumns.GPS_LONGITUDE + ", " + WorkItemColumns.GPS_LATITUDE + ", " + WorkItemColumns.TYPE + ") " +
                    "values (?, ?, ?, ?, ?)";

    private final SQLiteDatabase db;
    private final SQLiteStatement insertStatement;

    public WorkItemDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(WorkItemDao.INSERT);
    }

    @Override
    public long save(WorkItem entity) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, entity.title);
        insertStatement.bindString(2, entity.current_date);
        insertStatement.bindDouble(3, entity.gps_longitude);
        insertStatement.bindDouble(4, entity.gps_latitude);
        insertStatement.bindLong(5, entity.type);
        return insertStatement.executeInsert();
    }

    @Override
    public void update(WorkItem entity) {
        final ContentValues values = new ContentValues();
        values.put(WorkItemColumns.TITLE, entity.title);
        values.put(WorkItemColumns.CURRENT_DATE, entity.current_date);
        values.put(WorkItemColumns.GPS_LONGITUDE, entity.gps_longitude);
        values.put(WorkItemColumns.GPS_LATITUDE, entity.gps_latitude);
        values.put(WorkItemColumns.TYPE, entity.type);
        db.update(WorkItemTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String
                .valueOf(entity.id)});
    }

    @Override
    public void delete(WorkItem entity) {
        db.delete(WorkItemTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.id)});
    }

    @Override
    public WorkItem get(long id) {
        WorkItem entity = null;
        Cursor c =
                db.query(WorkItemTable.TABLE_NAME, new String[]{BaseColumns._ID,
                                WorkItemColumns.TITLE,
                                WorkItemColumns.CURRENT_DATE,
                                WorkItemColumns.GPS_LONGITUDE,
                                WorkItemColumns.GPS_LATITUDE,
                                WorkItemColumns.TYPE},
                        BaseColumns._ID + " = ?", new String[]{String.valueOf(id)}, null, null, null, "1"
                );
        if (c.moveToFirst()) {
            entity = this.buildFromCursor(c);
        }
        if (!c.isClosed()) {
            c.close();
        }
        return entity;
    }

    @Override
    public List<WorkItem> getAll() {
        List<WorkItem> list = new ArrayList<WorkItem>();
        Cursor c =
                db.query(WorkItemTable.TABLE_NAME, new String[]{BaseColumns._ID,
                                WorkItemColumns.TITLE,
                                WorkItemColumns.CURRENT_DATE,
                                WorkItemColumns.GPS_LONGITUDE,
                                WorkItemColumns.GPS_LATITUDE,
                                WorkItemColumns.TYPE},
                        null, null, null, null, WorkItemColumns.TITLE, null
                );
        if (c.moveToFirst()) {
            do {
                WorkItem entity = this.buildFromCursor(c);
                if (entity != null) {
                    list.add(entity);
                }
            } while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return list;
    }

    private WorkItem buildFromCursor(Cursor c) {
        WorkItem entity = null;
        if (c != null) {
            entity = new WorkItem();
            entity.id = c.getLong(0);
            entity.title = c.getString(1);
            entity.current_date = c.getString(2);
            entity.gps_longitude = c.getDouble(3);
            entity.gps_latitude = c.getDouble(4);
            entity.type = c.getInt(5);
        }
        return entity;
    }
}