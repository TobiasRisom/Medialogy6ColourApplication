package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import java.io.FileNotFoundException; // Exception thrown when file not found
import java.io.FileOutputStream;
import java.io.IOException;

public class SettingsActivity extends AppCompatActivity {

    // Buttons
    Button clearDataButton, backButton;

    // Data file
    private static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up views
        clearDataButton = findViewById(R.id.clearDataButton);
        backButton = findViewById(R.id.backButton);

        // Clear Data Button
        clearDataButton.setOnClickListener(view -> {
            clearData();
        });

        // Back to Title Screen
        backButton.setOnClickListener(view -> {
            Intent i = new Intent(SettingsActivity.this, TitleActivity.class);
            startActivity(i);
        });
    }

    // clearData - Clears the data stores in "data.txt", without deleting the file itself
    public void clearData()
    {
        {
            // Set data to an empty string
            String data = "";
            FileOutputStream fos = null;

            try {

                // Using "MODE_PRIVATE" to overwrite what is already written
                fos = openFileOutput(FILE_NAME, MODE_PRIVATE);
                fos.write(data.getBytes());

                // Confirmation message to the user
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