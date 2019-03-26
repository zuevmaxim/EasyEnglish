package ru.hse.android.easyenglish;

public class GameController {
    private static final int GAME_CYCLE = 10;

    GameController() {

    }
    private static final WordFactory wordFactory = new WordFactory();

    public void runGameCycle(Class<?> game) {
        for (int i = 0; i < GAME_CYCLE; i++) {

        }
    }

    public WordFactory getWordFactory() {
        return wordFactory;
    }
}
