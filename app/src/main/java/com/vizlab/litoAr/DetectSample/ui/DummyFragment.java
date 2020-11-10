package com.vizlab.litoAr.DetectSample.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vizlab.litoAr.DetectSample.adapters.FilesAdapter;
import com.vizlab.litoAr.R;

import java.util.LinkedList;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class DummyFragment extends Fragment {
    private final LinkedList<String> fileList = new LinkedList<>();

    private RecyclerView recyclerView;
    private FilesAdapter adapter;

    public DummyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);

        // Put initial data into the word list.
        for (int i = 0; i < 20; i++) {
            fileList.addLast("Word " + i);
        }

        // Get a handle to the RecyclerView.
        recyclerView = view.findViewById(R.id.files_recyclerview);
        // Create an adapter and supply the data to be displayed.
        adapter = new FilesAdapter(getContext(), fileList);
        // Connect the adapter with the RecyclerView.
        recyclerView.setAdapter(adapter);
        // Give the RecyclerView a default layout manager.
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        return view;
    }
}
