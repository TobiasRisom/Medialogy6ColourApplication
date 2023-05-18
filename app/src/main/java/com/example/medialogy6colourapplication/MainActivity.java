package com.example.medialogy6colourapplication;

// Various view-related imports
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import android.content.Intent; // Changing Activities
import android.content.pm.ActivityInfo; // Information pertaining to activities
import android.graphics.Color; // Changing colours
import android.graphics.PorterDuff; // Alpha compositing and blending
import android.graphics.drawable.Drawable; // Drawing objects to screen
import android.os.Bundle; // Maps string keys to certain values
import android.os.CountDownTimer; // Counting real-time seconds


import androidx.annotation.NonNull; // Value can never be null
import androidx.appcompat.app.AppCompatActivity; // Base activity class
import androidx.constraintlayout.widget.ConstraintLayout; // Constraining layouts
import java.io.FileOutputStream; // Writing data to files
import java.io.IOException; // Throwing exceptions
import java.util.Arrays; // Utilizing arrays
import java.util.Random; // Random number generation

public class MainActivity extends AppCompatActivity {

    // VARIABLES

    // Views
    Button button1, button2, button3; // 1: Same, 2: Different, 3: Unsure
    View circle1, circle2;
    ProgressBar progressBar;

    // Colour Center Values
    float x_start;
    float y_start;
    float Y_start;
    int[] currentRGB; // Color Center values converted to sRGB
    int currentAngle = 0; // Current test angle (Note: 0 is north, 90 is east, etc.)

    // Variables for Initial Test (Narrowing down candidate area)
    int index = 0;
    float[] areaBuffer = {0f, 0.013f, 0.025f, 0.038f, 0.05f}; // Interval Distances
    int[] areaCheckOrder = shuffledArray(new int[] {0,1,2,3,4}); // Order of interval tests, randomized
    int[] areaBufferCheck = {0,0,0,0,0}; // Interval answers - 0: Different, 1: Same
    Boolean areaCheck = true; // True when narrowing down the interval area
    boolean specialCase = false; // True in special cases for larger intervals
    int narrowAreaErrorCount = 0; // Counts user errors

    // Variables for Split and Precision Test
    float precisionTestValue; // Value used during Split
    float highValue = 0f; // Highest possible value for boundary point
    float lowValue = 0f; // Lowest possible value for boundary point
    boolean split = true; // True when Split
    boolean isLowValue = true; // True when testing the low value, false when testing high value

    // Communication with other Activities
    boolean[] isCompleted; // Keeps track of which color centers have been tested
    int buttonID; // Communicates what color center has been selected on the Color Select activity

