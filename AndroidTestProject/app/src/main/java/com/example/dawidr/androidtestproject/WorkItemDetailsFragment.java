package com.example.dawidr.androidtestproject;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

public class WorkItemDetailsFragment extends Fragment {

    public static EditText etTitle;
    public static Spinner spTypes;

    public WorkItemDetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_work_item_details, container, false);

        etTitle = (EditText) rootView.findViewById(R.id.etTitle);
        spTypes = (Spinner) rootView.findViewById(R.id.spTypes);

        if (WorkItemActivity.workItem != null) {
            etTitle.setText(WorkItemActivity.workItem.title);
            spTypes.setSelection(WorkItemActivity.workItem.type);
        }

        return rootView;
    }
}