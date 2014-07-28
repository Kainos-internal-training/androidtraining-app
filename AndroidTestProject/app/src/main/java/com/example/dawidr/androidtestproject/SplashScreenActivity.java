package com.example.dawidr.androidtestproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class SplashScreenActivity extends Activity {

    private static int SPLASH_TIME_OUT = 3000; //milliseconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ActivityStarter starter = new ActivityStarter();
        starter.start();
    }

    private class ActivityStarter extends Thread {

        @Override
        public void run() {
            try {
                Thread.sleep(SPLASH_TIME_OUT);
            } catch (Exception e) {
                Log.e("SplashScreenActivity", e.getMessage());
            }

            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
