package com.example.activewindows;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.util.UUID;
import com.google.android.material.slider.Slider;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
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


        messageSlider = findViewById(R.id.slider); //steps of 5 for commands to the window.
        sendCommand = findViewById(R.id.sendSliderCommandButton); //AWS sends command to button


        // imageViews for status
        checkMarkStatus = findViewById(R.id.ackStatusImage);
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


        // go back to the main function
        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(tcpip_select.this, MainActivity.class);
                startActivity(intent); // Once the back button is pressed, go back to the Main Activity.
            }
        });

        sendCommand.setOnClickListener(this); // set this to be the on click listener that will send
        // the TCP IP message

    }

    @Override
    public void onClick(View v) { // for the actual sending of commands
        switch (v.getId()) {
            case R.id.sendSliderCommandButton:

                final float currentPercentage = messageSlider.getValue();
                String percentageString = null;

                if (currentPercentage != 0){
                    percentageString = Float.toString(currentPercentage);
                }
                else {
                    percentageString = "0.0";
                }

                final String msg = "Window:" + "Operate:" + percentageString; //% open
                Toast.makeText(tcpip_select.this, msg, Toast.LENGTH_SHORT).show(); // appear on bottom
                sendMessage(msg);


            break;
        }
    }

    private void sendMessage(final String msg) {

        final Handler handler = new Handler();
        Thread thread = new Thread(new Runnable()

        {
            @Override
            public void run()
            {
                try {
                    Socket s = new Socket("192.168.2.162", 9002); // TODO This socket needs to
                    // be changed on every use until we have a static IP address for NMC

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String st = input.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            if (st.trim().length() != 0)
                                // TODO THIS IS UNTESTED CODE, AND REQUIRES THE NMC TO WORK PROPERLY
                                // WITH TCP/IP
                                if (st.contains("ACK")) {
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

                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"0\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"10\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"20\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"40\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"40\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"50\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"60\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"70\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"80\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"90\"}")) {
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
                                }

                                if (st.equals("{\"ID\": \"Window\",\"Operate\": \"100\"}")) {
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
                                }


                                latestMessage.setText(st);
                        }
                    });

                    output.close();
                    out.close();
                    s.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }) ;
        thread.start();


    }
}