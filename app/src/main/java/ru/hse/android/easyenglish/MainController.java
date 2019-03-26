package ru.hse.android.easyenglish;

public class MainController {
    private static final GameController gameController = new GameController();

    private MainController() { }

    public static GameController getGameController() {
        return gameController;
    }

    static void init() {

    }
}
