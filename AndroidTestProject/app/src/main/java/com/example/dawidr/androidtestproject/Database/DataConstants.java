package com.example.dawidr.androidtestproject.Database;

import android.os.Environment;

public class DataConstants {

    private static final String APP_PACKAGE_NAME = "com.example.dawidr.androidtestproject";

    private static final String EXTERNAL_DATA_DIR_NAME = "test_data";

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "TestProject.db";
    public static final String DATABASE_PATH =
            Environment.getDataDirectory() + "/data/" + DataConstants.APP_PACKAGE_NAME + "/databases/"
                    + DataConstants.DATABASE_NAME;

    private DataConstants() {
    }
}
