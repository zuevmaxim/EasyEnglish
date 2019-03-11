package ru.hse.android.easyenglish;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;

class TranslateController {
    private static final Translate translate = TranslateOptions.getDefaultInstance().getService();

    static String translateEnglishToRussian(String word) {
        Translation translation = translate.translate(word,
                Translate.TranslateOption.sourceLanguage("en"),
                Translate.TranslateOption.sourceLanguage("ru"));
        return translation.getTranslatedText();
    }
}
