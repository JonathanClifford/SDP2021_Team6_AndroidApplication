package com.example.activewindows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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

import com.google.android.material.slider.Slider;
import android.os.Handler; // periodic task for checking for updates from AWS every few secs.


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
    TextView currentStatus;
    TextView latestMessage;
    Button checkStatus;

    Slider messageSlider;
    Button sendCommand;
    // CREATE INFORMATION FOR AWS.
    //______________________________________________________________________________________________
    // AWS - for AWS Thing (ActiveWindowsAndroidApp thing)
    private static final String CUSTOMER_SPECIFIC_ENDPOINT = "aga9o21zvh26x-ats.iot.us-east-1.amazonaws.com";
    // For the COGNITO roles that we are using (Cognito_ActiveWindowsIoTAuth_Role / unauth)
    private static final String COGNITO_POOL_ID = "us-east-1:a2ebd097-89fc-44a3-9cda-c0dcdac34457";
    // AWS Region
    private static final Regions MY_REGION = Regions.US_EAST_1;
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
        currentStatus = (TextView) findViewById(R.id.currentStatusView2);
        latestMessage = (TextView) findViewById(R.id.latestMessage);
        checkStatus = findViewById(R.id.statusBelowMe); // button
        //checkStatus.setOnClickListener(subscribeClick); //call status DEPRECATED


        messageSlider = findViewById(R.id.slider); //steps of 5 for commands to the window.
        sendCommand = findViewById(R.id.sendSliderCommandButton); //AWS sends command to button
        sendCommand.setOnClickListener(pubSendCommand); //Call Clicker



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
        //__________________________________________________________________________________________
        //__________________________________________________________________________________________

        // Window commands are of two specific varities for format, always having 2 colons for
        // Delimitters between the messages
        // WINDOWX:COMMAND:PERCENT
        // Our case we have two commands that we are sending to the NMC:
        // Window1:GetStatus:0
        // Window1:Operate:100.0 where the last val is the percent.



        //__________________________________________________________________________________________
        // Run a periodic task so that the status will be auto updated without the users intervention
        final Handler autoCheck = new Handler();

        Runnable autoCheckStatus = new Runnable() {
            @Override
            public void run() {
                final String topic_sub = "windowStatusTopic"; //THIS IS THE TOPIC WE WILL SUBSCRIBE TO

                Log.d(LOG_TAG, "topic = " + topic_sub);

                try {
                    mqttManager.subscribeToTopic(topic_sub, AWSIotMqttQos.QOS0,
                            new AWSIotMqttNewMessageCallback() {
                                @Override
                                public void onMessageArrived(final String topic, final byte[] data) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                String message = new String(data, "UTF-8");
                                                Log.d(LOG_TAG, "Message arrived:");
                                                Log.d(LOG_TAG, "   Topic: " + topic);
                                                Log.d(LOG_TAG, " Message: " + message);

                                                latestMessage.setText(message);

                                            } catch (UnsupportedEncodingException e) {
                                                Log.e(LOG_TAG, "Message encoding error.", e);
                                            }
                                        }
                                    });
                                }
                            });
                } catch (Exception e) {
                    Log.e(LOG_TAG, "Subscription error.", e);
                }
                autoCheck.postDelayed(this, 2000);
            }

        };
        autoCheck.post(autoCheckStatus);


        //__________________________________________________________________________________________

        // First, send an immediate message to AWS requesting a current Status update
        initializeStatusUpdate();

        // Back Button Command
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(item_select.this, MainActivity.class);
                startActivity(intent); // Once the back button is pressed, go back to the Main Activity.
            }
        });




    }

    // _____________________________________________________________________________________________
    // This runs on startup, basically it'll request the NMC to send an immediate status update.
    private void initializeStatusUpdate() {
        try {
            Thread.sleep(700); // Put this here because it needs some sort of a delay.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        final String topic_pub = "windowCommandTopic";
        final String msg = "Window #1:GetStatus:0.0"; //Send an immediate status update so the user can see whats going on

        try {
            mqttManager.publishString(msg, topic_pub, AWSIotMqttQos.QOS0);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Publish error.", e);
        }

    }

    // THIS IS DEPRECATED! I AM ONLY KEEPING THIS HERE FR REFERENCE! THE ABOVE HANDLER FIXES THIS
    // PROBLEM ENTIRELY!
    //__________________________________________________________________________________________
    // Subscribe to the topic
//    View.OnClickListener subscribeClick = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            final String topic_sub = "windowStatusTopic"; //THIS IS THE TOPIC WE WILL SUBSCRIBE TO
//
//            Log.d(LOG_TAG, "topic = " + topic_sub);
//
//            try {
//                mqttManager.subscribeToTopic(topic_sub, AWSIotMqttQos.QOS0,
//                        new AWSIotMqttNewMessageCallback() {
//                            @Override
//                            public void onMessageArrived(final String topic, final byte[] data) {
//                                runOnUiThread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        try {
//                                            String message = new String(data, "UTF-8");
//                                            Log.d(LOG_TAG, "Message arrived:");
//                                            Log.d(LOG_TAG, "   Topic: " + topic);
//                                            Log.d(LOG_TAG, " Message: " + message);
//
//                                            latestMessage.setText(message);
//
//                                        } catch (UnsupportedEncodingException e) {
//                                            Log.e(LOG_TAG, "Message encoding error.", e);
//                                        }
//                                    }
//                                });
//                            }
//                        });
//            } catch (Exception e) {
//                Log.e(LOG_TAG, "Subscription error.", e);
//            }
//        }
//    };

    // to send the command to AWS:
    View.OnClickListener pubSendCommand = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            final String topic_pub = "windowCommandTopic";
            final float currentPercentage = messageSlider.getValue();
            String percentageString = null;

            if (currentPercentage != 0){
                percentageString = Float.toString(currentPercentage);
            }
            else {
                percentageString = "0.0";
            }

            final String msg = "Window1:" + "Operate:" + percentageString; //% open

            try {
                mqttManager.publishString(msg, topic_pub, AWSIotMqttQos.QOS0);
            } catch (Exception e) {
                Log.e(LOG_TAG, "Publish error.", e);
            }

        }
    };




}