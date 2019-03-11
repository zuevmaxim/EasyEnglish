package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class DictionaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Button translateButton = findViewById(R.id.translate_button);
        final TextView translateResult = findViewById(R.id.translation_text);
        final EditText enterText = findViewById(R.id.enter_word);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = enterText.getText().toString();
                //String translation = TranslateController.translateEnglishToRussian(word);
                //translateResult.setText(translation);
                Toast.makeText(DictionaryActivity.this, "Now I can not translate a word " + word + ". Sorry :(", Toast.LENGTH_LONG).show();
            }
        });
    }
}
