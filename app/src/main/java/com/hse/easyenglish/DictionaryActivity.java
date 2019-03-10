package com.hse.easyenglish;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DictionaryActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary);

        Button translate = findViewById(R.id.button_translate);
        final EditText enterText = findViewById(R.id.enter_word);
        translate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = enterText.getText().toString();
                Toast.makeText(DictionaryActivity.this, "Now I can not translate a word " + word + ". Sorry :(", Toast.LENGTH_LONG).show();
            }
        });
    }
}
