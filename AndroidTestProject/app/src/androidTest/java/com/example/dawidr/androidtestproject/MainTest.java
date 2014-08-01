package com.example.dawidr.androidtestproject;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;

public class MainTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;

    public MainTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mainActivity = getActivity();
    }

    public void test_add_click() {

        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(WorkItemActivity.class.getName(), null, false);
        getInstrumentation().invokeMenuActionSync(mainActivity, R.id.action_new, 0);
        Activity activity = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
        assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
        activity.finish();
    }
}
