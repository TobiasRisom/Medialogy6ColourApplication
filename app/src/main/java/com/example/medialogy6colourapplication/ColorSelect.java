package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast; // Pop-up notifications

public class ColorSelect extends AppCompatActivity {

    // Views
    Button[] buttonGroup = new Button[3];
    Button goBack;

    boolean[] buttonActive = {true, true, true};  // Button states
    float[][] YxyCoordinates = { {0.2f, 0.4f, 0.4f}, {0.2f, 0.35f, 0.42f}, {0.2f, 0.3f, 0.3f} }; // Test colours in Yxy-coordinates
    private static final String FILE_NAME = "some_data.txt";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);

        // Set up Views
        buttonSetup();
        goBack = findViewById(R.id.backToMain);

        // Get information from MainActivity about the state of the tests
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int ID = extras.getInt("ID");
            buttonActive = extras.getBooleanArray("array");
            buttonActive[ID] = false;
        }

        // Any already completed color tests get a ✔ to indicate that they have been done
        for(int i = 0; i < buttonGroup.length; i++)
        {
            if(buttonActive[i] == false)
            {
                buttonGroup[i].setText("✔");
                buttonGroup[i].setTextSize(30f);
            }
        }



        buttonGroup[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // If button is active, go to MainActivity and test that colour
                if(buttonActive[0] == true) {
                    float[] Yxy = {YxyCoordinates[0][0], YxyCoordinates[0][1], YxyCoordinates[0][2]};
                    Intent i = new Intent(ColorSelect.this, MainActivity.class);
                    i.putExtra("values", Yxy);
                    i.putExtra("button", 0);
                    i.putExtra("array", buttonActive);
                    startActivity(i);
                }
                else
                {
                    // Inform the user that they have already tested that colour
                    Toast.makeText(ColorSelect.this, "Level already done!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonGroup[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonActive[1] == true) {
                    float[] Yxy = {YxyCoordinates[1][0], YxyCoordinates[1][1], YxyCoordinates[1][2]};
                    Intent i = new Intent(ColorSelect.this, MainActivity.class);
                    i.putExtra("values", Yxy);
                    i.putExtra("button", 1);
                    i.putExtra("array", buttonActive);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(ColorSelect.this, "Level already done!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        buttonGroup[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(buttonActive[2] == true) {
                float[] Yxy = {YxyCoordinates[2][0], YxyCoordinates[2][1], YxyCoordinates[2][2]};
                Intent i = new Intent(ColorSelect.this, MainActivity.class);
                i.putExtra("values", Yxy);
                i.putExtra("button", 2);
                i.putExtra("array", buttonActive);
                startActivity(i);
                }
                    else
                {
                    Toast.makeText(ColorSelect.this, "Level already done!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Back to main menu
                Intent i = new Intent(ColorSelect.this, TitleActivity.class);
                startActivity(i);
            }
        });
    }

    // Same converter as found in MainActivity
    int[] YxyTosRGB(float Y, float x_coordinate, float y_coordinate){


        float X = (Y / y_coordinate) * x_coordinate;
        float Z = (Y / y_coordinate) * (1 - x_coordinate - y_coordinate);

        float a[][]={{3.2404542f,-1.5371385f,-0.4985314f},
                {-0.9692660f,1.8760108f,0.0415560f},
                {0.0556434f,-0.2040259f,1.0572252f}};

        float b[]={X,Y,Z};

        float c[]=new float[3];

        for(int i = 0; i < 3; i++){
            float temp = 0;
            for(int j = 0; j < 3; j++){
                temp += a[i][j] * b[j];
            }
            c[i] = temp;
        }

        int RC = Math.round(c[0] * 255);
        int GC = Math.round(c[1] * 255);
        int BC = Math.round(c[2] * 255);

        int[] sRGB = {RC, GC, BC};
        return(sRGB);
    }

    // buttonSetup - Sets up the buttons for selecting a colour
    void buttonSetup()
    {
        for(int i = 0; i < buttonGroup.length; i++)
        {
            switch(i)
            {
                case 0:
                    buttonGroup[i] = findViewById(R.id.level1);
                    break;
                case 1:
                    buttonGroup[i] = findViewById(R.id.level2);
                    break;
                case 2:
                    buttonGroup[i] = findViewById(R.id.level3);
                    break;
            }

            // Set the buttons colour to match the test colour center
            Drawable background = buttonGroup[i].getBackground();
            int[] colors = YxyTosRGB(YxyCoordinates[i][0], YxyCoordinates[i][1], YxyCoordinates[i][2]);
            background.setColorFilter((Color.rgb(colors[0], colors[1], colors[2])), PorterDuff.Mode.SRC_IN);
        }
    }
}