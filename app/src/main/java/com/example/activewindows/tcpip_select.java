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

public class tcpip_select extends AppCompatActivity {
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

        //TODO
        // Below here: Implement the same *basic* idea as before with the publishing & subscribe
        // Topics, but with TCP/IP protocol

    }
}