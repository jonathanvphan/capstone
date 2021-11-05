package com.example.listening_eye;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class signup extends AppCompatActivity {
    EditText usernamereg, passwordreg, re_enter;
    Button register, back;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        usernamereg = findViewById(R.id.usernamereg);
        passwordreg = findViewById(R.id.passwordreg);
        re_enter = findViewById(R.id.repass);
        register = findViewById(R.id.register);
        back = findViewById(R.id.back);
        dbHelper = new DBHelper(this);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(signup.this,MainActivity.class);
                // getApplicationContext()
                startActivity(intent);
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernamereg.getText().toString();
                String password = passwordreg.getText().toString();
                String reenter = re_enter.getText().toString();
                if(username.equals("") || password.equals("") || reenter.equals("")) {
                    Toast.makeText(signup.this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                } else if(!password.equals(reenter)){
                    Toast.makeText(signup.this, "Two passwords do not match.", Toast.LENGTH_SHORT).show();
                } else {
                    Boolean checkName = dbHelper.checkUsername(username);
                    if (checkName) {
                        Toast.makeText(signup.this, "User Exists! Please return to Log In page.", Toast.LENGTH_SHORT).show();
                    } else {
                        Boolean createNew = dbHelper.insertNew(username, password);
                        if (createNew) {
                            Toast.makeText(signup.this, "Registered Successfully. Please sign in.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(signup.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}