package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class TitleActivity extends AppCompatActivity {

    Button startButton, infoButton, settingsButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_title);

        // Set up start button and settings button

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