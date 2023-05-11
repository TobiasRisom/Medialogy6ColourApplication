package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class ColorblindExplanationActivity extends AppCompatActivity {

    Button start, colorblind, fullCB;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorblind_explanation);

        start = findViewById(R.id.startButton);
        colorblind = findViewById(R.id.colorblindButton);
        fullCB = findViewById(R.id.fullCBButton);

        start.setOnClickListener(view -> {
            Intent i = new Intent(ColorblindExplanationActivity.this, ColorblindActivity.class);
            startActivity(i);
        });

        colorblind.setOnClickListener(view -> {
            Intent i = new Intent(ColorblindExplanationActivity.this, ColorblindResultsActivity.class);
            i.putExtra("result", 1);
            startActivity(i);
        });

        fullCB.setOnClickListener(view -> {
            Intent i = new Intent(ColorblindExplanationActivity.this, ColorblindResultsActivity.class);
            i.putExtra("result", 2);
            startActivity(i);
        });
    }
}