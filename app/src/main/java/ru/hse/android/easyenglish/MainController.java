package ru.hse.android.easyenglish;

public class MainController {
    private static GameController gameController;

    private MainController() { }

    public static GameController getGameController() {
        return gameController;
    }

    static void init() {
        gameController = new GameController();
    }
}
