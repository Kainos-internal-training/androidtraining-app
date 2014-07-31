package com.example.dawidr.androidtestproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;

public class WorkItemDetailsFragment extends Fragment {

    public EditText etTitle;
    public EditText etCurrentDate;
    private WorkItem workItem = null;

    public WorkItemDetailsFragment() {
    }

    public WorkItemDetailsFragment(WorkItem w) {

        workItem = w;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_item_details, container, false);

        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        etCurrentDate = (EditText) rootView.findViewById(R.id.etCurrentDate);

        if (workItem != null) {

            etTitle.setText(workItem.title);
            etCurrentDate.setText(workItem.current_date);

            workItem = null;
        }

        return rootView;
    }
}