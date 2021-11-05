package com.example.listening_eye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ConversationMode extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation_mode);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        NavController navController = Navigation.findNavController(this, R.id.fragment);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.firstFragment, R.id.secondFragment, R.id.thirdFragment).build();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        //TextView frag1_instru;
//        Button viewConversionButton;
//        Button viewTranslationButton;

        //frag1_instru = findViewById(R.id.frag1_instruction);
//        viewConversionButton = findViewById(R.id.view_conversion_history_button);
//        viewTranslationButton = (Button) findViewById(R.id.view_record_button);

//        viewConversionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //frag1_instru.setText("   Text converted from Google API");
//                Intent intent = new Intent(ConversationMode.this, voice2text.class);
//                startActivity(intent);
//
//            }
//        });

//        viewTranslationButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(ConversationMode.this, translation.class);
//                startActivity(intent);
//
//            }
//        });

    }
}