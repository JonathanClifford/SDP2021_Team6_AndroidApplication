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
// If there are any comments/questions about this code, please do not hesitate to send an email to:
// joncliffjr@gmail.com. The rest of my teammates would be more than happy to assist as well going
// forward for this project or to answer questions about their subsystems.
// ________________________________________________________________________________________________

// Import various packages.

package com.example.activewindows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat; // Used to display the date.
import java.util.Date;
import java.util.Locale;




//_____________________________________________________________________________________________
// To whom it may concern, this line is the equivalent of the Main file in a Java/C/C++/Python File.
// Normally there is no 'Implements AdapterView' section, but this is here to help implement our
// Spinner in the UI
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

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
        welcomeUser.setText("Welcome, " + userName + " !");

        // Create the spinner. This is used as a drop down menu that the user will select each of
        // The windows from.  TODO changed this to final
        final Spinner windowSpinner = (Spinner) findViewById(R.id.spinner1);

        //ArrayAdapter is the container that holds the values and then integrate w/ spinner

        ArrayAdapter<String> windowAdapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.names));
        windowAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        windowSpinner.setAdapter(windowAdapter); // Set the Spinner String and the Adapter as one item
        // In other words: this line allows for the adapter to show the data in the spinner.
        // We also need the spinner to react to clicks, so:
        windowSpinner.setOnItemSelectedListener(this); // Call on Item Selected. Once one of the
        // Specific selected options for the windows



    }

    //_____________________________________________________________________________________________
    // The following two methods are for the Spinner drop down list to react to clicks from the
    // User.
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        if (parent.getItemAtPosition(position).equals("Please Select Your Window")){
            //Proceed to do absolutely nothing. This is the default case.
        }
        else
        {
            String text = "Selected: " + parent.getItemAtPosition(position).toString(); // Take item @ position, turn
            //into string
            Toast.makeText(parent.getContext(), text, Toast.LENGTH_SHORT).show();

            //This statement will ensure that it goes to the next Activity, which is item_select.java.
            if(parent.getItemAtPosition(position).equals("Window #1"))
            {
                // Basically when this is selected, and its window 1, we are now going to load the other
                // Activity. This other activity will handle sending the data.
                Intent intent = new Intent(MainActivity.this, item_select.class);
                startActivity(intent);
            }
        }



    }



    // This method just does nothing. It gets imported with the OnSelected method.
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}