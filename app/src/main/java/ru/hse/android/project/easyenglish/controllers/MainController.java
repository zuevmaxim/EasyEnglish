package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;

/**
 * MainController contains GameController.
 * Should be static in oder to have an ability to reach it simply from everywhere.
 */
public class MainController {
    private static GameController gameController;

    private MainController() { }

    public static GameController getGameController() {
        return gameController;
    }

    /** Create GameController and init it. */
    public static void init(Context context) {
        gameController = new GameController(context);
        gameController.init();
    }
}
