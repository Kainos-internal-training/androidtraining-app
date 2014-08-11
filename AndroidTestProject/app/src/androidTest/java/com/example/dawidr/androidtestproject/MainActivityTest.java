package com.example.dawidr.androidtestproject;

import android.app.Activity;
import android.app.Instrumentation;
import android.test.ActivityInstrumentationTestCase2;
import android.test.TouchUtils;
import android.view.View;
import android.widget.ListView;

public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mainActivity;
    private ListView list;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mainActivity = getActivity();
        list = (ListView) mainActivity.findViewById(android.R.id.list);
    }

    public void test_add_click() {
        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(WorkItemActivity.class.getName(), null, false);
        getInstrumentation().invokeMenuActionSync(mainActivity, R.id.action_new, 0);
        Activity activity = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
        assertEquals(true, getInstrumentation().checkMonitorHit(am, 1));
        activity.finish();
    }

    public void test_work_item_click() {
        Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(WorkItemActivity.class.getName(), null, false);
        View view = list.getChildAt(0);
        TouchUtils.clickView(this, view);
        Activity activity = getInstrumentation().waitForMonitorWithTimeout(am, 1000);
        assertNotNull(activity);

        activity.finish();
    }

    public void test_work_item_clicks_50() {
        for (int i = 0; i < 50; i++) {
            Instrumentation.ActivityMonitor am = getInstrumentation().addMonitor(WorkItemActivity.class.getName(), null, false);
            View view = list.getChildAt(0);
            TouchUtils.clickView(this, view);
            Activity activity = getInstrumentation().waitForMonitor(am);
            assertNotNull(activity);

            activity.finish();
        }
    }
}
