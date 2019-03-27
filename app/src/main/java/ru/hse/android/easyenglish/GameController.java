package ru.hse.android.easyenglish;

import android.util.Log;

public class GameController {
    GameController() {

    }

    private static final WordFactory wordFactory = new WordFactory();

    public void saveWordResult(String word, boolean result) {
        Log.d("save result", word + " : " + result);
    }

    public WordFactory getWordFactory() {
        return wordFactory;
    }
}
