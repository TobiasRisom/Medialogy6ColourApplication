package com.example.medialogy6colourapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class ColorblindActivity extends AppCompatActivity {

    // Views
    Button answer1, answer2, answer3;
    ImageView plateDisplay;

    // Answer key, user answers and current question
    int[] answerKey = {1,2,2,1,1,3};
    int[] answers = {0,0,0,0,0,0};
    int question = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_colorblind);

        // Views
        answer1 = findViewById(R.id.answer1);
        answer2 = findViewById(R.id.answer2);
        answer3 = findViewById(R.id.answer3);
        plateDisplay = findViewById(R.id.ishiharaDisplay);

        // Show the first plate and first round of answers
        plateDisplay.setImageResource(R.drawable.plate1);
        answer1.setText("12");
        answer2.setText("78");
        answer3.setText("I do not see a number");

        // Button listeners
        answer1.setOnClickListener(view -> {
            updateQuestion(1);
        });

        answer2.setOnClickListener(view -> {
            updateQuestion(2);
        });

        answer3.setOnClickListener(view -> {
            updateQuestion(3);
        });
    }

    // updateQuestion - Updates the plate and answers, and moves on when the test is done
    void updateQuestion(int answer)
    {
        answers[question - 1] = answer;
        question++;
        switch(question)
        {
            case 2:
                plateDisplay.setImageResource(R.drawable.plate2);
                answer1.setText("3");
                answer2.setText("8");
                break;
            case 3:
                plateDisplay.setImageResource(R.drawable.plate3);
                answer1.setText("17");
                answer2.setText("15");
                break;
            case 4:
                plateDisplay.setImageResource(R.drawable.plate4);
                answer1.setText("45");
                answer2.setText("33");
                break;
            case 5:
                plateDisplay.setImageResource(R.drawable.plate5);
                answer1.setText("7");
                answer2.setText("2");
                break;
            case 6:
                plateDisplay.setImageResource(R.drawable.plate6);
                answer1.setText("5");
                answer2.setText("74");
                break;
            default:
                Intent i = new Intent(ColorblindActivity.this, ColorblindResultsActivity.class);
                i.putExtra("result", calculateResult());
                startActivity(i);
                break;
        }
    }

    // calculateResult - Calculates the result of the colorblind test
    int calculateResult()
    {
        int results = 0; // Results: 0 = Normal Vision, 1 = Colorblind, 2 = Total Colorblindness

        // If the participant fails to see the first image, they might suffer from total colorblindness
        if(answers[0] != 1)
        {
            return 2;
        }

        for (int i = 0; i < 6; i++)
            {
                // If any question is wrong, the participant might have some level of colorblindness
                // Otherwise, they have normal color vision
                if(answers[i] != answerKey[i])
                {
                    results = 1;
                }
            }
        return results;
    }
}