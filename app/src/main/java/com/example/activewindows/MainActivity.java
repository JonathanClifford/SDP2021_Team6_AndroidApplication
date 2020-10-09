// ________________________________________________________________________________________________
// Created by Jonathan Clifford
// UMass ECE Class of 2021
// Senior Design Project 2021, Team # 6 Active Windows Project
// The purpose of this application is to serve as a direct interface with Amazon Web Services (AWS)
// And is capable of reading data from the Window & it's status (Whether it is open, cracked, or
// closed) and to send direct commands to the window to adjust automatically (These commands are:
// Open, Crack, and Close).
// This is the basic fundamentals of this software. For the next steps directly relating to this
// Software, it would need to be capable of also reading the sensor data that the rest of the system
// has access to, and would automatically allow for AWS to control the window in a "Smart" manner.
// If there are any comments about this code, please do not hesitate to send an email to:
// joncliffjr@gmail.com . The rest of my teammates would be more than happy to assist as well going
// forward for this project or to answer questions about their subsystems.
// ________________________________________________________________________________________________

// Import various packages.

package com.example.activewindows;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import java.text.SimpleDateFormat; // Used to display the date.
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) { // This gets called all the time once the
        // App starts. This is effectively the Main method, but for Android Studio.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Create a string that gets recognized as the date. Change the display file to match
        String theDate_str = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(new Date()); // This just sets a date
        TextView textDate = (TextView)findViewById(R.id.editTextDate); // This is for the text date
        textDate.setText(theDate_str);

        //Create a string that gets recognized as the Time in Military format.
        String theTime_str = new SimpleDateFormat("HH:mm").format(new Date());
        TextView textTime = (TextView)findViewById(R.id.editTextTime);
        textTime.setText(theTime_str);

        //Set the user name and have it be recognized as such and as a total string.
        //TODO Eventually make this string as a field that the user needs to edit on the first boot
        String userName = "Jonathan";
        TextView welcomeUser = (TextView)findViewById(R.id.editTextTextPersonName2);
        welcomeUser.setText("Welcome " + userName);
    }

}