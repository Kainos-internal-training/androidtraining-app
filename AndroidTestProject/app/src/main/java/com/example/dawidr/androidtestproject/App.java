package com.example.dawidr.androidtestproject;

import android.app.Application;

import com.example.dawidr.androidtestproject.Database.DataManager;

public class App extends Application {

    public static final String APP_PACKAGE_NAME = "com.example.dawidr.androidtestproject";
    public DataManager dataManager;

    @Override
    public void onCreate() {

        dataManager = new DataManager(this);

        super.onCreate();
    }
}
