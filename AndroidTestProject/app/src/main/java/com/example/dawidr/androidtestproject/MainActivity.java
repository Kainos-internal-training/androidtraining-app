package com.example.dawidr.androidtestproject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;

import java.util.List;


public class MainActivity extends ListActivity {

    private App app;
    private WorkItemsAdapter workItemsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (App) getApplication();
    }

    @Override
    protected void onResume() {
        workItemsAdapter = new WorkItemsAdapter(app.dataManager.getWorkItems());
        setListAdapter(workItemsAdapter);
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_new) {

            Intent intent = new Intent(MainActivity.this, WorkItemActivity.class);
            startActivity(intent);

            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        WorkItem workItem = (WorkItem) l.getItemAtPosition(position);

        Intent intent = new Intent(MainActivity.this, WorkItemActivity.class);
        intent.putExtra("content", workItem);
        startActivity(intent);
    }

    private class WorkItemsAdapter extends ArrayAdapter<WorkItem> {

        public WorkItemsAdapter(List<WorkItem> items) {
            super(MainActivity.this, R.layout.list_item, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.list_item, parent, false);
            }

            TextView text = (TextView) convertView.findViewById(R.id.deal_title);
            ImageView image = (ImageView) convertView.findViewById(R.id.deal_img);

            WorkItem item = getItem(position);

            if (item != null) {
                text.setText(item.title);
            }

            return convertView;
        }
    }
}
