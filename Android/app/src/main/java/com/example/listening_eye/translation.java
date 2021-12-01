package com.example.listening_eye;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.TextView;

public class translation extends AppCompatActivity {

    private translationViewModel viewModel;
    Button clickDisplayButton;
    String resultText = "";

    public static final String LAST_TEXT = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);
        TextView translation_text = findViewById(R.id.translation_text);
        viewModel = new ViewModelProvider(this).get(translationViewModel.class);

        viewModel.getSelectedName().observe(this, item -> {
            for(int i = 0; i < item.size(); i++) {
                resultText = resultText.concat(item.get(i));
            }
            translation_text.setText(resultText);
            //resultText = item;
        });

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        translation_text.setText(pref.getString(LAST_TEXT, ""));
        translation_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                pref.edit().putString(LAST_TEXT, s.toString()).commit();
            }
        });
    }
}