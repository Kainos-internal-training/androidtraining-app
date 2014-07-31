package com.example.dawidr.androidtestproject.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.SystemClock;

import com.example.dawidr.androidtestproject.Database.Dao.WorkItemDao;
import com.example.dawidr.androidtestproject.Database.Model.WorkItem;

import java.util.List;

public class DataManager {

    private Context context;

    private SQLiteDatabase db;

    private WorkItemDao workItemDao;

    public DataManager(Context context) {

        this.context = context;

        SQLiteOpenHelper openHelper = new OpenHelper(this.context);
        db = openHelper.getWritableDatabase();

        workItemDao = new WorkItemDao(db);
    }

    public SQLiteDatabase getDb() {
        return db;
    }

    private void openDb() {
        if (!db.isOpen()) {
            db = SQLiteDatabase.openDatabase(DataConstants.DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);

            workItemDao = new WorkItemDao(db);
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

    /////////////////////////////////////////////////////////////

    public WorkItem getWorkItem(long id) {
        return workItemDao.get(id);
    }

    public List<WorkItem> getWorkItems() {
        return workItemDao.getAll();
    }

    public long saveWorkItem(WorkItem entity) {
        return workItemDao.save(entity);
    }

    public void updateWorkItem(WorkItem entity) {
        workItemDao.update(entity);
    }

    public void deleteWorkItem(WorkItem entity) {
        workItemDao.delete(entity);
    }

}