    // Miscellaneous
    boolean noFade = false; // Makes sure colours do not swap on the first comparison
    boolean buttonActive = true; // Sets buttons inactive when fading to black
    private static final String FILE_NAME = "data.txt"; // Name of file to save data to
    Random rand; // Random class
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Lock screen orientation to portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set up views
        button1 = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);
        progressBar = findViewById(R.id.progressBar);
        progressBar.setProgress(0);

        // Get data from the ColorSelect activity
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            float[] Yxy = extras.getFloatArray("values");
            Y_start = Yxy[0];
            x_start = Yxy[1];
            y_start = Yxy[2];
            noFade = true;

            buttonID = extras.getInt("button");
            isCompleted = extras.getBooleanArray("array");
        }

        // Convert the Yxy values of the color center to sRGB
        float[] startingPoint = angleToCoordinates(x_start, y_start, 0, currentAngle);
        int[] startingPointRGB = YxyTosRGB(Y_start, startingPoint[0], startingPoint[1]);
        currentRGB = new int[]{startingPointRGB[0], startingPointRGB[1], startingPointRGB[2]};

        // Set the comparison color to the first interval test color
        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[0]], currentAngle);
        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
        setColour(testColors[0], testColors[1], testColors[2]);

        // Same Button
        button1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {
                // Run main method if active
                if(buttonActive)
                {
                    main(true);
                }
            }
        });

        // Different Button
        button2.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if (buttonActive) {
                   main(false);
               }
           }
       });

        // Unsure Button
        button3.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (buttonActive) {
                    main(true);
                }
            }
        });
    }

    // Main - The body of the program. Keeps track of which step in the process the user is at.
    void main(boolean yesButton)
    {
        // INITIAL TEST
        if (areaCheck)
        {
            // Sets the check to either 0 or 1 depending on user's answer
            if(yesButton)
            {
                areaBufferCheck[areaCheckOrder[index]] = 1;
            }
            else {
                areaBufferCheck[areaCheckOrder[index]] = 0;
            }
            index++;

            // If the index value is longer than the areaBuffer array, we've tested all intervals
            if(index >= areaBuffer.length)
            {
                index = 0;

                // If the interval divisions are valid, the test moves on
                if(validArea())
                {
                    // Move on to the Split
                    narrowAreaErrorCount = 0;
                    progressBar.incrementProgressBy(6);
                    split = true;
                    areaCheck = false;
                    narrowArea();

                    // Find the midpoint between the current highest and lowest values
                    // Set the comparison color to that point
                    precisionTestValue = roundToDecimal(highValue - ((highValue - lowValue) / 2f));
                    float[] coordinates = angleToCoordinates(x_start, y_start, precisionTestValue, currentAngle);
                    int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                    setColour(testColors[0], testColors[1], testColors[2]);
                }
                else
                {
                    // Case where the area is not valid
                    narrowAreaErrorCount++;

                    // If the area is invalid 3 times in a row, move on to the next angle
                    if(narrowAreaErrorCount == 3)
                    {
                        saveDataError();
                        narrowAreaErrorCount = 0;
                        areaCheckOrder = shuffledArray(new int[] {0,1,2,3,4});
                        saveData(-1);
                        nextAngle();
                    }
                    else {
                        // If the user still has attempts left, restart the initial test
                        saveDataError();
                        System.out.println("Answer not valid. Restarting...");
                        Arrays.fill(areaBufferCheck, 0);
                        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[index]], currentAngle);
                        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                        setColour(testColors[0], testColors[1], testColors[2]);
                    }
                }
            }
            else
            {
                // If the initial test is not done yet, move on to the next interval
                float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[index]], currentAngle);
                int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                setColour(testColors[0], testColors[1], testColors[2]);
                System.out.println("Currently testing: " + areaBuffer[areaCheckOrder[index]]);
                System.out.println("Current threshold values: " + areaBufferCheck[0] + ", " + areaBufferCheck[1] + ", " + areaBufferCheck[2] + ", " + areaBufferCheck[3] + ", " + areaBufferCheck[4] + ", ");
            }
        }

        // SPLIT
        // If areaCheck is false, we move on to the Split
        // The color center is compared to the middle of the new area
        else {

            if (split)
            {
                // If user answers "Same", the boundary point is larger than the midpoint
                if(yesButton)
                {
                    lowValue = precisionTestValue + 0.001f;
                    highValue -= 0.001f;
                }

                // If user answers "Different", the boundary point is smaller than the midpoint
                else {
                    highValue = precisionTestValue - 0.001f;
                    lowValue += 0.001f;
                }

                // Set the comparison colour to the low value
                float[] coordinates = angleToCoordinates(x_start, y_start, lowValue, currentAngle);
                int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                setColour(testColors[0], testColors[1], testColors[2]);
                split = false;
            }

            // PRECISION TEST
            // Test bounces back and forth between low and high values until a precise value has been located
            else
            {

                // If the high value and the low value match, then that value is the boundary point
                if(highValue <= lowValue)
                {
                    System.out.println("COLOR THRESHOLD FOUND!");
                    System.out.println("Boundary Point: " + highValue);

                    // Reset the order array, save data and move on to the next angle
                    areaCheckOrder = shuffledArray(new int[] {0,1,2,3,4});
                    saveData(highValue);
                    nextAngle();
                }
                else {

                    if (yesButton) {
                        // User says the low value is the same - test is narrowed further
                        if (isLowValue) {
                            lowValue = roundToDecimal(lowValue += 0.001f);
                            isLowValue = false;
                            float[] coordinates = angleToCoordinates(x_start, y_start, highValue, currentAngle);
                            int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                            setColour(testColors[0], testColors[1], testColors[2]);
                        }

                        // User says the high value is the same - Boundary point found!
                        // The value is the highest point the user answered "Same" to
                        else {
                            System.out.println("COLOR THRESHOLD FOUND!");
                            System.out.println("Boundary Point: " + highValue);
                            areaCheckOrder = shuffledArray(new int[]{0, 1, 2, 3, 4});
                            saveData(highValue);
                            nextAngle();
                        }
                    } else {

                        // User says the high value is different - test is narrowed further
                        if (!isLowValue) {
                            highValue = roundToDecimal(highValue -= 0.001f);
                            isLowValue = true;
                            float[] coordinates = angleToCoordinates(x_start, y_start, lowValue, currentAngle);
                            int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
                            setColour(testColors[0], testColors[1], testColors[2]);
                        }
                        // User says the low value is different - boundary point found!
                        // The value 0.001 below is the highest point the user answered "Same" to
                        else if (isLowValue) {
                            System.out.println("COLOR THRESHOLD FOUND!");
                            System.out.println("Boundary Point: " + (lowValue - 0.001f));
                            areaCheckOrder = shuffledArray(new int[]{0, 1, 2, 3, 4});
                            saveData(lowValue - 0.001f);
                            nextAngle();
                        }
                    }
                }
            }
        }
    }

    // YxyTosRGB - Converts Yxy coordinates to sRGB values
    int[] YxyTosRGB(float Y, float x_coordinate, float y_coordinate){

        // X and Z calculations
        float X = (Y / y_coordinate) * x_coordinate;
        float Z = (Y / y_coordinate) * (1 - x_coordinate - y_coordinate);

        // Conversation matrix
        float[][] a ={{3.2404542f,-1.5371385f,-0.4985314f},
                {-0.9692660f,1.8760108f,0.0415560f},
                {0.0556434f,-0.2040259f,1.0572252f}};

        // XYZ matrix
        float[] b ={X,Y,Z};

        // Empty matrix which is where the resulting RGB values will go
        float[] c =new float[3];

        // Matrix multiplication to convert to RGB
        for(int i = 0; i < 3; i++){
            float temp = 0;
            for(int j = 0; j < 3; j++){
                temp += a[i][j] * b[j];
            }
            c[i] = temp;
        }

        // Convert from 0-1 to 0-255
        int RC = Math.round(c[0] * 255);
        int GC = Math.round(c[1] * 255);
        int BC = Math.round(c[2] * 255);

        int[] sRGB = {RC, GC, BC};

        return(sRGB);
    }

    // validArea - Checks if the narrowed down area is valid (0 = Different, 1 = Same)
    Boolean validArea()
    {
        // Area is only valid if there is a clear dividing line between "Same" and "Different" (or a special case)
        // Example of valid arrays:
        // [1,1,0,0,0]
        // [1,0,0,0,0]
        // [1,1,0,1,0] (Special case)
        // Example of invalid arrays:
        // [0,1,0,0,0] (First value is "Different")
        // [1,0,0,0,1] (Last value is "Same")
        // [1,0,1,0,1] (Inconclusive results - there's no narrowed down area)

        // Define the special cases, where one value is "Different" between two that are "Same"
        int[] scArray1 = {1,0,1,0,0};
        int[] scArray2 = {1,1,0,1,0};

        // If the first value is "Different" or the last value is "Same", the area is invalid
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
            // For all other cases, we loop through the array to check
            for(int i = 0; i < areaBufferCheck.length; i++)
            {
                // When the first instance of "Different" is found, loop through the array
                // This value is referred to as the "Division"
                if(areaBufferCheck[i] == 0)
                {
                    for(int j = 0; j < areaBufferCheck.length; j++)
                    {
                        // If any interval smaller than the division was labelled "Different", area is invalid
                        if(areaBufferCheck[j] == 0 && j < i)
                        {
                            System.out.println("Invalid - smaller value than division was 0");
                            return false;
                        }

                        // If any interval larger than the divison was labelled "Same", area is invalid
                        else if(areaBufferCheck[j] == 1 && j > i)
                        {
                            System.out.println("Invalid - larger value than division was 1");
                            return false;
                        }
                    }

                    // If no errors pop op, the area is valid!
                    System.out.println("Answer valid!");
                    return true;
                }
            }

            // Failsafe in case the loop fails to return anything
            System.out.println("Answer invalid - for loop resulted in no return");
            return false;
        }
    }

    // narrowArea - Narrows a valid area down by setting the high and low values
    void narrowArea()
    {
        // Loop through the array
        for(int i = 0; i < areaBufferCheck.length; i++)
        {
            if (areaBufferCheck[i] == 0)
            {
                // Set the first interval labelled "Different" as the highest possible value for the Boundary point
                // Set the last interval labelled "Same" as the lowest possible value
                if(!specialCase)
                {
                    highValue = areaBuffer[i];
                    lowValue = areaBuffer[i-1];
                }
                else
                {
                    // For special cases, the area is wider
                    highValue = areaBuffer[i+1];
                    lowValue = areaBuffer[i-1];
                    specialCase = false;
                    System.out.println("Special case! High buffer is: " + highValue + ", low buffer is: " + lowValue);
                }
                break;
            }
        }
    }

    // setColor - Sets the comparison colour
    void setColour(int RC2, int GC2, int BC2)
    {

        // Find the circle views
        circle1 = findViewById(R.id.circle1);
        Drawable background1 = circle1.getBackground();

        circle2 = findViewById(R.id.circle2);
        Drawable background2 = circle2.getBackground();

        // Change the vertical position of the circles
        changeYCoordinate();

        // Do not fade to black on the first color pair
        if (!noFade)
        {

            // Fade the colours to black and set the buttons inactive for one second
            background1.setColorFilter(Color.rgb(25,25,25), PorterDuff.Mode.SRC_IN);
            background2.setColorFilter(Color.rgb(25,25,25), PorterDuff.Mode.SRC_IN);
            buttonActive = false;
            new CountDownTimer(1000, 1000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    buttonActive = true;
                    // 50% chance for the colors to be on either side
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
            noFade = false;
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

    // changeYCoordinate - Change the vertical position of the circles
    void changeYCoordinate()
    {

        // Get a random int between 190 and 270
        int bufferInt =  (int)Math.floor(Math.random() * (270 - 190 + 1) + 190);
        System.out.println("buffer int = " + bufferInt);

        // Find the circles and set their Y-coordinate to the buffer int
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

    // angleToCoordinates - Converts the angle and distance from color center into CIE-XYZ coordinates
    float[] angleToCoordinates(float x_start, float y_start, float radius, int angle_degrees)
    {
        // Using trigonometry to find the coordinates
        double x = radius * Math.sin(Math.PI * 2 * angle_degrees / 360);
        double y = radius * Math.cos(Math.PI * 2 * angle_degrees / 360);

        float[] coordinates = {(float)x + x_start, (float)y + y_start};
        System.out.println("Points coors are: " + coordinates[0] + " and " + coordinates[1]);

        return coordinates;
    }

    // nextAngle - Sets the new angle, and stops the test if the angle is 360
    void nextAngle()
    {
        // Increase angle by 45 degrees
        currentAngle += 45;
        progressBar.incrementProgressBy(6);

        // If we've gone around the entire circle, we're done! Go back to Color Select
        if(currentAngle == 360)
        {
            Intent i = new Intent(MainActivity.this, ColorSelect.class);
            i.putExtra("array", isCompleted);
            i.putExtra("ID", buttonID);
            startActivity(i);
        }

        // Reset variables and set the comparison color to the new point
        areaCheck = true;
        index = 0;
        float[] coordinates = angleToCoordinates(x_start, y_start, areaBuffer[areaCheckOrder[0]], currentAngle);
        int[] testColors = YxyTosRGB(Y_start, coordinates[0], coordinates[1]);
        setColour(testColors[0], testColors[1], testColors[2]);

    }

    // saveData - Saves the data to a .txt file
    public void saveData(float value)
    {
        // Gets the coordinates and writes a string with all the data
        float[] coors = angleToCoordinates(x_start, y_start, value, currentAngle);
        String data = "Starting Points:\nx: " + x_start + "\ny: " + y_start + "\nAngle: " + currentAngle + "\nPoint: " + value + "\nx1: " + coors[0] + "\ny1: " + coors[1] + "\n";
        FileOutputStream fos = null;

        // Attempt to open the file and write the data
        // The mode is 'MODE_APPEND', so we do not write over what is already there
        try {
            fos = openFileOutput(FILE_NAME, MODE_APPEND);
            fos.write(data.getBytes());

            System.out.println("Saved to " + getFilesDir() + "/" + FILE_NAME);

            // Throw exceptions on failed connections / failure to close the connection
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

    // saveDataError - Same as saveData, but for invalid user inputs
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

    // roundToDecimal - Rounds numbers to 3 decimals
    float roundToDecimal(float number)
    {
        int scale = (int) Math.pow(10, 3);
        return (float) Math.round(number * scale) / scale;
    }

    // fiftyFifty - 50% chance of returning True, 50% change of return False
    boolean fiftyFifty()
    {
        rand = new Random();
        return Math.random() < 0.5;
    }

    // shuffledArray - Shuffles an array using a Fisher-Yates shuffle
    int[] shuffledArray(@NonNull int[] array)
    {
        rand = new Random();

        // For each position in array, swap value with another random position
        // Repeat for each position in the array
        for (int i = array.length - 1; i > 0; i--)
        {
            int index = rand.nextInt(i + 1);
            int a = array[index];
            array[index] = array[i];
            array[i] = a;
        }
        return array;
    }
}
