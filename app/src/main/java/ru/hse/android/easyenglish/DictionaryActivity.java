package ru.hse.android.easyenglish;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class DictionaryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dictionary);

        final TextView languageToText = findViewById(R.id.language_to_button);
        final TextView languageFromText = findViewById(R.id.language_from_button);
        Button swapLanguagesButton = findViewById(R.id.language_swap_button);
        swapLanguagesButton.setOnClickListener(v -> {
            CharSequence newLanguageTo = languageFromText.getText();
            CharSequence newLanguageFrom = languageToText.getText();
            languageToText.setText(newLanguageTo);
            languageFromText.setText(newLanguageFrom);
        });

        final TextView translateResultText = findViewById(R.id.translation_text);
        final TextView yandexText = findViewById(R.id.yandex_text);
        final EditText enterText = findViewById(R.id.enter_word);
        Button translateButton = findViewById(R.id.translate_button);
        translateButton.setOnClickListener(v -> {
            String word = enterText.getText().toString();
            String languageFrom = languageFromText.getText().toString();
            String languageTo = languageToText.getText().toString();
            String languagePair;
            if (languageFrom.equals("RU") && languageTo.equals("EN")) {
                languagePair = "ru-en";
            } else {
                languagePair = "en-ru";
            }
            String translation = TranslateController.translate(word, languagePair);
            translateResultText.setText(translation);
            yandexText.setText("translated by Yandex.translate\n http://translate.yandex.ru/");
        });
    }
}
