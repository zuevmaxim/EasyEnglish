package ru.hse.android.project.easyenglish.controllers;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.widget.Toast;

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
    public static void init(@NonNull Activity context) {
        try {
            TranslateController.init(context);
        } catch (IllegalStateException e) {
            Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
            context.finish();
        }
        gameController = new GameController(context);
        gameController.init();
    }
}
