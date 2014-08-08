package com.example.dawidr.androidtestproject;

import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;
import com.example.dawidr.androidtestproject.Database.Model.WorkPhoto;

import java.io.File;
import java.util.List;


public class MainActivity extends ListActivity {

    private App app;
    private WorkItemsAdapter workItemsAdapter;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (App) getApplication();
        list = (ListView) findViewById(android.R.id.list);
        registerForContextMenu(list);
    }

    @Override
    protected void onResume() {
        workItemsAdapter = new WorkItemsAdapter(app.dataManager.getWorkItems());
        setListAdapter(workItemsAdapter);
        super.onResume();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);

        if (v.getId() == android.R.id.list) {

            AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) menuInfo;
            WorkItem workItem = workItemsAdapter.getItem(info.position);
            menu.setHeaderTitle(workItem.title);

            menu.add(Menu.NONE, 0, 0, getString(R.string.delete_from_list));
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        WorkItem workItem = workItemsAdapter.getItem(info.position);

        for (WorkPhoto p : workItem.photos) {
            new File(p.path).delete();
        }
        app.dataManager.deleteWorkItem(workItem);
        workItemsAdapter.remove(workItem);

        return super.onContextItemSelected(item);
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

            TextView tvTitle = (TextView) convertView.findViewById(R.id.WorkItemList_tv_Title);
            TextView tvStatus = (TextView) convertView.findViewById(R.id.WorkItemList_tv_Status);

            WorkItem item = getItem(position);

            if (item != null) {
                tvTitle.setText(item.title);

                String[] typeArrays = getResources().getStringArray(R.array.type_arrays);
                tvStatus.setText(typeArrays[item.type]);
            }

            return convertView;
        }
    }
}
