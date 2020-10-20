package com.vizlab.litoAr.DetectSample.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.vizlab.litoAr.R;

import java.io.File;

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

        TextView sampleName = view.findViewById(R.id.toolbar_title);
        TextView sampleDescription = view.findViewById(R.id.description_title);
        TextView fileList = view.findViewById(R.id.file_list);

        sampleName.setText(getArguments().getString("name"));
        sampleDescription.setText(getArguments().getString("description"));

        File sampleFolder = new File(getArguments().getString("path"));
        File[] subFolders = sampleFolder.listFiles();

        String folders = "Files encountered: ";
        for (int i = 0; i < subFolders.length; ++i) {
            folders = folders + subFolders[i].getName() + "\n";
        }

        fileList.setText(folders);

        return view;
    }
}