package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;

public class MainController {
    private static GameController gameController;

    private MainController() { }

    public static GameController getGameController() {
        return gameController;
    }

    public static void init(Context context) {
        gameController = new GameController(context);
        gameController.init();
    }
}
