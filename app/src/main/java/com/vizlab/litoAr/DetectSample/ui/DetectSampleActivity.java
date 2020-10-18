package com.vizlab.litoAr.DetectSample.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.vizlab.litoAr.DetectSample.utils.FullScreenUtils;
import com.vizlab.litoAr.R;

public class DetectSampleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detect_sample);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        FullScreenUtils.setFullScreenOnWindowFocusChanged(this, hasFocus);
    }
}