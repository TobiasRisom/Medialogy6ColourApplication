package com.example.medialogy6colourapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BlendMode;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ColorSelect extends AppCompatActivity {
    Button[] levelGroup = new Button[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color_select);
        levelGroup[0] = findViewById(R.id.level1);
        levelGroup[1] = findViewById(R.id.level2);
        levelGroup[2] = findViewById(R.id.level3);

        levelGroup[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] Yxy = {0.2f, 0.3f, 0.3f};
                Intent i = new Intent(ColorSelect.this, MainActivity.class);
                i.putExtra("values", Yxy);
                startActivity(i);
            }
        });

        levelGroup[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] Yxy = {0.2f, 0.2f, 0.2f};
                Intent i = new Intent(ColorSelect.this, MainActivity.class);
                i.putExtra("values", Yxy);
                startActivity(i);
            }
        });

        levelGroup[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float[] Yxy = {0.2f, 0.15f, 0.2f};
                Intent i = new Intent(ColorSelect.this, MainActivity.class);
                i.putExtra("values", Yxy);
                startActivity(i);
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
}