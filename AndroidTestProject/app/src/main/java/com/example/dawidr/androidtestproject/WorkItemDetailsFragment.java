package com.example.dawidr.androidtestproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import com.example.dawidr.androidtestproject.Database.Model.WorkItem;

public class WorkItemDetailsFragment extends Fragment {

    public EditText etTitle;
    public Spinner spTypes;
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
        spTypes = (Spinner) rootView.findViewById(R.id.spTypes);

        etTitle.setText(workItem.title);
        spTypes.setSelection(workItem.type);

        return rootView;
    }
}