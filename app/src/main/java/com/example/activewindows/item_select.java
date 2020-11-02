package com.example.activewindows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
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


// This file is dedicated to essentially loading another activity (Think of it as another class)
// That will load the data for the specific window and send the data to AWS.
// Since for this testbed we only have one window, this file could be renamed to instead
// "item_select_window_1.java". This would be a reccommended naming scheme for the other windows
// going forward post initital prototype. Essentially you could use this as a base and modify
// The other files in order to represent the other windows. In other words, the support is essentially
// There for you to play around with

public class item_select extends AppCompatActivity {
    Button back; // I have a back button instead of re-implementing the spinner in this class.
    // It would be straightforward to implement but for testing this is the most straightforward.

    // Select all of the buttons and instantiate them as existing.
    Button openWindow;
    Button crackWindow;
    Button closeWindow;
    TextView currentStatus;
    TextView latestMessage;
    // CREATE INFORMATION FOR AWS.
    //______________________________________________________________________________________________
    // AWS - for AWS Thing (ActiveWindowsAndroidApp thing)
    public static final String CUSTOMER_SPECIFIC_ENDPOINT = "aga9o21zvh26x-ats.iot.us-east-1.amazonaws.com";
    // For the COGNITO roles that we are using (Cognito_ActiveWindowsIoTAuth_Role / unauth)
    public static final String COGNITO_POOL_ID = "us-east-1:a2ebd097-89fc-44a3-9cda-c0dcdac34457";
    // AWS Region
    public static final Regions MY_REGION = Regions.US_EAST_1; // TODO Note these are originally private
    public AWSIotMqttManager mqttManager; // manager for MQTT
    public String clientId; // Client ID, since this needs to be unique for the users, we will be using it.
    public CognitoCachingCredentialsProvider credentialsProvider; //For credentials to connect to AWS
    static final String LOG_TAG = item_select.class.getCanonicalName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_select);


        //Select buttons and recognize them for later on.
        back = findViewById(R.id.TestButton);
        openWindow = findViewById(R.id.OpenWindowButton); // make it the open window button
        crackWindow = findViewById(R.id.CrackWindowButton);
        closeWindow = findViewById(R.id.CloseWindowButton);
        currentStatus = (TextView) findViewById(R.id.currentStatusView2);
        latestMessage = (TextView) findViewById(R.id.latestMessage);

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

        //__________________________________________________________________________________________
        // Publish and subscribe to topics.
        final String topic_sub = "windowStatusTopic"; //THIS IS THE TOPIC WE WILL SUBSCRIBE TO
        final String topic_pub = "windowCommandTopic"; //THIS IS THE TOPIC WE WILL PUBLISH TO
        //__________________________________________________________________________________________









        //_________________________________________________________________________________________
        // Set button clickers.
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(item_select.this, MainActivity.class);
                startActivity(intent); // Once the back button is pressed, go back to the Main Activity.
            }
        });

        openWindow.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //TODO: SEND THIS DATA TO THE TOPIC TO OPEN THE WINDOW!
                String command = "Open Window";
                Toast.makeText(item_select.this, command, Toast.LENGTH_SHORT).show();

            }
        });

        crackWindow.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //TODO: SEND THIS DATA TO THE TOPIC TO OPEN THE WINDOW!
                String command = "Crack Window";
                Toast.makeText(item_select.this, command, Toast.LENGTH_SHORT).show();

            }
        });

        closeWindow.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //TODO: SEND THIS DATA TO THE TOPIC TO OPEN THE WINDOW!
                String command = "Close Window";
                Toast.makeText(item_select.this, command, Toast.LENGTH_SHORT).show();

            }
        });

    }
}