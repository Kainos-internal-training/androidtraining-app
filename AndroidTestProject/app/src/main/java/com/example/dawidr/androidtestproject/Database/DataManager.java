package com.example.dawidr.androidtestproject.Database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

import com.example.dawidr.androidtestproject.Database.Dao.WorkItemDao;
import com.example.dawidr.androidtestproject.Database.Dao.WorkPhotoDao;
import com.example.dawidr.androidtestproject.Database.Model.WorkItem;
import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;

import java.util.List;

public class DataManager {

    private final Context context;
    private SQLiteDatabase db;
    private WorkItemDao workItemDao;
    private WorkPhotoDao workPhotoDao;

    public DataManager(Context context) {

        this.context = context;

        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();

        workItemDao = new WorkItemDao(db);
        workPhotoDao = new WorkPhotoDao(db);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private void openDb() {
        if (!db.isOpen()) {
            db = SQLiteDatabase.openDatabase(DataConstants.DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);

            workItemDao = new WorkItemDao(db);
            workPhotoDao = new WorkPhotoDao(db);
        }
    }

    private void closeDb() {
        if (db.isOpen()) {
            db.close();
        }
    }

    private void resetDb() {
        closeDb();
        SystemClock.sleep(500);
        openDb();
    }

    public WorkItem getWorkItem(long id) {
        return workItemDao.get(id);
    }

    public List<WorkItem> getWorkItems() {

        List<WorkItem> list = workItemDao.getAll();
        for (WorkItem w : list) {
            w.photos = workPhotoDao.getWorkPhotos(w.id);
        }

        return list;
    }

    public long insertWorkItem(WorkItem entity) {

        long work_item_id = 0L;

        try {
            db.beginTransaction();

            work_item_id = workItemDao.save(entity);

            for (WorkPhoto w : entity.photos) {
                workPhotoDao.save(w, work_item_id);
            }

            db.setTransactionSuccessful();
        } catch (SQLException e) {
            work_item_id = 0L;
        } finally {
            db.endTransaction();
        }

        return work_item_id;
    }

    public void updateWorkItem(WorkItem entity) {

        try {
            db.beginTransaction();

            workPhotoDao.deleteAll(entity.id);

            for (WorkPhoto w : entity.photos) {
                workPhotoDao.save(w, entity.id);
            }

            workItemDao.update(entity);

            db.setTransactionSuccessful();

        } catch (SQLException ignored) {
        } finally {
            db.endTransaction();
        }
    }

    public void deleteWorkItem(WorkItem entity) {

        workPhotoDao.deleteAll(entity.id);
        workItemDao.delete(entity);
    }

}
