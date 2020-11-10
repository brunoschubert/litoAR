package com.vizlab.litoAr.DetectSample.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.vizlab.litoAr.R;

import java.util.LinkedList;

public class FilesAdapter extends
        RecyclerView.Adapter<FilesAdapter.FileViewHolder>  {

    private final LinkedList<String> fileList;
    private LayoutInflater inflater;

    @NonNull
    @Override
    public FilesAdapter.FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = inflater.inflate(R.layout.filelist_item,
                parent, false);
        return new FileViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(@NonNull FilesAdapter.FileViewHolder holder, int position) {
        String currentFile = fileList.get(position);
        holder.fileItemView.setText(currentFile);
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public FilesAdapter(Context context, LinkedList<String> wordList) {
        inflater = LayoutInflater.from(context);
        this.fileList = wordList;
    }

    class FileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView fileItemView;
        final FilesAdapter adapter;

        public FileViewHolder(View itemView, FilesAdapter adapter) {
            super(itemView);
            this.fileItemView = itemView.findViewById(R.id.file);
            this.adapter = adapter;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            // Get the position of the item that was clicked.
            int mPosition = getLayoutPosition();
            // Use that to access the affected item in mWordList.
            String element = fileList.get(mPosition);
            // Change the word in the mWordList.
            fileList.set(mPosition, "Clicked! " + element);
            // Notify the adapter, that the data has changed so it can
            // update the RecyclerView to display the data.
            adapter.notifyDataSetChanged();
        }
    }
}