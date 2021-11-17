package com.example.listening_eye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class Menu extends AppCompatActivity {

    Button btSetup, enterApp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        btSetup = findViewById(R.id.btSetup);
        enterApp = findViewById(R.id.features);

        btSetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, Bluetooth.class);
                startActivity(intent);
            }
        });

        enterApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Menu.this, ConversationMode.class);
                // getApplicationContext()
                startActivity(intent);
            }
        });
    }
}