package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;
import android.support.annotation.NonNull;

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

    /** Create GameController and init it. Init TranslateController. */
    public static void init(@NonNull Context context) {
        gameController = new GameController(context);
        gameController.init();
        TranslateController.init(context);
    }
}
