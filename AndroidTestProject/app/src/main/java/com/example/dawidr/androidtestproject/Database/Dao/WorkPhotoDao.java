package com.example.dawidr.androidtestproject.Database.Dao;

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
                    + WorkPhotoColumns.PATH + ") values (?, ?)";

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
        return insertStatement.executeInsert();
    }

    public void deleteAll(long work_item_id) {
        db.delete(WorkPhotoTable.TABLE_NAME, WorkPhotoColumns.WORK_ITEM_ID + " = ?", new String[]{String.valueOf(work_item_id)});
    }

    public List<WorkPhoto> getWorkPhotos(long work_item_id) {
        List<WorkPhoto> list = new ArrayList<WorkPhoto>();
        String sql =
                "select " + BaseColumns._ID + ", " + WorkPhotoColumns.PATH + " from "
                        + WorkPhotoTable.TABLE_NAME + " where "
                        + WorkPhotoColumns.WORK_ITEM_ID + " = ?";
        Cursor c = db.rawQuery(sql, new String[]{String.valueOf(work_item_id)});
        if (c.moveToFirst()) {
            do {
                WorkPhoto entity = new WorkPhoto();
                entity.id = c.getLong(0);
                entity.path = c.getString(1);

                list.add(entity);
            } while (c.moveToNext());
        }
        if (!c.isClosed()) {
            c.close();
        }
        return list;
    }
}