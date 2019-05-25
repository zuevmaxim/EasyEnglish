package ru.hse.android.project.easyenglish.controllers;

import android.content.Context;

import ru.hse.android.project.easyenglish.words.Word;
import ru.hse.android.project.easyenglish.words.WordFactory;

public class GameController {
    GameController(Context context) {
        wordFactory = new WordFactory(context);
        wordListController = new WordListController(context);
        wordStorage = new WordStorage();
    }

    void init() {
        wordStorage.updateStorage();
    }

    private final WordStorage wordStorage;
    private final WordFactory wordFactory;
    private final WordListController wordListController;

    public void saveWordResult(Word word, boolean result) {
        wordFactory.saveWordStatistic(word, result);
    }

    public void resetStatistics(Word word) {
        wordFactory.resetStatistics(word);
    }

    public WordFactory getWordFactory() {
        return wordFactory;
    }

    public WordListController getWordListController() {
        return wordListController;
    }

    public WordStorage getWordStorage() {
        return wordStorage;
    }
}
