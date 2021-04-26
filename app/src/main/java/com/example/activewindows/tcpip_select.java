package com.example.activewindows;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Looper;
import android.text.format.Formatter;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import java.util.Timer;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.UUID;

import com.amazonaws.mobileconnectors.iot.AWSIotMqttQos;
import com.google.android.material.slider.Slider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View; // test commit
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;


// This TCP/IP Protcol could would not have been possible without the immense help of
// Girish Bhalerao https://stackoverflow.com/questions/7384678/how-to-create-socket-connection-in-android
public class tcpip_select extends AppCompatActivity implements View.OnClickListener {
    Button back; // I have a back button instead of re-implementing the spinner in this class.
    // It would be straightforward to implement but for testing this is the most straightforward.

    // Select all of the buttons and instantiate them as existing.
    TextView currentStatus;
    TextView latestMessage;
    Button checkStatus;

    Slider messageSlider;
    Button sendCommand;

    // Images
    ImageView checkMarkStatus;
    ImageView percent0;
    ImageView percent10;
    ImageView percent20;
    ImageView percent30;
    ImageView percent40;
    ImageView percent50;
    ImageView percent60;
    ImageView percent70;
    ImageView percent80;
    ImageView percent90;
    ImageView percent100;
    public boolean isFirstMessage = true;
    public String[] command = {"",""};
    public String[] status;
    public ServerSocket ss;
    public Socket s;
    TextView connected;
    PrintWriter sockOutput;
    BufferedReader sockInput;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tcpip_select);

        // I am basing a lot of the code here on the item_select class as well and modifying it
        // so that it can eventually utilize the TCP/IP protocol
        back = findViewById(R.id.TestButton);
        currentStatus = (TextView) findViewById(R.id.currentStatusView2);
        latestMessage = (TextView) findViewById(R.id.latestMessage);
        checkStatus = findViewById(R.id.statusBelowMe); // button
        connected = findViewById(R.id.connected); //checks socket connection

        messageSlider = findViewById(R.id.slider); //steps of 5 for commands to the window.
        sendCommand = findViewById(R.id.sendSliderCommandButton); //AWS sends command to button

        // imageViews for status
        checkMarkStatus = findViewById(R.id.checkmarkgif);
        percent0 = findViewById(R.id.percent0open);
        percent10 = findViewById(R.id.percent10open);
        percent20 = findViewById(R.id.percent20open);
        percent30 = findViewById(R.id.percent30open);
        percent40 = findViewById(R.id.percent40open);
        percent50 = findViewById(R.id.percent50open);
        percent60 = findViewById(R.id.percent60open);
        percent70 = findViewById(R.id.percent70open);
        percent80 = findViewById(R.id.percent80open);
        percent90 = findViewById(R.id.percent90open);
        percent100 = findViewById(R.id.percent100open);
        // ____________________________________________
        // Send an immediate get status command upon starting the task.
        //initializeStatusUpdate();

        /* ~Opens socket on startUp~ */
        startServerSocket();

        // go back to the main function and close socket
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(s!= null) {
                    //tell socket to close
                    //sockOutput.write("#\0");
                    //sockOutput.flush();
                    command[0] = "#\0";

                    //sockOutput.close();
                    //s.close();
                    //ss.close();
                    //ss.close();
                }

                Intent intent = new Intent(tcpip_select.this, MainActivity.class);
                startActivity(intent); // Once the back button is pressed, go back to the Main Activity.
            }
        });


        sendCommand.setOnClickListener(this); // set this to be the on click listener that will send
        // the TCP IP message

    }

    @Override
    public void onClick(View v) { // for the actual sending of commands
        //
        //switch (v.getId()) {
        //    case R.id.sendSliderCommandButton:

        final float currentPercentage = messageSlider.getValue();
        String percentageString = null;

        if (currentPercentage != 0){
            percentageString = Float.toString(currentPercentage);
        }
        else {
            percentageString = "0.0";
        }

        String msgCommand = "Window:" + "Operate:" + percentageString +"\0"; //% open

        String curMsg = (String) latestMessage.getText();
        String temp = "Message sent. Waiting on reply.";

        //if (curMsg.equals("Message sent. Waiting on reply.")) {
        //    Toast.makeText(tcpip_select.this, "Waiting on previous command. Did not send message.", Toast.LENGTH_SHORT).show(); // appear on bottom
        //}
        //else {
        Toast.makeText(tcpip_select.this, msgCommand, Toast.LENGTH_SHORT).show(); // appear on bottom
        latestMessage.setText(temp); //transient case, update the status view to let user know that msg sent.
        checkMarkStatus.setVisibility(View.INVISIBLE);
        percent0.setVisibility(View.INVISIBLE);
        percent10.setVisibility(View.INVISIBLE);
        percent20.setVisibility(View.INVISIBLE);
        percent30.setVisibility(View.INVISIBLE);
        percent40.setVisibility(View.INVISIBLE);
        percent50.setVisibility(View.INVISIBLE);
        percent60.setVisibility(View.INVISIBLE);
        percent70.setVisibility(View.INVISIBLE);
        percent80.setVisibility(View.INVISIBLE);
        percent90.setVisibility(View.INVISIBLE);
        percent100.setVisibility(View.INVISIBLE);
        command[0] = msgCommand;
        //}


        //break;
        //}
    }
    //Remove these
    /*
    private void sendMessage(final String msg) {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable()

        {
            @Override
            public void run()
            {

                try {
                    ServerSocket ss = new ServerSocket(9002);
                    boolean end = false;
                    while(!end) {
                        String stringData = null;
                        Socket s = ss.accept();

                        PrintWriter output = new PrintWriter(s.getOutputStream());

                        output.write(msg, 0, msg.length());
                        OutputStream out = s.getOutputStream();


                        output.println(msg);
                        output.flush();

                        output.close();
                        s.close();
                        listenMessage();
                        end = true;
                    }
                    ss.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private void listenMessage() {

        final Handler handler = new Handler();
        final Thread thread = new Thread(new Runnable()

        {
            @Override
            public void run()
            {

                try {

                    try {
                        Thread.sleep(1000);
                    }catch (InterruptedException e){
                        e.getCause();
                    }
                    ServerSocket ss = new ServerSocket(9002);
                    boolean end2 = false;

                    while(!end2) {
                        Socket s = ss.accept();
                        BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

                        //BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                        final String st = input.readLine();

                        handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (st.trim().length() != 0)
                                // TODO THIS IS UNTESTED CODE, AND REQUIRES THE NMC TO WORK PROPERLY
                                // WITH TCP/IP
                                if (st.contains("ACK")) {
                                    String curMsg = "Window standing by.";
                                    checkMarkStatus.setVisibility(View.VISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);

                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"0\"}")) {
                                    String curMsg = "Window is fully closed.";
                                    percent0.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"10\"}")) {
                                    String curMsg = "Window is 10% open.";
                                    percent10.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"20\"}")) {
                                    String curMsg = "Window is 20% open.";
                                    percent20.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"30\"}")) {
                                    String curMsg = "Window is 30% open.";
                                    percent30.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"40\"}")) {
                                    String curMsg = "Window is 40% open.";
                                    percent40.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"50\"}")) {
                                    String curMsg = "Window is 50% open.";
                                    percent50.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"60\"}")) {
                                    String curMsg = "Window is 60% open.";
                                    percent60.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"70\"}")) {
                                    String curMsg = "Window is 70% open.";
                                    percent70.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"80\"}")) {
                                    String curMsg = "Window is 80% open.";
                                    percent80.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"90\"}")) {
                                    String curMsg = "Window is 90% open.";
                                    percent90.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    percent100.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else if (st.equals("{\"ID\": \"Window\",\"Operate\": \"100\"}")) {
                                    String curMsg = "Window is fully open.";
                                    percent100.setVisibility(View.VISIBLE);

                                    checkMarkStatus.setVisibility(View.INVISIBLE);
                                    percent10.setVisibility(View.INVISIBLE);
                                    percent20.setVisibility(View.INVISIBLE);
                                    percent30.setVisibility(View.INVISIBLE);
                                    percent40.setVisibility(View.INVISIBLE);
                                    percent50.setVisibility(View.INVISIBLE);
                                    percent60.setVisibility(View.INVISIBLE);
                                    percent70.setVisibility(View.INVISIBLE);
                                    percent80.setVisibility(View.INVISIBLE);
                                    percent90.setVisibility(View.INVISIBLE);
                                    percent0.setVisibility(View.INVISIBLE);
                                    latestMessage.setText(curMsg);
                                } else {
                                    latestMessage.setText(st); // failsafe case, unexpected msg
                                }

                            }
                        });

                    input.close();
                    s.close();
                    end2 = true;
                    }

                } catch (IOException e) {
                        e.printStackTrace();
                }
            }
        });
        thread.start();
    }
    private void initializeStatusUpdate() {
        try {
            Thread.sleep(700); // Put this here because it needs some sort of a delay.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        if (isFirstMessage) {
            isFirstMessage = false;

            WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
            String ipAddress1 = Formatter.formatIpAddress(wifiManager.getConnectionInfo().getIpAddress());

            String portNumber = "9002"; // Apparantly default port number for TCP/IP is 8080
            String androidAppNetInfo = "IP:"+ ipAddress1 + "::" + portNumber+ "\0";
            sendMessage(androidAppNetInfo);
            //IP test is delimeter or ignore blocking
        }

        // SLEEP FOR 1000 MS WHILE NMC RECEIVES IP ADDRESS

        try {
            Thread.sleep(6000); // Put this here because it needs some sort of a delay.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        //  SEND IMMEDIATE REQUEST FOR STATUS UPDATE

        if (isFirstMessage == false) {
            String msg = "Window:GetStatus:0.0";
            sendMessage(msg); //Send an immediate status update so the user can see whats going on
        }

    }
    */

    private void updateVisuals(String[] msg,int flag){

        final String val = msg[3];
        final String func = msg[2];
        final String ID = msg[1];
        final String ACK = msg[1];

        final int connection = flag;

        boolean handler1 = new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (connection==1) {
                    connected.setText("TCP_IP Connected");
                }
                else if (connection ==2){
                    connected.setText("TCP_IP Disconnected");
                }
                else if (func.equals("ACK")) {
                    String curMsg = "Window standing by.";
                    checkMarkStatus.setVisibility(View.VISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);

                }
                else if (val.equals("0")) {
                    String curMsg = "Window is fully closed.";
                    percent0.setVisibility(View.VISIBLE);
                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("10")) {
                    String curMsg = "Window is 10% open.";
                    percent10.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("20")) {
                    String curMsg = "Window is 20% open.";
                    percent20.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("30")) {
                    String curMsg = "Window is 30% open.";
                    percent30.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("40")) {
                    String curMsg = "Window is 40% open.";
                    percent40.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("50")) {
                    String curMsg = "Window is 50% open.";
                    percent50.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("60")) {
                    String curMsg = "Window is 60% open.";
                    percent60.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("70")) {
                    String curMsg = "Window is 70% open.";
                    percent70.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("80")) {
                    String curMsg = "Window is 80% open.";
                    percent80.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("90")) {
                    String curMsg = "Window is 90% open.";
                    percent90.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    percent100.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else if (val.equals("100")) {
                    String curMsg = "Window is fully open.";
                    percent100.setVisibility(View.VISIBLE);

                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    latestMessage.setText(curMsg);
                }
                else {// failsafe case, unexpected msg

                    percent100.setVisibility(View.INVISIBLE);
                    checkMarkStatus.setVisibility(View.INVISIBLE);
                    percent10.setVisibility(View.INVISIBLE);
                    percent20.setVisibility(View.INVISIBLE);
                    percent30.setVisibility(View.INVISIBLE);
                    percent40.setVisibility(View.INVISIBLE);
                    percent50.setVisibility(View.INVISIBLE);
                    percent60.setVisibility(View.INVISIBLE);
                    percent70.setVisibility(View.INVISIBLE);
                    percent80.setVisibility(View.INVISIBLE);
                    percent90.setVisibility(View.INVISIBLE);
                    percent0.setVisibility(View.INVISIBLE);
                    latestMessage.setText(val);
                }

            }
        },3000);
    }

    public void startServerSocket() {
        try {
            Thread.sleep(700); // Put this here because it needs some sort of a delay.
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }

        Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    boolean end = false;
                    ss = new ServerSocket(9002);

                    /* Thread waits for a connection*/
                    s = ss.accept();
                    s.setSoTimeout(300);

                    sockInput = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    sockOutput = new PrintWriter(s.getOutputStream());

                    String stringData = "";
                    String[] tempArr = {"", "", "","",""};

                    /* Updates connection visuals   */
                    updateVisuals(tempArr, 1);

                    float count =0;
                    float count2 = 0;
                    //sockOutput.write("Get Status\n");

                    /*===========Socket Main===============*/
                    while (!end) {
                        count +=1;

                        /*=============Socket TimeOut Check ===============
                        if(count == 2000000) {
                            count2+=1;
                            count = 0;
                            if(count2 == 300) {
                                updateVisuals(tempArr, 2);
                                //end = true;
                                //break;
                            }
                        }===============================================*/

                        /* ================= Read Socket =================*/
                        if (sockInput.ready()) {
                            try {
                                stringData = sockInput.readLine();
                            }catch (SocketTimeoutException e){
                                e.getMessage();
                            }
                            //status[shift] = stringData;
                            //shift += 1;
                        }/*================================================*/

                        /*================  Input Checker  ================*/
                        if(stringData!= null) {
                            if (stringData.contains("Status") | stringData.contains("ACK")) {
                                count = 0;
                                //stringData = stringData.substring(1, stringData.length() - 1);
                                status = stringData.split(":");
                                updateVisuals(status,0);
                                stringData=null;
                                continue;
                            }
                            switch (stringData) {

                                case "#":       /* NMC closed socket */
                                    count = 0;
                                    updateVisuals(tempArr,2);
                                    end = true;
                                    break;

                                case "$":       /* Connection check */
                                    count = 0;
                                    sockOutput.write("$\0");
                                    sockOutput.flush();
                            }
                        }/*====================================================*/

                        /*================== APP Command Check ================*/
                        if (!command[0].equals("")) {
                            if (command[0].equals("#\0")){ end=true; }
                            sockOutput.write(command[0]);
                            sockOutput.flush();
                            command[0] = "";
                        }/*================================================*/
                        stringData = null;

                    }
                    sockOutput.close();
                    s.close();
                    ss.close();

                } catch(IOException e){
                    e.printStackTrace();
                }
            }

        });
        thread.start();
    }

}