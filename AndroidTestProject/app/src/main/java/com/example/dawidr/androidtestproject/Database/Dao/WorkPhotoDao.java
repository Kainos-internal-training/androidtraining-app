package com.example.dawidr.androidtestproject.Database.Dao;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.provider.BaseColumns;

import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;
import com.example.dawidr.androidtestproject.Database.Tables.WorkPhotoTable;
import com.example.dawidr.androidtestproject.Database.Tables.WorkPhotoTable.WorkPhotoColumns;

import java.util.ArrayList;
import java.util.List;

public class WorkPhotoDao {

    private static final String INSERT =
            "insert into " + WorkPhotoTable.TABLE_NAME + "(" + WorkPhotoColumns.WORK_ITEM_ID + ", "
                    + WorkPhotoColumns.PATH + ", " + WorkPhotoColumns.NAME + ", " + WorkPhotoColumns.IS_UPLOADED + ") values (?, ?, ?, ?)";

    private final SQLiteDatabase db;
    private final SQLiteStatement insertStatement;

    public WorkPhotoDao(SQLiteDatabase db) {
        this.db = db;
        insertStatement = db.compileStatement(WorkPhotoDao.INSERT);
    }

    public long save(WorkPhoto entity, long work_item_id) {
        insertStatement.clearBindings();
        insertStatement.bindLong(1, work_item_id);
        insertStatement.bindString(2, entity.path);
        insertStatement.bindString(3, entity.name);
        insertStatement.bindLong(4, entity.is_uploaded ? 1 : 0);
        return insertStatement.executeInsert();
    }

    public void update(WorkPhoto entity) {
        final ContentValues values = new ContentValues();
        values.put(WorkPhotoColumns.PATH, entity.path);
        values.put(WorkPhotoColumns.NAME, entity.name);
        values.put(WorkPhotoColumns.IS_UPLOADED, entity.is_uploaded ? 1 : 0);
        db.update(WorkPhotoTable.TABLE_NAME, values, BaseColumns._ID + " = ?", new String[]{String
                .valueOf(entity.id)});
    }

    public void deleteAll(long work_item_id) {
        db.delete(WorkPhotoTable.TABLE_NAME, WorkPhotoColumns.WORK_ITEM_ID + " = ?", new String[]{String.valueOf(work_item_id)});
    }

    public List<WorkPhoto> getWorkPhotos(long work_item_id) {
        List<WorkPhoto> list = new ArrayList<WorkPhoto>();
        String sql =
                "select " + BaseColumns._ID + ", " + WorkPhotoColumns.PATH + ", " + WorkPhotoColumns.NAME + ", " + WorkPhotoColumns.IS_UPLOADED + " from "
                        + WorkPhotoTable.TABLE_NAME + " where "
                        + WorkPhotoColumns.WORK_ITEM_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(work_item_id)});
        if (c.moveToFirst()) {
            do {
                WorkPhoto entity = new WorkPhoto();
                entity.id = c.getLong(0);
                entity.path = c.getString(1);
                entity.name = c.getString(2);
                entity.is_uploaded = c.getLong(3) == 0 ? false : true;

                list.add(entity);
            } while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return list;
    }
}