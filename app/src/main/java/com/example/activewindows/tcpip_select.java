package com.example.activewindows;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
                    //Replace below IP with the IP of that device in which server socket open.
                    //If you change port then change the port number in the server side code also.
                    Socket s = new Socket("192.168.2.36", 9002); // This socket needs to
                    // be changed on every use primarily because of stupid shit

                    OutputStream out = s.getOutputStream();

                    PrintWriter output = new PrintWriter(out);

                    output.println(msg);
                    output.flush();
                    BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));
                    final String st = input.readLine();

                    handler.post(new Runnable() {
                        @Override
                        public void run() {

                            String s = latestMessage.getText().toString();
                            if (st.trim().length() != 0)
                                latestMessage.setText(s + "\nFrom Server : " + st);
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