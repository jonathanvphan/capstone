package com.example.listening_eye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    EditText username, password;
    Button login, signup;
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        login = findViewById(R.id.login);
        signup = findViewById(R.id.signup);
        dbHelper = new DBHelper(this);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, signup.class);
                startActivity(intent);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = username.getText().toString();
                String passWord = password.getText().toString();
                if (userName.equals("") || passWord.equals("")) {
                    Toast.makeText(MainActivity.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkUser = dbHelper.checkUsername(userName);
                    if (!checkUser) {
                        Toast.makeText(MainActivity.this, "User does not exist, please sign up first.", Toast.LENGTH_SHORT).show();
                    } else {
                        Boolean authenticate = dbHelper.checkUsernamePassword(userName, passWord);
                        if (authenticate) {
                            Toast.makeText(MainActivity.this, "Logged in Successfully.", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ConversationMode.class);
                            // getApplicationContext()
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, "Username and password do not match.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

            }
        });
    }
}