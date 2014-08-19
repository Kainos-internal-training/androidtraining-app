package com.example.dawidr.androidtestproject;

import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;
import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.media.MediaHttpUploader;
import com.google.api.client.googleapis.media.MediaHttpUploaderProgressListener;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Locale;

public class WorkItemActivity extends Activity {

    private App app;
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private WorkItemPhotosFragment workItemPhotosFragment;
    private WorkItemDetailsFragment workItemDetailsFragment;
    private WorkItemActivity workItemActivity;

    static public WorkItem workItem;
    static final int REQUEST_ACCOUNT_PICKER = 1;
    Boolean isCreating = false;
    SharedPreferences sharedPreferences;

    GoogleAccountCredential credential;
    Drive service;

    private ProgressDialog _progressDialog = null;

    class CustomProgressListener implements MediaHttpUploaderProgressListener {

        WorkPhoto workPhoto;

        public CustomProgressListener(WorkPhoto photo) {
            workPhoto = photo;
        }

        public void progressChanged(final MediaHttpUploader uploader) throws IOException {
            switch (uploader.getUploadState()) {
                case INITIATION_STARTED:
                    break;
                case INITIATION_COMPLETE:
                    break;
                case MEDIA_IN_PROGRESS:
                    _progressDialog.setProgress((int) uploader.getProgress());
                    break;
                case MEDIA_COMPLETE:
                    workPhoto.is_uploaded = true;
                    app.dataManager.updateWorkPhoto(workPhoto);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_work_item);

        app = (App) getApplication();
        workItemActivity = this;

        final ActionBar actionBar = getActionBar();

        if (actionBar != null)
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        sharedPreferences = getSharedPreferences(App.APP_PACKAGE_NAME, MODE_PRIVATE);

//      Google Drive
        credential = GoogleAccountCredential.usingOAuth2(this, Collections.singleton(DriveScopes.DRIVE));
        String accountName = sharedPreferences.getString(App.PREFERENCES_ACCOUNT_NAME, null);
        if (accountName != null)
            ConnectToGoogleDrive(accountName);
        else
            startActivityForResult(credential.newChooseAccountIntent(), REQUEST_ACCOUNT_PICKER);

        if (this.getIntent().hasExtra("content")) {
            workItem = (WorkItem) this.getIntent().getSerializableExtra("content");
            workItem = app.dataManager.getWorkItem(workItem.id);
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

            if (workItemDetailsFragment.etTitle.getText().toString().length() == 0) {

                mViewPager.setCurrentItem(1);
                ShowToast(getString(R.string.error_title_empty));

                return false;
            }

            FillEntity(workItem);

            //Hide keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);

            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
            _progressDialog = ProgressDialog.show(this, getResources().getString(R.string.pb_google_drive_title), getResources().getString(R.string.pb_google_drive_message), true, false);

            if (this.getIntent().hasExtra("content")) {
                app.dataManager.updateWorkItem(workItem);

                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            com.google.api.services.drive.model.File fileFolderID = GoogleDriveHelper.GetFolder(service, Long.toString(workItem.id));
                            com.google.api.services.drive.model.File fileFolderPhotos = GoogleDriveHelper.GetFolder(service, getResources().getString(R.string.folder_photos), fileFolderID.getId());
                            com.google.api.services.drive.model.File fileDataForm = GoogleDriveHelper.GetFile(service, getResources().getString(R.string.file_data_form), fileFolderID.getId());

                            service.files().update(fileDataForm.getId(), fileDataForm, GoogleDriveHelper.GetTempFileContent(FormToString())).execute();

                            if (fileFolderID != null) {
                                for (WorkPhoto p : workItem.photos) {
                                    if (!p.is_uploaded)
                                        GoogleDriveHelper.UploadPhotoToGoogleDrive(service, p, fileFolderPhotos.getId(), new CustomProgressListener(p));
                                }
                            }
                            ShowToast(getString(R.string.success_upload));
                        } catch (Exception e) {
                            ShowToast(getString(R.string.error_upload));
                        } finally {
                            _progressDialog.dismiss();
                            _progressDialog = null;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            finish();
                        }
                    }
                });
                t.start();
            } else {
                app.dataManager.insertWorkItem(workItem);
                Thread t = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            com.google.api.services.drive.model.File fileFolderID = GoogleDriveHelper.CreateFolder(service, Long.toString(workItem.id));
                            com.google.api.services.drive.model.File fileFolderPhotos = GoogleDriveHelper.CreateFolder(service, getResources().getString(R.string.folder_photos), fileFolderID.getId());

                            if (fileFolderID != null && fileFolderPhotos != null) {
                                com.google.api.services.drive.model.File fileDataForm = GoogleDriveHelper.CreateFile(getResources().getString(R.string.file_data_form), fileFolderID.getId());
                                service.files().insert(fileDataForm, GoogleDriveHelper.GetTempFileContent(FormToString())).execute();

                                for (WorkPhoto p : workItem.photos) {
                                    GoogleDriveHelper.UploadPhotoToGoogleDrive(service, p, fileFolderPhotos.getId(), new CustomProgressListener(p));
                                }
                            }
                            ShowToast(getString(R.string.success_upload));
                        } catch (Exception e) {
                            ShowToast(getString(R.string.error_upload));
                        } finally {
                            _progressDialog.dismiss();
                            _progressDialog = null;
                            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
                            finish();
                        }
                    }
                });
                t.start();
            }

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void ShowToast(String message) {
        final String msg = message;
        runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(workItemActivity, msg, Toast.LENGTH_SHORT)
                        .show();
            }
        });
    }

    private String FormToString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Title: " + workItem.title + "\n");
        sb.append("Current date: " + workItem.current_date + "\n");
        sb.append("GPS Longitude: " + workItem.gps_longitude + "\n");
        sb.append("GPS Latitude: " + workItem.gps_latitude + "\n");
        sb.append("Type: " + getResources().getStringArray(R.array.type_arrays)[workItem.type] + "\n");

        return sb.toString();
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

        Location location = lm.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        if (location != null) {
            workItem.gps_longitude = location.getLongitude();
            workItem.gps_latitude = location.getLatitude();
        }

        //Type
        workItem.type = workItemDetailsFragment.spTypes.getSelectedItemPosition();
    }

    private Drive getDriveService(GoogleAccountCredential credential) {
        return new Drive.Builder(AndroidHttp.newCompatibleTransport(), new GsonFactory(), credential).build();
    }

    private void ConnectToGoogleDrive(String accountName) {
        credential.setSelectedAccountName(accountName);
        service = getDriveService(credential);
    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_ACCOUNT_PICKER:
                if (resultCode == RESULT_OK && data != null && data.getExtras() != null) {

                    String accountName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if (accountName != null) {
                        ConnectToGoogleDrive(accountName);

                        sharedPreferences.edit()
                                .putString(App.PREFERENCES_ACCOUNT_NAME, accountName)
                                .commit();
                    }
                }
                break;
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
