package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    Button clearDataButton, backButton;
    private static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        clearDataButton = findViewById(R.id.clearDataButton);
        backButton = findViewById(R.id.backButton);

        clearDataButton.setOnClickListener(view -> {
            clearData();
        });

        backButton.setOnClickListener(view -> {
            Intent i = new Intent(SettingsActivity.this, TitleActivity.class);
            startActivity(i);
        });
    }

    public void clearData()
    {
        {
            String data = "";
            FileOutputStream fos = null;

            try {
                fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write(data.getBytes());

                Toast.makeText(SettingsActivity.this, "Data cleared.", Toast.LENGTH_SHORT).show();

            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if (fos != null)
                {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}