package ru.hse.android.easyenglish;

import android.content.Context;

public class GameController {
    GameController(Context context) {
        wordFactory = new WordFactory(context);
        wordListController = new WordListController(context);
        wordStorage = new WordStorage();
    }

    void init(Context context) {
        wordStorage.updateStorage(context);
    }

    private final WordStorage wordStorage;
    private final WordFactory wordFactory;
    private final WordListController wordListController;

    public void saveWordResult(String word, boolean result) {
        wordFactory.saveWordStatistic(word, result);
    }

    public WordFactory getWordFactory() {
        return wordFactory;
    }

    public WordListController getWordListController(Context context) {
        return wordListController;
    }

    public WordStorage getWordStorage() {
        return wordStorage;
    }
}
