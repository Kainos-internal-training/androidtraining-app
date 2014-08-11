package com.example.dawidr.androidtestproject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;
import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Contents;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.MetadataChangeSet;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class WorkItemActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private App app;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private WorkItemPhotosFragment workItemPhotosFragment;
    private WorkItemDetailsFragment workItemDetailsFragment;

    static public WorkItem workItem;
    static final int RESOLVE_CONNECTION_REQUEST_CODE = 0;
    static final int REQUEST_CODE_RESOLUTION = 3;
    Boolean isCreating = false;

    GoogleApiClient mGoogleApiClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_item);

        app = (App) getApplication();

        final ActionBar actionBar = getActionBar();

        if (actionBar != null)
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        if (this.getIntent().hasExtra("content")) {
            workItem = (WorkItem) this.getIntent().getSerializableExtra("content");
            isCreating = false;
        } else {
            workItem = new WorkItem();
            workItem.photos = new ArrayList<WorkPhoto>();
            isCreating = true;
        }

        workItemPhotosFragment = new WorkItemPhotosFragment();
        workItemDetailsFragment = new WorkItemDetailsFragment();

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
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable("content", workItem);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        workItem = (WorkItem) savedInstanceState.getSerializable("content");
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

            FillEntity(workItem);
            if (this.getIntent().hasExtra("content")) {
                app.dataManager.updateWorkItem(workItem);
            } else {
                app.dataManager.insertWorkItem(workItem);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (mGoogleApiClient.isConnected()) {
                            for (WorkPhoto p : workItem.photos) {
                                mGoogleApiClient.blockingConnect(5, TimeUnit.SECONDS);
                                saveFileToDrive(p.path);
                            }
                        }
                    }
                });
                t.start();
            }
            finish();

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (isCreating) {
            for (WorkPhoto p : workItem.photos) {
                new File(p.path).delete();
            }
        }
    }

    void FillEntity(WorkItem workItem) {
        workItem.title = workItemDetailsFragment.etTitle.getText().toString();

        //Current date
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        workItem.current_date = df.format(Calendar.getInstance().getTime());

        //GPS coordinates
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
            workItem.gps_longitude = location.getLongitude();
            workItem.gps_latitude = location.getLatitude();
        }

        //Type
        workItem.type = workItemDetailsFragment.spTypes.getSelectedItemPosition();
    }

    private void saveFileToDrive(String path) {
        final String pathSSS = path;
        Drive.DriveApi.newContents(mGoogleApiClient)
                .setResultCallback(new ResultCallback<DriveApi.ContentsResult>() {
                    @Override
                    public void onResult(DriveApi.ContentsResult contentsResult) {

                        Contents contents = contentsResult.getContents();

                        byte[] buf = new byte[1024];
                        int nbyteWritten = 0;
                        int nbyte;
                        OutputStream outputStream = contentsResult.getContents().getOutputStream();
                        try {
                            DataInputStream dis = new DataInputStream(new FileInputStream(new File(pathSSS)));
                            DataOutputStream dos = new DataOutputStream(outputStream);
                            while ((nbyte = dis.read(buf)) > 0) {
                                dos.write(buf, 0, nbyte);
                                nbyteWritten += nbyte;
                            }
                            dis.close();
                            dos.close();

                            MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                                    .setMimeType("image/jpeg")
                                    .setTitle(pathSSS)
                                    .build();

                            Drive.DriveApi.getRootFolder(mGoogleApiClient)
                                    .createFile(mGoogleApiClient, changeSet, contents)
                                    .setResultCallback(new ResultCallback<DriveFolder.DriveFileResult>() {
                                        @Override
                                        public void onResult(DriveFolder.DriveFileResult driveFileResult) {

                                        }
                                    });
                        } catch (IOException e1) {
                            Context context = getApplicationContext();
                            CharSequence text = String.format(getString(R.string.error_upload_photo), pathSSS);
                            int duration = Toast.LENGTH_SHORT;

                            Toast toast = Toast.makeText(context, text, duration);
                            toast.show();
                        }
                    }
                });
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case RESOLVE_CONNECTION_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    mGoogleApiClient.connect();
                }
                break;
        }
    }

    void ConnectGoogleDrive() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addScope(Drive.SCOPE_APPFOLDER)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ConnectGoogleDrive();
    }

    @Override
    protected void onPause() {
        if (mGoogleApiClient != null) {
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    @Override
    public void onConnected(Bundle bundle) {
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {

        if (!result.hasResolution()) {

            GooglePlayServicesUtil.getErrorDialog(result.getErrorCode(), this, 0).show();
            return;
        }

        try {
            result.startResolutionForResult(this, REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
        }
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
