package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html; // HTML-styled text
import android.widget.Button;
import android.widget.TextView;

import java.io.FileOutputStream;
import java.io.IOException;

public class ColorblindResultsActivity extends AppCompatActivity {

    // Views
    TextView resultText;
    Button continueButton, redoButton;

    // Variables
    String source;
    int result = 1;
    private static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorblind_results);

        // Set up views
        resultText = findViewById(R.id.CBResults);

        continueButton = findViewById(R.id.moveOnButton);
        redoButton = findViewById(R.id.RedoTestButton);

        // Get results data from the ColorblindActivity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            result = extras.getInt("result");
        }

        switch(result)
        {
            // Normal Color Vision
            case 0:
                source = "The results of the test indicate that you have <b>normal color vision</b>.";
                resultText.setText(Html.fromHtml(source));
                saveData("None");
                break;

            // Some level of colorblindness
            case 1:
                source = "The results of the test indicate that you are <b>some level of colorblind</b>.";
                resultText.setText(Html.fromHtml(source));
                saveData("Some Colorblindness");
                break;

            // Total Colorblindness
            case 2:
                source = "The results of the test indicate that you suffer from <b>total colorblindness</b>.";
                resultText.setText(Html.fromHtml(source));
                saveData("Total Colorblindness");
                break;
            // Failsafe in case of errors
            default:
                source = "An error happened. Please try the test again.";
                resultText.setText(Html.fromHtml(source));
                break;
        }

        continueButton.setOnClickListener(view -> {
            Intent i = new Intent(ColorblindResultsActivity.this, CameraActivity.class);
            startActivity(i);
        });

        redoButton.setOnClickListener(view -> {
            Intent i = new Intent(ColorblindResultsActivity.this, ColorblindExplanationActivity.class);
            startActivity(i);
        });
    }

    // Same saveData function as found in MainActivity
    public void saveData(String result)
    {
        String data = "Colorblindness: " + result + "\n\n";
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(data.getBytes());

            System.out.println("Saved to " + getFilesDir() + "/" + FILE_NAME);

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