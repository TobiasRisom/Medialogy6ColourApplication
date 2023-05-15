package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.icu.text.CaseMap;
import android.icu.text.IDNA;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

public class TitleActivity extends AppCompatActivity {

    Button startButton, infoButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        startButton = findViewById(R.id.StartButton);
        settingsButton = findViewById(R.id.Settings);
        startButton.setOnClickListener(view -> {
            Intent i = new Intent(TitleActivity.this, InfoScreen.class);
            startActivity(i);
        });

        settingsButton.setOnClickListener(view -> {
            Intent i = new Intent(TitleActivity.this, SettingsActivity.class);
            startActivity(i);
        });
    }
}