package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

public class ColorblindActivity extends AppCompatActivity {

    Button answer1, answer2, answer3;
    ImageView plateDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorblind);

        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        plateDisplay = findViewById(R.id.ishiharaDisplay);

        plateDisplay.setImageResource(R.drawable.plate1);
    }
}