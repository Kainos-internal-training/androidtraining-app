package com.example.dawidr.androidtestproject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;

import java.util.Locale;

public class WorkItemActivity extends Activity {

    private App app;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private WorkItemPhotosFragment workItemPhotosFragment;
    private WorkItemDetailsFragment workItemDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_item);

        app = (App) getApplication();
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        if (this.getIntent().hasExtra("content")) {
            WorkItem workItem = (WorkItem) this.getIntent().getSerializableExtra("content");

            workItemPhotosFragment = new WorkItemPhotosFragment(workItem);
            workItemDetailsFragment = new WorkItemDetailsFragment(workItem);
        } else {
            workItemPhotosFragment = new WorkItemPhotosFragment();
            workItemDetailsFragment = new WorkItemDetailsFragment();
        }

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        actionBar.setSelectedNavigationItem(position);
                    }
                }
        );

        ActionBar.TabListener tabListener = new ActionBar.TabListener() {
            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

            }
        };

        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
            actionBar.addTab(
                    actionBar.newTab()
                            .setText(mSectionsPagerAdapter.getPageTitle(i))
                            .setTabListener(tabListener)
            );
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.work_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_save) {

            if (this.getIntent().hasExtra("content")) {
                WorkItem workItem = (WorkItem) this.getIntent().getSerializableExtra("content");
                FillEntityFromLayout(workItem);

                app.dataManager.updateWorkItem(workItem);
            } else {
                WorkItem workItem = new WorkItem();
                FillEntityFromLayout(workItem);

                app.dataManager.saveWorkItem(workItem);
            }

            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    void FillEntityFromLayout(WorkItem workItem) {
        workItem.title = workItemDetailsFragment.etTitle.getText().toString();
        workItem.current_date = workItemDetailsFragment.etCurrentDate.getText().toString();
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return workItemPhotosFragment;
                case 1:
                    return workItemDetailsFragment;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section_photos).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section_details).toUpperCase(l);
            }
            return null;
        }
    }
}
