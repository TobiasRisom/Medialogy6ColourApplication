package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ColorSelect extends AppCompatActivity {
    Button[] buttonGroup = new Button[3];
    boolean[] buttonActive = {true, true, true};
    boolean firstTime = true;

    float[][] YxyCoordinates = { {0.2f, 0.3f, 0.3f}, {0.2f, 0.4f, 0.4f}, {0.2f, 0.15f, 0.2f} };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);
        buttonSetup();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            int ID = extras.getInt("ID");
            buttonActive = extras.getBooleanArray("array");
            buttonActive[ID] = false;
        }

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
    }

    int[] YxyTosRGB(float Y, float x_coordinate, float y_coordinate){

        // X and Z calculations
        float X = (Y / y_coordinate) * x_coordinate;
        float Z = (Y / y_coordinate) * (1 - x_coordinate - y_coordinate);

        // Conversation matrix
        // Some online converters might be using a rounded up version of these values, making the result slightly different
        float a[][]={{3.2404542f,-1.5371385f,-0.4985314f},
                {-0.9692660f,1.8760108f,0.0415560f},
                {0.0556434f,-0.2040259f,1.0572252f}};

        // XYZ matrix
        float b[]={X,Y,Z};

        // Empty matrix which is where the resulting RGB values will go
        float c[]=new float[3];

        // Matrix multiplication to convert to RGB
        for(int i = 0; i < 3; i++){
            float temp = 0;
            for(int j = 0; j < 3; j++){
                temp += a[i][j] * b[j];
            }
            c[i] = temp;
        }

        // Prints out the X Y Z values
        System.out.println("\nX Y Z values:");

        for (int i = 0; i < 3; i++) {
            System.out.print(b[i] + " ");
            System.out.println();
        }

        // Prints out the R G B values
        System.out.println("\nResultant Matrix:");

        for (int i = 0; i < 3; i++) {

            // Gamma correction
            if(c[i] <= 0.0031308)
            {
                if (c[i] <= 0)
                {
                    System.out.print(0 + " (Real color value was: " + c[i] + ")");
                }
                else
                {
                    System.out.print(c[i] * 12.92 + " (Real color value was: " + c[i] + ")");
                }
            }
            else
            {
                System.out.print(String.format("%.2f",1.055*Math.pow(c[i], (1 / 2.4)) - 0.055) + " (Real color value was: " + c[i] + ")");
            }

            System.out.println();
        }

        for (int i = 0; i < 3; i++) {
            System.out.print("Colors: " + c[i] * 255 + " ");
            System.out.println();
        }

        // RC = Red Converted Color
        int RC = Math.round(c[0] * 255);
        int GC = Math.round(c[1] * 255);
        int BC = Math.round(c[2] * 255);

        int[] sRGB = {RC, GC, BC};

        for (int i = 0; i < 3; i++) {
            System.out.print("Colors rounded: " + sRGB[i] + " ");
            System.out.println();
        }
        return(sRGB);
    }

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

            Drawable background = buttonGroup[i].getBackground();
            int[] colors = YxyTosRGB(YxyCoordinates[i][0], YxyCoordinates[i][1], YxyCoordinates[i][2]);
            background.setColorFilter((Color.rgb(colors[0], colors[1], colors[2])), PorterDuff.Mode.SRC_IN);
        }
    }
}