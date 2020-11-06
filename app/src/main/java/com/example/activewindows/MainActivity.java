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
// Import necessary info for AWS
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import java.io.UnsupportedEncodingException;
import java.util.UUID;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttClientStatusCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttManager;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttNewMessageCallback;
import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.amazonaws.regions.Regions;

// The following tutorial is incredably helpful on understanding AWS -> Android App stuff
// https://www.linkedin.com/pulse/android-app-aws-iot-core-guide-felipe-ramos-da-silva
// The same can be said with this video for the future although this mainly deals with microcontrollers:
// https://youtu.be/LNVRzr4oDW0
// https://github.com/felipemeriga/aws-sdk-android-samples

// _________________________________________________________________________________________
// Various info for my own knowledge
// Cognito Identities consist of the following:
// ActiveWindowsIoT
// IAM Roles:
// Cognito_ActiveWindowsIoTAuth_Role => Authenticated Role
// Cognito_ActiveWindowsIoTUnauth_Role => Unauthenticated Role
// Both of these Roles were given IoTFullAccess Policies
// Iot Core:
// This is referenced as "ActiveWindowsAndroidApp" for a thing
// HTTPS
// Update your Thing Shadow using this Rest API Endpoint.
// aga9o21zvh26x-ats.iot.us-east-1.amazonaws.com
// This app will SUBSCRIBE to:
// windowStatusTopic
// This app will PUBLISH to:
// windowCommandTopic
// _________________________________________________________________________________________



//_____________________________________________________________________________________________
// To whom it may concern, this line is the equivalent of the Main file in a Java/C/C++/Python File.
// Normally there is no 'Implements AdapterView' section, but this is here to help implement our
// Spinner in the UI
public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    static final String LOG_TAG = MainActivity.class.getCanonicalName();
    // CREATE INFORMATION FOR AWS.
    //______________________________________________________________________________________________
    // AWS - for AWS Thing (ActiveWindowsAndroidApp thing)
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "aga9o21zvh26x-ats.iot.us-east-1.amazonaws.com";
    // For the COGNITO roles that we are using (Cognito_ActiveWindowsIoTAuth_Role / unauth)
    private static final String COGNITO_POOL_ID = "us-east-1:a2ebd097-89fc-44a3-9cda-c0dcdac34457";
    // AWS Region
    private static final Regions MY_REGION = Regions.US_EAST_1; // TODO Note these are originally private
    public AWSIotMqttManager mqttManager; // manager for MQTT
    public String clientId; // Client ID, since this needs to be unique for the users, we will be using it.
    public CognitoCachingCredentialsProvider credentialsProvider; //For credentials to connect to AWS
    // FOR SOME REASON: The above code can't be referenced in the other activity to actively see data.
    // So basically will need to remake it in the item select code.
    TextView currentStatus; // This var is to keep track of connectivity to aws



    // START APPLICATION
    //_____________________________________________________________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) { // This gets called all the time once the
        // App starts. This is effectively the Main method, but for Android Studio.
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        currentStatus = (TextView) findViewById(R.id.currentStatusView); //grab

        //SET UP MORE INFORMATION FOR AWS.
        //_________________________________________________________________________________________
        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        //clientId = UUID.randomUUID().toString();

        // MQTT client IDs are required to be unique per AWS IoT account.
        // This UUID is "practically unique" but does not _guarantee_
        // uniqueness.
        clientId = UUID.randomUUID().toString();
        // Initialize the AWS Cognito credentials provider
        credentialsProvider = new CognitoCachingCredentialsProvider(
                getApplicationContext(), // context
                COGNITO_POOL_ID, // Identity Pool ID
                MY_REGION // Region
        );

        // MQTT Client
        mqttManager = new AWSIotMqttManager(clientId, CUSTOMER_SPECIFIC_ENDPOINT);

        // Check MQTT connectrion response when attempting to connect to the servers.
        try {
            mqttManager.connect(credentialsProvider, new AWSIotMqttClientStatusCallback() {
                @Override
                public void onStatusChanged(final AWSIotMqttClientStatus status, final Throwable throwable) {
                    Log.d(LOG_TAG, "Status = " + String.valueOf(status));

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (status == AWSIotMqttClientStatus.Connecting) {
                                currentStatus.setText("Connecting...");

                            } else if (status == AWSIotMqttClientStatus.Connected) {
                                currentStatus.setText("Connected");

                            } else if (status == AWSIotMqttClientStatus.Reconnecting) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                }
                                currentStatus.setText("Reconnecting");
                            } else if (status == AWSIotMqttClientStatus.ConnectionLost) {
                                if (throwable != null) {
                                    Log.e(LOG_TAG, "Connection error.", throwable);
                                    throwable.printStackTrace();
                                }
                                currentStatus.setText("Disconnected");
                            } else {
                                currentStatus.setText("Disconnected");

                            }
                        }
                    });
                }
            });
        } catch (final Exception e) { //If there is an error, display it.
            Log.e(LOG_TAG, "Connection error.", e);
            currentStatus.setText("Error! " + e.getMessage());
        }






        // ________________________________________________________________________________________
        // Create UI Elements primarily for date and time
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
        // The windows from.
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