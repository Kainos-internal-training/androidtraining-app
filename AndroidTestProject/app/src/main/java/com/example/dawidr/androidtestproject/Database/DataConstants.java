package com.example.dawidr.androidtestproject.Database;

import android.os.Environment;

import com.example.dawidr.androidtestproject.App;

public class DataConstants {

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TestProject.db";
    public static final String DATABASE_PATH = Environment.getDataDirectory() + "/data/" + App.APP_PACKAGE_NAME + "/databases/" + DataConstants.DATABASE_NAME;

    private DataConstants() {
    }
}
