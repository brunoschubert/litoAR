package com.vizlab.litoAr;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.vizlab.litoAr.DetectSample.ui.DetectSampleActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button launch_detect = (Button) findViewById(R.id.btn_launch_detection);
        launch_detect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(view.getContext(), DetectSampleActivity.class);
                view.getContext().startActivity(intent);}
        });
    }
}