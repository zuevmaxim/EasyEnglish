package ru.hse.android.project.easyenglish;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.hse.android.project.easyenglish.controllers.TranslateController;

/**
 * Application English-Russian and Russian-English translator.
 * Get all possible translations and synonyms from Yandex translator.
 */
public class DictionaryActivity extends AppCompatActivity {

    /** Get word, ask TranslateController for translation and show result. */
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
            TranslateController.DicResult result = TranslateController.translateTotal(word, languagePair);
            if (result == null) {
                new AlertDialog.Builder(this)
                        .setMessage(this.getString(R.string.check_internet_connect))
                        .setNeutralButton(android.R.string.ok, null)
                        .show();
            } else {
                StringBuilder builder = new StringBuilder();
                if (result.def != null) {
                    for (TranslateController.DicResult.Definition definition : result.def) {
                        if (definition != null && definition.text != null) {
                            builder.append(definition.text);
                            if (definition.ts != null) {
                                builder.append("\t\t\t[").append(definition.ts).append("]");
                            }
                            if (definition.pos != null) {
                                builder.append("\t\t\t(").append(definition.pos).append(")");
                            }
                            builder.append("\n");
                            if (definition.tr != null) {
                                for (TranslateController.DicResult.Translation translation : definition.tr) {
                                    if (translation != null && translation.text != null) {
                                        builder.append("\t\t-").append(translation.text);
                                        if (translation.pos != null) {
                                            builder.append("\t\t\t(").append(translation.pos).append(")").append("\n");
                                        }
                                        if (translation.mean != null) {
                                            for (TranslateController.DicResult.Meaning meaning : translation.mean) {
                                                if (meaning != null && meaning.text != null) {
                                                    builder.append("\t\t\t\t-").append(meaning.text).append("\n");
                                                }
                                            }
                                        }
                                    }
                                }
                                builder.append("\n");
                            }
                        }
                        builder.append("\n");
                    }
                }
                translateResultText.setMovementMethod(new ScrollingMovementMethod());
                translateResultText.setText(builder.toString());
                yandexText.setText("«Реализовано с помощью сервиса «Яндекс.Словарь»\nhttps://tech.yandex.ru/dictionary/");
            }
        });
    }
}
