package com.example.medialogy6colourapplication;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Define the object for Radio Group,
    // Submit and Clear buttons
    private RadioGroup radioGroup;
    Button submit, clear;
    View circle1, circle2;

    float[] buffers = {0.1f, 0.001f, 0.05f, 0.002f, 0.05f, 0.005f, 0.03f, 0.007f, 0.02f, 0.01f, 0.015f};
    int index = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind the components to their respective object by assigning their IDs with the help of findViewById() method
        submit = (Button)findViewById(R.id.submit);
        radioGroup = (RadioGroup)findViewById(R.id.groupradio);

        // Find circle2 and change its color
        circle2 = (View)findViewById(R.id.circle2);
        Drawable background = circle2.getBackground();
        background.setColorFilter(Color.parseColor("#440033"), PorterDuff.Mode.SRC_IN);

        // Uncheck or reset the radio buttons initially
        radioGroup.clearCheck();

        // Add the Listener to the RadioGroup
        radioGroup.setOnCheckedChangeListener(
                new RadioGroup
                        .OnCheckedChangeListener() {
                    @Override

                    // The flow will come here when
                    // any of the radio buttons in the radioGroup
                    // has been clicked

                    // Check which radio button has been clicked
                    public void onCheckedChanged(RadioGroup group,
                                                 int checkedId)
                    {

                        // Get the selected Radio Button
                        RadioButton
                                radioButton
                                = (RadioButton)group
                                .findViewById(checkedId);
                    }
                });

        int[] testColors = YxyTosRGB(0.2f, 0.3f,0.3f + buffers[0]);
        setColor(50, 51, 60, testColors[0], testColors[1], testColors[2]);

        // Add the Listener to the Submit Button
        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v)
            {

                // When submit button is clicked,
                // Ge the Radio Button which is set
                // If no Radio Button is set, -1 will be returned
                int selectedId = radioGroup.getCheckedRadioButtonId();

                if (selectedId == -1)
                {
                    Toast.makeText(MainActivity.this, "No answer has been selected", Toast.LENGTH_SHORT).show();
                }

                else

                {

                    RadioButton radioButton = (RadioButton)radioGroup.findViewById(selectedId);

                    // Now display the value of selected item
                    // by the Toast message
                    Toast.makeText(MainActivity.this, radioButton.getText(), Toast.LENGTH_SHORT).show();
                    index++;
                    if(index >= buffers.length)
                    {
                        index = 0;
                    }
                    int[] testColors = YxyTosRGB(0.2f, 0.3f,0.3f + buffers[index]);
                    setColor(testColors[0], testColors[1], testColors[2]);
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

    void setColor(int RC1, int GC1, int BC1, int RC2, int GC2, int BC2)
    {
        // Left-Most Color (TARGET COLOR)
        circle1 = (View)findViewById(R.id.circle1);
        Drawable background1 = circle1.getBackground();
        background1.setColorFilter((Color.rgb(RC1, GC1, BC1)), PorterDuff.Mode.SRC_IN);

        // Right-Most Color (COMPARISON COLOR)
        circle2 = (View)findViewById(R.id.circle2);
        Drawable background2 = circle2.getBackground();
        background2.setColorFilter((Color.rgb(RC2, GC2, BC2)), PorterDuff.Mode.SRC_IN);
    }
    void setColor(int RC2, int GC2, int BC2)
    {
        // Right-Most Color (COMPARISON COLOR)
        circle2 = (View)findViewById(R.id.circle2);
        Drawable background2 = circle2.getBackground();
        background2.setColorFilter((Color.rgb(RC2, GC2, BC2)), PorterDuff.Mode.SRC_IN);
    }
}
