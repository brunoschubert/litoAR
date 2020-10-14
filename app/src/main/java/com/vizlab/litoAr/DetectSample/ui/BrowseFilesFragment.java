package com.vizlab.litoAr.DetectSample.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vizlab.litoAr.R;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class BrowseFilesFragment extends Fragment {

    public BrowseFilesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_browse_files, container, false);



        return view;
    }
}