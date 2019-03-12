package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class DictionaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        Button translateButton = findViewById(R.id.translate_button);
        final TextView translateResult = findViewById(R.id.translation_text);
        final TextView yandexText = findViewById(R.id.yandex_text);
        final EditText enterText = findViewById(R.id.enter_word);
        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String word = enterText.getText().toString();
                String translation = TranslateController.translate(word);
                translateResult.setText(translation);
                yandexText.setText("translated by Yandex.translate\n http://translate.yandex.ru/");
            }
        });
    }
}
