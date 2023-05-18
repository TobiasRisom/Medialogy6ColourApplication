package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText; // Text field the user can edit

import java.io.FileOutputStream;
import java.io.IOException;

public class QuestionActivity extends AppCompatActivity {

    // Views
    Button button;
    EditText gender, age, country;

    // Data file
    private static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question);

        // Set up views
        button =  findViewById(R.id.continueQuestion);
        gender = findViewById(R.id.editName);
        age = findViewById(R.id.editAge);
        country = findViewById(R.id.editCountry);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
                Intent i = new Intent(QuestionActivity.this, ColorblindExplanationActivity.class);
                startActivity(i);
            }
        });
    }

    // saveData works the same as MainActivity
    public void saveData()
    {
        String genderString = gender.getText().toString();
        String ageString = age.getText().toString();
        String countryString = country.getText().toString();
        String data = "PARTICIPANT INFO\nGender: " + genderString + "\nAge: " + ageString + "\nCountry: " + countryString + "\n";
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