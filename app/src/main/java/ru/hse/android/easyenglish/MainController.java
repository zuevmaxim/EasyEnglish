package ru.hse.android.easyenglish;

import android.content.Context;

public class MainController {
    private static GameController gameController;

    private MainController() { }

    public static GameController getGameController() {
        return gameController;
    }

    static void init(Context context) {
        gameController = new GameController(context);
        gameController.init();
    }
}
