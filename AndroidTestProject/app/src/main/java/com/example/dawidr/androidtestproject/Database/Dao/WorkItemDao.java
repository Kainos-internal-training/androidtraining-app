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
            "insert into " + WorkItemTable.TABLE_NAME + "(" + WorkItemColumns.TITLE + ", " + WorkItemColumns.CURRENT_DATE + ") " +
                    "values (?, ?)";

    private SQLiteDatabase db;
    private SQLiteStatement insertStatement;

    public WorkItemDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(WorkItemDao.INSERT);
    }

    @Override
    public long save(WorkItem entity) {
        insertStatement.clearBindings();
        insertStatement.bindString(1, entity.title);
        insertStatement.bindString(2, entity.current_date);
        return insertStatement.executeInsert();
    }

    @Override
    public void update(WorkItem entity) {
        final ContentValues values = new ContentValues();
        values.put(WorkItemColumns.TITLE, entity.title);
        values.put(WorkItemColumns.CURRENT_DATE, entity.current_date);
        db.update(WorkItemTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String
                .valueOf(entity.id)});
    }

    @Override
    public void delete(WorkItem entity) {
        if (entity.id > 0) {
            db.delete(WorkItemTable.TABLE_NAME, BaseColumns._ID + " = ?", new String[]{String.valueOf(entity.id)});
        }
    }

    @Override
    public WorkItem get(long id) {
        WorkItem entity = null;
        Cursor c =
                db.query(WorkItemTable.TABLE_NAME, new String[]{BaseColumns._ID,
                                WorkItemColumns.TITLE,
                                WorkItemColumns.CURRENT_DATE},
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
                                WorkItemColumns.CURRENT_DATE},
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
        }
        return entity;
    }
}