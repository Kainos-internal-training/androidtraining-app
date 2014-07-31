package com.example.dawidr.androidtestproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;

public class WorkItemPhotosFragment extends Fragment {

    private WorkItem workItem = null;

    public WorkItemPhotosFragment() {
    }

    public WorkItemPhotosFragment(WorkItem w) {

        workItem = w;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_item_photos, container, false);
        return rootView;
    }
}