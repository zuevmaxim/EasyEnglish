package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.support.annotation.NonNull;

import ru.hse.android.project.easyenglish.R;

/**
 * MainController contains GameController.
 * Should be static in oder to have an ability to reach it simply from everywhere.
 */
public class MainController {

    private static GameController gameController;

    private MainController() { }

    @NonNull
    public static GameController getGameController() {
        return gameController;
    }

    /** Create GameController and init it. */
    public static void init(@NonNull Context context) {
        checkKeys(context);
        gameController = new GameController(context);
        gameController.init();
        TranslateController.init(context);
    }

    private static void checkKeys(@NonNull Context context) {
        if (context.getString(R.string.yandex_dictionary_key).startsWith("YOUR_")) {
            throw new RuntimeException(context.getString(R.string.dictionary_key_error));
        }
        if (context.getString(R.string.yandex_translate_key).startsWith("YOUR_")) {
            throw new RuntimeException(context.getString(R.string.translate_key_error));
        }
    }
}
