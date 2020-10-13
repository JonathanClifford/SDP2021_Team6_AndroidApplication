package com.example.activewindows;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_select);

        back = findViewById(R.id.TestButton);

        back.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(item_select.this, MainActivity.class);
                startActivity(intent); // Once the back button is pressed, go back to the Main Activity.
            }
        });


    }
}