package ru.hse.android.project.easyenglish.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import ru.hse.android.project.easyenglish.R;
import ru.hse.android.project.easyenglish.controllers.TranslateController;

import static ru.hse.android.project.easyenglish.controllers.TranslateController.*;
import static ru.hse.android.project.easyenglish.controllers.TranslateController.TranslateDirection.EN_RU;
import static ru.hse.android.project.easyenglish.controllers.TranslateController.TranslateDirection.RU_EN;

/**
 * Application English-Russian and Russian-English translator.
 * Get all possible translations and synonyms from Yandex translator.
 */
public class DictionaryActivity extends AppCompatActivity {

    /** Direction to translate word(True - from russian to english, False - visa versa). */
    private boolean translationDirection = true;

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
            translationDirection = !translationDirection;
        });
        Button translateButton = findViewById(R.id.translate_button);
        translateButton.setOnClickListener(v -> setTranslationInfo());

        final TextView yandexText = findViewById(R.id.yandex_text);
        yandexText.setText(R.string.yandex_info_text);
    }

    /** Show translation info fro request. */
    private void setTranslationInfo() {
        final TextView translateResultText = findViewById(R.id.translation_text);
        final EditText enterText = findViewById(R.id.enter_word);
        String word = enterText.getText().toString();
        TranslateDirection languagePair = translationDirection ? RU_EN : EN_RU;
        DicResult result = TranslateController.translateTotal(word, languagePair);
        if (result == null) {
            new AlertDialog.Builder(this)
                    .setMessage(this.getString(R.string.check_internet_connect))
                    .setNeutralButton(android.R.string.ok, null)
                    .show();
        } else if (result.def != null) {
            translateResultText.setMovementMethod(new ScrollingMovementMethod());
            translateResultText.setText(getDefinitions(result));
        }
    }

    /** Show all definitions for requested word. */
    private String getDefinitions(DicResult result) {
        StringBuilder builder = new StringBuilder();
        for (DicResult.Definition definition : result.def) {
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
                    getTranslations(builder, definition);
                }
            }
            builder.append("\n");
        }
        return builder.toString();
    }

    /** Show all translations for definitions for requested word. */
    private void getTranslations(StringBuilder builder, DicResult.Definition definition) {
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
