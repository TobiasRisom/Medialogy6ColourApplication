package com.example.medialogy6colourapplication;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // button1 = Yes
    // button2 = No
    // button3 = Unsure
    Button button1, button2, button3;
    ProgressBar progressBar;
    View circle1, circle2;
    int[] currentRGB;
    Boolean areaCheck = true;
    float[] areaBuffer = {0f, 0.013f, 0.025f, 0.038f, 0.05f};
    int[] areaCheckOrder = shuffledArray(new int[] {0,1,2,3,4});
    int[] areaBufferCheck = {0,0,0,0,0};
    float x_start;
    float y_start;
    float Y_start;
    int currentAngle = 0;
    float highValue = 0f;
    float lowValue = 0f;
    float precisionTestValue;
    int index = 0;
    boolean split = true;
    boolean isLowValue = true;
    Random rand;
    boolean[] isCompleted;
    boolean specialCase = false;
    int buttonID;
    int narrowAreaErrorCount = 0;
    boolean firstValue = false;
    boolean noSwitch = false;
    boolean buttonActive = true;
    int steps = 0;
    float value = 0f;

    // Create the txt file for storing data
    private static final String FILE_NAME = "data.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lock screen orientation to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            float[] Yxy = extras.getFloatArray("values");
            Y_start = Yxy[0];
            x_start = Yxy[1];
            y_start = Yxy[2];
            noSwitch = true;

            buttonID = extras.getInt("button");
            isCompleted = extras.getBooleanArray("array");
        }

        float[] startingPoint = angleToCoordinates(x_start, y_start, 0, currentAngle);
        int[] startingPointRGB = YxyTosRGB(Y_start, startingPoint[0], startingPoint[1]);
        currentRGB = new int[]{startingPointRGB[0], startingPointRGB[1], startingPointRGB[2]};

        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[0]], currentAngle);
        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
        setColor(testColors[0], testColors[1], testColors[2]);

        // Add the Listener to the Submit Button
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                if(buttonActive == true)
                {
                    main(true);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (buttonActive == true) {
                   main(false);
               }
           }
       });

        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (buttonActive == true) {
                    main(true);
                }
            }
        });
    }

    void main(boolean yesButton)
    {
        // AREA NARROW
        if (areaCheck)
        {
            if(yesButton)
            {
                areaBufferCheck[areaCheckOrder[index]] = 1;
            }
            else {
                areaBufferCheck[areaCheckOrder[index]] = 0;
            }
            index++;
            if(index >= areaBuffer.length)
            {
                index = 0;

                // If the area is valid, we should have an area narrowed down. The next test is the halfway point in this area.
                if(validArea() == true)
                {
                    progressBar.incrementProgressBy(6);
                    split = true;
                    narrowArea();
                    areaCheck = false;
                    narrowAreaErrorCount = 0;
                    System.out.println("Valid area found!");
                    System.out.println("Value is between: " + lowValue + " and " + highValue);
                    precisionTestValue = roundToDecimal(highValue -((highValue - lowValue) / 2f), 3);
                    float[] coordinates = angleToCoordinates(x_start, y_start, precisionTestValue, currentAngle);
                    int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                    setColor(testColors[0], testColors[1], testColors[2]);
                    System.out.println("Currently testing: " + precisionTestValue);
                }
                else
                {
                    narrowAreaErrorCount++;
                    if(narrowAreaErrorCount == 3)
                    {
                        saveDataError();
                        narrowAreaErrorCount = 0;
                        //Toast.makeText(MainActivity.this, "ANSWER INVALID. MOVING ON.", Toast.LENGTH_SHORT).show();
                        areaCheckOrder = shuffledArray(new int[] {0,1,2,3,4});
                        saveData(-1);
                        nextAngle();
                    }
                    else {

                        // Reset the test on invalid data (e.g. "[0,0,1,0,1]" or "[1,1,1,1,1]")
                        saveDataError();
                        //Toast.makeText(MainActivity.this, "ANSWER INVALID FOR ANGLE. TRY AGAIN.", Toast.LENGTH_SHORT).show();
                        System.out.println("Answer not valid. Restarting...");
                        for (int i = 0; i < areaBufferCheck.length; i++) {
                            areaBufferCheck[i] = 0;
                        }
                        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[index]], currentAngle);
                        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                        setColor(testColors[0], testColors[1], testColors[2]);
                    }
                }
            }
            else
            {
                float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[index]], currentAngle);
                int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                setColor(testColors[0], testColors[1], testColors[2]);
                System.out.println("Currently testing: " + areaBuffer[areaCheckOrder[index]]);
                System.out.println("Current threshold values: " + areaBufferCheck[0] + ", " + areaBufferCheck[1] + ", " + areaBufferCheck[2] + ", " + areaBufferCheck[3] + ", " + areaBufferCheck[4] + ", ");
            }
        }

        // FIND THRESHOLD
        else if(areaCheck == false)
        {

            // First test splits the area in half. This narrows down the test field a bit.
            if (split == true)
            {
                if(yesButton)
                {
                    lowValue = precisionTestValue + 0.001f;
                    highValue -= 0.001f;
                }
                else if(!yesButton)
                {
                    highValue = precisionTestValue - 0.001f;
                    lowValue += 0.001f;
                }

                System.out.println("High value: " + highValue + " Low value: " + lowValue);
                System.out.println("Currently testing: " + lowValue);
                float[] coordinates = angleToCoordinates(x_start, y_start, lowValue, currentAngle);
                int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                setColor(testColors[0], testColors[1], testColors[2]);
                split = false;
                firstValue = true;
            }

            // Starts on the lowValue now with one split having been done (example: low = 0.05, high = 0.063)
            // Now the test bounces back and forth until a precise value has been located
            else
            {
                firstValue = false;

                // If the high value and the low value match, what do we do?
                // CURRENT SOLUTION: Use that point as the data
                if(highValue <= lowValue)
                {
                    System.out.println("COLOR THRESHOLD FOUND!");
                    System.out.println("MacAdam Point: " + highValue);
                    areaCheckOrder = shuffledArray(new int[] {0,1,2,3,4});
                    saveData(highValue);
                    nextAngle(highValue);
                }
                else {
                    steps++;

                    if (yesButton) {
                        // User says the low value is the same - test is narrowed further
                        if (isLowValue == true) {
                            lowValue = roundToDecimal(lowValue += 0.001f, 3);
                            isLowValue = false;
                            float[] coordinates = angleToCoordinates(x_start, y_start, highValue, currentAngle);
                            int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                            setColor(testColors[0], testColors[1], testColors[2]);

                            System.out.println("High value: " + highValue + " Low value: " + lowValue);
                            System.out.println("Currently testing: " + highValue);
                        }

                        // User says the high value is the same - this must be the highest possible matching color.
                        else if (isLowValue == false) {
                            System.out.println("COLOR THRESHOLD FOUND!");
                            System.out.println("MacAdam Point: " + highValue);
                            areaCheckOrder = shuffledArray(new int[]{0, 1, 2, 3, 4});
                            saveData(highValue);
                            nextAngle(highValue);
                        }
                    } else if (!yesButton) {

                        // User says the high value is different - test is narrowed further
                        if (isLowValue == false) {
                            highValue = roundToDecimal(highValue -= 0.001f, 3);
                            isLowValue = true;
                            float[] coordinates = angleToCoordinates(x_start, y_start, lowValue, currentAngle);
                            int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                            setColor(testColors[0], testColors[1], testColors[2]);

                            System.out.println("High value: " + highValue + " Low value: " + lowValue);
                            System.out.println("Currently testing: " + lowValue);
                        }
                        // User says the low value is different - this must be the value 1 step above the highest possible matching color.
                        else if (isLowValue == true) {
                            System.out.println("COLOR THRESHOLD FOUND!");
                            System.out.println("MacAdam Point: " + (lowValue - 0.001f));
                            areaCheckOrder = shuffledArray(new int[]{0, 1, 2, 3, 4});
                            saveData(lowValue - 0.001f);
                            nextAngle(lowValue - 0.001f);
                        }
                    }
                }
            }
        }
    }


    void undo()
    {/*
        // Undo on initial narrowing down of area
        if (index > 0) {
            index--;
            float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[index]], currentAngle);
            int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
            setColor(testColors[0], testColors[1], testColors[2], false);
            System.out.println("Undo button!");
            System.out.println("Currently testing: " + areaBuffer[areaCheckOrder[index]]);
            System.out.println("Current threshold values: " + areaBufferCheck[0] + ", " + areaBufferCheck[1] + ", " + areaBufferCheck[2] + ", " + areaBufferCheck[3] + ", " + areaBufferCheck[4] + ", ");
        } else if (areaCheck == false) {
            // Undo on area split
            if (split == true) {
                index = 4;
                areaCheck = true;
                float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[index]], currentAngle);
                int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                setColor(testColors[0], testColors[1], testColors[2], false);
                System.out.println("Undo button!");
                System.out.println("Currently testing: " + areaBuffer[areaCheckOrder[index]]);
                System.out.println("Current threshold values: " + areaBufferCheck[0] + ", " + areaBufferCheck[1] + ", " + areaBufferCheck[2] + ", " + areaBufferCheck[3] + ", " + areaBufferCheck[4] + ", ");
            } else {
                // If the player presses Undo on the first after split
                if (firstValue || steps == 0) {
                    split = true;
                    areaCheck = false;
                    narrowArea();
                    float[] coordinates = angleToCoordinates(x_start, y_start, precisionTestValue, currentAngle);
                    int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                    setColor(testColors[0], testColors[1], testColors[2], true);
                    System.out.println("Currently testing: " + precisionTestValue);
                } else {
                    steps--;
                    // If the current test is Low Value, then the previous one must be High Value
                    if (isLowValue) {
                        highValue = roundToDecimal(highValue += 0.001f, 3);
                        isLowValue = false;
                        float[] coordinates = angleToCoordinates(x_start, y_start, highValue, currentAngle);
                        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                        setColor(testColors[0], testColors[1], testColors[2], true);
                        System.out.println("Currently testing: " + highValue);
                    } else {
                        lowValue = roundToDecimal(lowValue -= 0.001f, 3);
                        isLowValue = true;
                        float[] coordinates = angleToCoordinates(x_start, y_start, highValue, currentAngle);
                        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                        setColor(testColors[0], testColors[1], testColors[2], true);
                        System.out.println("Currently testing: " + lowValue);
                    }
                }
            }
        }
        System.out.println("Index is now: " + index + "Steps: " + steps);
        System.out.println("Lowvalue " + lowValue + "Highvalue: " + highValue);
    }*/}
    Boolean validArea()
    {
        int[] scArray1 = {1,0,1,0,0};
        int[] scArray2 = {1,1,0,1,0};
        for (int i = 0; i < 5; i++)
        {
            System.out.println("AreaBuffer: " + areaBufferCheck[i]);
            System.out.println(scArray1[i]);
        }

        // 1 = same, 0 = different
        if(areaBufferCheck[0] == 0 || areaBufferCheck[4] == 1)
        {
            System.out.println("Answer invalid - first or last value was invalid.");
            return false;
        }

        // Special cases - area is wider than usual
        else if((Arrays.equals(areaBufferCheck, scArray1)) || Arrays.equals(areaBufferCheck, scArray2))
        {
            specialCase = true;
            return true;
        }
        else
        {
            for(int i = 0; i < areaBufferCheck.length; i++)
            {
                if(areaBufferCheck[i] == 0)
                {
                    int division = i;

                    // valid case: 1 1 0 0 0
                    // to-be valid case: 1 0 1 1 1
                    // to-be valid case: 1 1 0 1 0
                    // NOT valid case: 1 0 0 1 0
                    for(int j = 0; j < areaBufferCheck.length; j++)
                    {
                        if(areaBufferCheck[j] == 0 && j < division)
                        {
                            System.out.println("Invalid - smaller value than division was 0");
                            return false;
                        }
                        else if(areaBufferCheck[j] == 1 && j > division)
                        {
                            System.out.println("Invalid - larger value than division was 1");
                            return false;
                        }
                    }
                    System.out.println("Answer valid!");
                    return true;
                }
                else if (areaBufferCheck[i] == 1)
                {
                    continue;
                }
            }
            System.out.println("Answer invalid - for loop resulted in no return");
            return false;
        }
    }
    void narrowArea()
    {
        for(int i = 0; i < areaBufferCheck.length; i++)
        {
            if (areaBufferCheck[i] == 0)
            {
                if(specialCase == false)
                {
                    highValue = areaBuffer[i];
                    lowValue = areaBuffer[i-1];
                    break;
                }
                else
                {
                    highValue = areaBuffer[i+1];
                    lowValue = areaBuffer[i-1];
                    specialCase = false;
                    System.out.println("Special case! High buffer is: " + highValue + ", low buffer is: " + lowValue);
                    break;
                }
            }
            else
            {
                continue;
            }
        }
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

    float[] sRGBtoYxy(int red, int green, int blue)
    {
        float R = (red / 255f);
        float G = (green / 255f);
        float B = (blue / 255f);

        System.out.println("R = " + R);
        System.out.println("G = " + G);
        System.out.println("B = " + B);

        float[] sRGBcolors = {R, G, B};

        for(int i = 0; i < 3; i++)
        {
            if(sRGBcolors[i] <= 0.04045)
            {
                if(sRGBcolors[i] <= 0)
                {
                    sRGBcolors[i] = 0;
                }
                else
                {
                    sRGBcolors[i] = sRGBcolors[i] / 12.92f;
                }
            }
            else
            {
                sRGBcolors[i] = (float) Math.pow(((sRGBcolors[i] + 0.055) / 1.055), 2.4f);
            }
        }

        System.out.println("R corrected = " + sRGBcolors[0]);
        System.out.println("G corrected = " + sRGBcolors[1]);
        System.out.println("B corrected = " + sRGBcolors[2]);

        float a[][] = {{0.4124564f, 0.3575761f, 0.1804375f},
                      {0.2126729f, 0.7151522f, 0.0721750f},
                      {0.0193339f, 0.1191920f, 0.9503041f}};

        float b[] = {sRGBcolors[0], sRGBcolors[1], sRGBcolors[2]};
        //float b[] = {R, G, B};

        float c[] = new float[3];

        // Matrix multiplication to convert to XYZ
        for(int i = 0; i < 3; i++){
            float temp = 0;
            for(int j = 0; j < 3; j++){
                temp += a[i][j] * b[j];
            }
            c[i] = temp;
        }
        float X = c[0];
        float Y = c[1];
        float Z = c[2];

        System.out.println("X = " + X);
        System.out.println("Y = " + Y);
        System.out.println("Z = " + Z);

        // Convert XYZ to Yxy
        float[] Yxy = {Y, X / (X + Y + Z), Y / (X + Y + Z)};

        System.out.println("Y = " + Yxy[0]);
        System.out.println("x = " + Yxy[1]);
        System.out.println("y = " + Yxy[2]);

       /* for (int i = 0; i < 3; i++) {
            Yxy[i] = roundToDecimal(Yxy[i], 2);
        }*/

        System.out.println("Y = " + Yxy[0]);
        System.out.println("x = " + Yxy[1]);
        System.out.println("y = " + Yxy[2]);

        return(Yxy);
    }

    float roundToDecimal(float number, int precision)
    {
        int scale = (int) Math.pow(10, precision);
        return (float) Math.round(number * scale) / scale;
    }
    boolean fiftyFifty()
    {
        rand = new Random();
        return Math.random() < 0.5;
    }

    int[] shuffledArray(@NonNull int[] array)
    {
        rand = new Random();
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rand.nextInt(i + 1);
            // Simple swap
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
        for (int number : array)
        {
            System.out.println(number);
        }
        return array;
    }
    void setColor(int RC2, int GC2, int BC2)
    {
        circle1 = findViewById(R.id.circle1);
        Drawable background1 = circle1.getBackground();

        circle2 = findViewById(R.id.circle2);
        Drawable background2 = circle2.getBackground();

        changeYCoordinate();
        // Do not switch colors on the first pass
        if (noSwitch == false)
        {
            background1.setColorFilter(Color.rgb(25,25,25), PorterDuff.Mode.SRC_IN);
            background2.setColorFilter(Color.rgb(25,25,25), PorterDuff.Mode.SRC_IN);
            buttonActive = false;
            new CountDownTimer(1000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    buttonActive = true;
                    // Swap colors randomly
                    if(fiftyFifty())
                    {
                        background1.setColorFilter((Color.rgb(currentRGB[0], currentRGB[1], currentRGB[2])), PorterDuff.Mode.SRC_IN);
                        background2.setColorFilter((Color.rgb(RC2, GC2, BC2)), PorterDuff.Mode.SRC_IN);
                        System.out.println("Left color is standard");
                    }
                    else {
                        background1.setColorFilter((Color.rgb(RC2, GC2, BC2)), PorterDuff.Mode.SRC_IN);
                        background2.setColorFilter((Color.rgb(currentRGB[0], currentRGB[1], currentRGB[2])), PorterDuff.Mode.SRC_IN);
                        System.out.println("Right color is standard");
                    }
                }
            }.start();
        }
        else
        {
            noSwitch = false;
            // Swap colors randomly
            if(fiftyFifty())
            {
                background1.setColorFilter((Color.rgb(currentRGB[0], currentRGB[1], currentRGB[2])), PorterDuff.Mode.SRC_IN);
                background2.setColorFilter((Color.rgb(RC2, GC2, BC2)), PorterDuff.Mode.SRC_IN);
                System.out.println("Left color is standard");
            }
            else {
                background1.setColorFilter((Color.rgb(RC2, GC2, BC2)), PorterDuff.Mode.SRC_IN);
                background2.setColorFilter((Color.rgb(currentRGB[0], currentRGB[1], currentRGB[2])), PorterDuff.Mode.SRC_IN);
                System.out.println("Right color is standard");
            }
        }

    }

    void changeYCoordinate()
    {
        int bufferInt =  (int)Math.floor(Math.random() * (270 - 190 + 1) + 190);
        System.out.println("buffer int = " + bufferInt);
        circle1 = findViewById(R.id.circle1);
        circle2 = findViewById(R.id.circle2);

        ViewGroup.LayoutParams params1 = circle1.getLayoutParams();
        ViewGroup.LayoutParams params2 = circle2.getLayoutParams();

        ConstraintLayout.LayoutParams  cParams1 = (ConstraintLayout.LayoutParams) params1;
        ConstraintLayout.LayoutParams  cParams2 = (ConstraintLayout.LayoutParams) params2;

        cParams1.topMargin = bufferInt;
        cParams2.topMargin = bufferInt;

        circle1.setLayoutParams(cParams1);
        circle2.setLayoutParams(cParams2);
    }
    float[] angleToCoordinates(float x_start, float y_start, float radius, int angle_degrees)
    {
        double x = radius * Math.sin(Math.PI * 2 * angle_degrees / 360);
        double y = radius * Math.cos(Math.PI * 2 * angle_degrees / 360);

        float[] coordinates = {(float)x + x_start, (float)y + y_start};
        System.out.println("Points coors are: " + coordinates[0] + " and " + coordinates[1]);

        return coordinates;
    }
    void nextAngle(float ellipsePoint)
    {
        //Toast.makeText(MainActivity.this, "THRESHOLD FOUND: " + ellipsePoint, Toast.LENGTH_SHORT).show();
        currentAngle += 45;

        progressBar.incrementProgressBy(6);

        if(currentAngle == 360)
        {
            Intent i = new Intent(MainActivity.this, ColorSelect.class);
            i.putExtra("array", isCompleted);
            i.putExtra("ID", buttonID);
            System.out.println("Just finished testing button: " + buttonID);
            startActivity(i);
        }
        areaCheck = true;
        index = 0;
        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[0]], currentAngle);
        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
        setColor(testColors[0], testColors[1], testColors[2]);
        //Toast.makeText(MainActivity.this, "NEXT ANGLE: " + currentAngle, Toast.LENGTH_SHORT).show();

    }
    // nextAngle without argument is ONLY used for when there is an error in the answers
    void nextAngle()
    {
        progressBar.incrementProgressBy(6);
        currentAngle += 45;

        if(currentAngle == 360)
        {
            Intent i = new Intent(MainActivity.this, ColorSelect.class);
            i.putExtra("array", isCompleted);
            i.putExtra("ID", buttonID);
            System.out.println("Just finished testing button: " + buttonID);
            startActivity(i);
        }
        areaCheck = true;
        index = 0;
        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[0]], currentAngle);
        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
        setColor(testColors[0], testColors[1], testColors[2]);
        //Toast.makeText(MainActivity.this, "NEXT ANGLE: " + currentAngle, Toast.LENGTH_SHORT).show();

    }

    public void saveDataError()
    {
        String data = "Data Invalid Error!\n";
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
    public void saveData(float value)
    {
        float[] coors = angleToCoordinates(x_start, y_start, value, currentAngle);
        String data = "Starting Points:\nx: " + x_start + "\ny: " + y_start + "\nAngle: " + currentAngle + "\nPoint: " + value + "\nCoordinate: " + coors[0] + ", " + coors[1] + "\n";
        FileOutputStream fos = null;

        try {
            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(data.getBytes());

            System.out.println("Saved to " + getFilesDir() + "/" + FILE_NAME);

            //Toast.makeText(this, "Saved to " + getFilesDir() + "/" + FILE_NAME, Toast.LENGTH_LONG).show();
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
